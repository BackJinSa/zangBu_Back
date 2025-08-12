package bjs.zangbu.payment.controller;

import bjs.zangbu.config.TestConfig;
import bjs.zangbu.payment.mapper.PaymentMapper;
import bjs.zangbu.payment.service.PaymentService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringJUnitConfig(TestConfig.class)
@Transactional
@DisplayName("PaymentController MySQL 통합 테스트")
class PaymentControllerIntegrationTest {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentService paymentService;

    private PaymentController paymentController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);

        // 컨트롤러 설정
        paymentController = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .defaultRequest(get("/").characterEncoding("UTF-8"))
                .build();
        objectMapper = new ObjectMapper();

        // 테스트 데이터 초기화
        initTestData();
    }

    private void initTestData() {
        // 테스트 데이터 초기화 (외래키 제약조건 순서 고려)
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE download_history");
        jdbcTemplate.execute("TRUNCATE TABLE payment");
        jdbcTemplate.execute("TRUNCATE TABLE membership");
        jdbcTemplate.execute("TRUNCATE TABLE member_entitlement");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

        // 멤버 데이터
        jdbcTemplate.execute(
                """
                        INSERT INTO member (member_id, email, password, phone, nickname, identity, role, birth, name, consent, telecom) VALUES
                        ('test-member-1', 'test1@example.com', 'password123', '01012345678', '테스터1', '9001011234567', 'ROLE_MEMBER', '900101', '김테스트', true, 'SKT'),
                        ('test-member-2', 'test2@example.com', 'password123', '01087654321', '테스터2', '9002022345678', 'ROLE_MEMBER', '900202', '이테스트', true, 'KT'),
                        ('test-member-3', 'test3@example.com', 'password123', '01055555555', '테스터3', '9003033456789', 'ROLE_MEMBER', '900303', '박테스트', false, 'LGU+')
                        """);
    }

    @Test
    @DisplayName("POST /payment/confirm - MEMBERSHIP 결제 성공")
    void confirmPayment_membership() throws Exception {
        // Given
        Map<String, Object> payload = Map.of(
                "paymentKey", "pay_membership_001",
                "orderId", "order_membership_001",
                "amount", 9900,
                "productType", "MEMBERSHIP",
                "productId", "plan_standard",
                "method", "CARD",
                "pgPayload", "{\"raw\": \"toss_payment_data\"}");

        // When & Then
        mockMvc.perform(post("/payment/confirm")
                .requestAttr("memberId", "test-member-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // DB에 결제 정보가 저장되었는지 확인
        Long paymentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM payment WHERE member_id = 'test-member-1' AND product_type = 'MEMBERSHIP'",
                Long.class);
        assert paymentCount == 1L;

        // DB에 멤버십 정보가 저장되었는지 확인
        Long membershipCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membership WHERE member_id = 'test-member-1' AND status = 'ACTIVE'",
                Long.class);
        assert membershipCount == 1L;

        // 멤버십 유효기간 확인
        String nextBillingAt = jdbcTemplate.queryForObject(
                "SELECT DATE_FORMAT(next_billing_at, '%Y-%m-%d') FROM membership WHERE member_id = 'test-member-1'",
                String.class);
        assert nextBillingAt != null;
    }

    @Test
    @DisplayName("POST /payment/confirm - PER_CASE 결제 성공")
    void confirmPayment_perCase() throws Exception {
        // Given
        Map<String, Object> payload = Map.of(
                "paymentKey", "pay_percase_001",
                "orderId", "order_percase_001",
                "amount", 1900,
                "productType", "PER_CASE",
                "productId", "per_case_1",
                "method", "CARD",
                "pgPayload", "{\"raw\": \"toss_payment_data\"}");

        // When & Then
        mockMvc.perform(post("/payment/confirm")
                .requestAttr("memberId", "test-member-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // DB에 결제 정보가 저장되었는지 확인
        Long paymentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM payment WHERE member_id = 'test-member-2' AND product_type = 'PER_CASE'",
                Long.class);
        assert paymentCount == 1L;

        // DB에 건당 차감 잔액이 추가되었는지 확인
        Integer perCaseBalance = jdbcTemplate.queryForObject(
                "SELECT per_case_balance FROM member_entitlement WHERE member_id = 'test-member-2'",
                Integer.class);
        assert perCaseBalance == 1;
    }

    @Test
    @DisplayName("GET /payment/entitlements - 멤버십 활성 상태")
    void getEntitlements_membershipActive() throws Exception {
        // Given - 멤버십 활성화
        jdbcTemplate.execute(
                """
                        INSERT INTO membership (member_id, plan, status, start_at, next_billing_at, created_at) VALUES
                        ('test-member-1', 'STANDARD', 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW())
                        """);

        // When & Then
        mockMvc.perform(get("/payment/entitlements")
                .requestAttr("memberId", "test-member-1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membershipActive", is(1)))
                .andExpect(jsonPath("$.perCaseRemaining", is(0)));
    }

    @Test
    @DisplayName("GET /payment/entitlements - 건당 차감 잔액 존재")
    void getEntitlements_perCaseBalance() throws Exception {
        // Given - 건당 차감 잔액 설정
        jdbcTemplate.execute(
                """
                        INSERT INTO member_entitlement (member_id, per_case_balance, updated_at) VALUES
                        ('test-member-2', 5, NOW())
                        """);

        // When & Then
        mockMvc.perform(get("/payment/entitlements")
                .requestAttr("memberId", "test-member-2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membershipActive", is(0)))
                .andExpect(jsonPath("$.perCaseRemaining", is(5)));
    }

    @Test
    @DisplayName("POST /payment/consume - 건당 차감 성공")
    void consumePerCase_success() throws Exception {
        // Given - 건당 차감 잔액 설정
        jdbcTemplate.execute(
                """
                        INSERT INTO member_entitlement (member_id, per_case_balance, updated_at) VALUES
                        ('test-member-3', 3, NOW())
                        """);

        // When & Then
        mockMvc.perform(post("/payment/consume")
                .requestAttr("memberId", "test-member-3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // DB에서 잔액이 차감되었는지 확인
        Integer remainingBalance = jdbcTemplate.queryForObject(
                "SELECT per_case_balance FROM member_entitlement WHERE member_id = 'test-member-3'",
                Integer.class);
        assert remainingBalance == 2;
    }

    @Test
    @DisplayName("POST /payment/consume - 건당 차감 실패 (잔액 부족)")
    void consumePerCase_failure() throws Exception {
        // Given - 건당 차감 잔액이 0인 상태
        jdbcTemplate.execute(
                """
                        INSERT INTO member_entitlement (member_id, per_case_balance, updated_at) VALUES
                        ('test-member-1', 0, NOW())
                        """);

        // When & Then
        mockMvc.perform(post("/payment/consume")
                .requestAttr("memberId", "test-member-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}

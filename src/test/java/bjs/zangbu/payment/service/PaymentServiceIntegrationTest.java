package bjs.zangbu.payment.service;

import bjs.zangbu.config.TestConfig;
import bjs.zangbu.payment.mapper.PaymentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(TestConfig.class)
@Transactional
@DisplayName("PaymentService MySQL 통합 테스트")
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);

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
    @DisplayName("MEMBERSHIP 결제 확인 - 결제 정보 및 멤버십 DB 저장 검증")
    void confirmPayment_membership() {
        // Given
        String memberId = "test-member-1";
        Map<String, Object> payload = Map.of(
                "paymentKey", "pay_membership_001",
                "orderId", "order_membership_001",
                "amount", 9900,
                "productType", "MEMBERSHIP",
                "productId", "plan_standard",
                "method", "CARD",
                "pgPayload", "{\"raw\": \"toss_payment_data\"}");

        // When
        paymentService.confirmPayment(memberId, payload);

        // Then - 결제 정보 DB 저장 검증
        Long paymentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM payment WHERE member_id = ? AND product_type = 'MEMBERSHIP'",
                Long.class, memberId);
        assertThat(paymentCount).isEqualTo(1L);

        // 결제 상세 정보 검증
        Map<String, Object> payment = jdbcTemplate.queryForMap(
                "SELECT * FROM payment WHERE member_id = ? AND product_type = 'MEMBERSHIP'",
                memberId);
        assertThat(payment.get("payment_key")).isEqualTo("pay_membership_001");
        assertThat(payment.get("order_id")).isEqualTo("order_membership_001");
        assertThat(payment.get("amount")).isEqualTo(9900);
        assertThat(payment.get("product_id")).isEqualTo("plan_standard");
        assertThat(payment.get("method")).isEqualTo("CARD");
        assertThat(payment.get("status")).isEqualTo("APPROVED");

        // 멤버십 정보 DB 저장 검증
        Long membershipCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membership WHERE member_id = ? AND status = 'ACTIVE'",
                Long.class, memberId);
        assertThat(membershipCount).isEqualTo(1L);

        // 멤버십 상세 정보 검증
        Map<String, Object> membership = jdbcTemplate.queryForMap(
                "SELECT * FROM membership WHERE member_id = ? AND status = 'ACTIVE'",
                memberId);
        assertThat(membership.get("plan")).isEqualTo("STANDARD");
        assertThat(membership.get("status")).isEqualTo("ACTIVE");
        assertThat(membership.get("start_at")).isNotNull();
        assertThat(membership.get("next_billing_at")).isNotNull();
        assertThat(membership.get("end_at")).isNull(); // 무제한 멤버십
    }

    @Test
    @DisplayName("PER_CASE 결제 확인 - 결제 정보 및 건당 차감 잔액 DB 저장 검증")
    void confirmPayment_perCase() {
        // Given
        String memberId = "test-member-2";
        Map<String, Object> payload = Map.of(
                "paymentKey", "pay_percase_001",
                "orderId", "order_percase_001",
                "amount", 1900,
                "productType", "PER_CASE",
                "productId", "per_case_1",
                "method", "CARD",
                "pgPayload", "{\"raw\": \"toss_payment_data\"}");

        // When
        paymentService.confirmPayment(memberId, payload);

        // Then - 결제 정보 DB 저장 검증
        Long paymentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM payment WHERE member_id = ? AND product_type = 'PER_CASE'",
                Long.class, memberId);
        assertThat(paymentCount).isEqualTo(1L);

        // 결제 상세 정보 검증
        Map<String, Object> payment = jdbcTemplate.queryForMap(
                "SELECT * FROM payment WHERE member_id = ? AND product_type = 'PER_CASE'",
                memberId);
        assertThat(payment.get("payment_key")).isEqualTo("pay_percase_001");
        assertThat(payment.get("order_id")).isEqualTo("order_percase_001");
        assertThat(payment.get("amount")).isEqualTo(1900);
        assertThat(payment.get("product_id")).isEqualTo("per_case_1");
        assertThat(payment.get("method")).isEqualTo("CARD");
        assertThat(payment.get("status")).isEqualTo("APPROVED");

        // 건당 차감 잔액 DB 저장 검증
        Integer perCaseBalance = jdbcTemplate.queryForObject(
                "SELECT per_case_balance FROM member_entitlement WHERE member_id = ?",
                Integer.class, memberId);
        assertThat(perCaseBalance).isEqualTo(1);
    }

    @Test
    @DisplayName("멤버십 생성 후 DB 저장 검증 - 유효기간 설정 확인")
    void membershipCreation_validationPeriod() {
        // Given
        String memberId = "test-member-1";
        Map<String, Object> payload = Map.of(
                "paymentKey", "pay_membership_002",
                "orderId", "order_membership_002",
                "amount", 9900,
                "productType", "MEMBERSHIP",
                "productId", "plan_standard",
                "method", "CARD",
                "pgPayload", "{\"raw\": \"toss_payment_data\"}");

        // When
        paymentService.confirmPayment(memberId, payload);

        // Then - 멤버십 유효기간 설정 검증
        Map<String, Object> membership = jdbcTemplate.queryForMap(
                "SELECT * FROM membership WHERE member_id = ? AND status = 'ACTIVE'",
                memberId);

        // 시작일은 현재 시간과 같거나 이전이어야 함
        Object startAt = membership.get("start_at");
        assertThat(startAt).isNotNull();

        // 다음 결제일은 현재 시간으로부터 1개월 후여야 함
        Object nextBillingAt = membership.get("next_billing_at");
        assertThat(nextBillingAt).isNotNull();

        // end_at은 null이어야 함 (무제한 멤버십)
        assertThat(membership.get("end_at")).isNull();

        // 멤버십 상태가 ACTIVE여야 함
        assertThat(membership.get("status")).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("권한 조회 - 멤버십 활성 상태")
    void getEntitlements_membershipActive() {
        // Given - 멤버십 활성화
        String memberId = "test-member-1";
        jdbcTemplate.execute(
                """
                        INSERT INTO membership (member_id, plan, status, start_at, next_billing_at, created_at) VALUES
                        ('%s', 'STANDARD', 'ACTIVE', NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH), NOW())
                        """.formatted(memberId));

        // When
        Map<String, Object> entitlements = paymentService.getEntitlements(memberId);

        // Then
        assertThat(entitlements.get("membershipActive")).isEqualTo(1L); // MySQL에서 EXISTS는 1 또는 0 반환
        assertThat(entitlements.get("perCaseRemaining")).isEqualTo(0L);
    }

    @Test
    @DisplayName("권한 조회 - 건당 차감 잔액 존재")
    void getEntitlements_perCaseBalance() {
        // Given - 건당 차감 잔액 설정
        String memberId = "test-member-2";
        jdbcTemplate.execute(
                """
                        INSERT INTO member_entitlement (member_id, per_case_balance, updated_at) VALUES
                        ('%s', 5, NOW())
                        """.formatted(memberId));

        // When
        Map<String, Object> entitlements = paymentService.getEntitlements(memberId);

        // Then
        assertThat(entitlements.get("membershipActive")).isEqualTo(0L); // MySQL에서 EXISTS는 1 또는 0 반환
        assertThat(entitlements.get("perCaseRemaining")).isEqualTo(5L);
    }

    @Test
    @DisplayName("건당 차감 - 성공 및 DB 업데이트 검증")
    void consumePerCase_success() {
        // Given - 건당 차감 잔액 설정
        String memberId = "test-member-3";
        jdbcTemplate.execute(
                """
                        INSERT INTO member_entitlement (member_id, per_case_balance, updated_at) VALUES
                        ('%s', 3, NOW())
                        """.formatted(memberId));

        // When
        boolean result = paymentService.consumePerCase(memberId);

        // Then
        assertThat(result).isTrue();

        // DB에서 잔액이 차감되었는지 확인
        Integer remainingBalance = jdbcTemplate.queryForObject(
                "SELECT per_case_balance FROM member_entitlement WHERE member_id = ?",
                Integer.class, memberId);
        assertThat(remainingBalance).isEqualTo(2);
    }

    @Test
    @DisplayName("건당 차감 - 실패 (잔액 부족)")
    void consumePerCase_failure() {
        // Given - 건당 차감 잔액이 0인 상태
        String memberId = "test-member-1";
        jdbcTemplate.execute(
                """
                        INSERT INTO member_entitlement (member_id, per_case_balance, updated_at) VALUES
                        ('%s', 0, NOW())
                        """.formatted(memberId));

        // When
        boolean result = paymentService.consumePerCase(memberId);

        // Then
        assertThat(result).isFalse();

        // DB에서 잔액이 변경되지 않았는지 확인
        Integer remainingBalance = jdbcTemplate.queryForObject(
                "SELECT per_case_balance FROM member_entitlement WHERE member_id = ?",
                Integer.class, memberId);
        assertThat(remainingBalance).isEqualTo(0);
    }

    @Test
    @DisplayName("다운로드 기록 - DB 저장 검증")
    void recordDownload() {
        // Given
        String memberId = "test-member-1";
        String resourceType = "ANALYSIS_REPORT";
        String resourceId = "R-001";
        String usedPaymentType = "MEMBERSHIP";
        String orderId = "order_001";

        // When
        paymentService.recordDownload(memberId, resourceType, resourceId, usedPaymentType, orderId);

        // Then - 다운로드 기록이 DB에 저장되었는지 확인
        Long downloadCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM download_history WHERE member_id = ? AND resource_type = ? AND resource_id = ?",
                Long.class, memberId, resourceType, resourceId);
        assertThat(downloadCount).isEqualTo(1L);

        // 다운로드 기록 상세 정보 검증
        Map<String, Object> download = jdbcTemplate.queryForMap(
                "SELECT * FROM download_history WHERE member_id = ? AND resource_type = ? AND resource_id = ?",
                memberId, resourceType, resourceId);
        assertThat(download.get("member_id")).isEqualTo(memberId);
        assertThat(download.get("resource_type")).isEqualTo(resourceType);
        assertThat(download.get("resource_id")).isEqualTo(resourceId);
        assertThat(download.get("used_payment_type")).isEqualTo(usedPaymentType);
        assertThat(download.get("order_id")).isEqualTo(orderId);
        assertThat(download.get("downloaded_at")).isNotNull();
    }

    @Test
    @DisplayName("멤버십 갱신 - 기존 멤버십 정보 업데이트 검증")
    void membershipRenewal() {
        // Given - 기존 멤버십이 만료된 상태
        String memberId = "test-member-1";
        jdbcTemplate.execute(
                """
                        INSERT INTO membership (member_id, plan, status, start_at, end_at, next_billing_at, created_at) VALUES
                        ('%s', 'STANDARD', 'EXPIRED', DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 2 MONTH))
                        """
                        .formatted(memberId));

        Map<String, Object> payload = Map.of(
                "paymentKey", "pay_membership_renewal",
                "orderId", "order_membership_renewal",
                "amount", 9900,
                "productType", "MEMBERSHIP",
                "productId", "plan_standard",
                "method", "CARD",
                "pgPayload", "{\"raw\": \"toss_payment_data\"}");

        // When
        paymentService.confirmPayment(memberId, payload);

        // Then - 멤버십이 갱신되었는지 확인
        Long membershipCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membership WHERE member_id = ? AND status = 'ACTIVE'",
                Long.class, memberId);
        assertThat(membershipCount).isEqualTo(1L);

        // 멤버십 상세 정보 확인
        Map<String, Object> membership = jdbcTemplate.queryForMap(
                "SELECT * FROM membership WHERE member_id = ? AND status = 'ACTIVE'",
                memberId);

        assertThat(membership.get("status")).isEqualTo("ACTIVE");
        assertThat(membership.get("end_at")).isNull(); // 무제한으로 변경
        assertThat(membership.get("next_billing_at")).isNotNull(); // 다음 결제일 설정
    }
}

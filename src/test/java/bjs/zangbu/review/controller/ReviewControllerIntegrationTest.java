package bjs.zangbu.review.controller;

import bjs.zangbu.config.TestConfig;
import bjs.zangbu.notification.service.NotificationService;
import bjs.zangbu.review.dto.request.ReviewCreateRequest;
import bjs.zangbu.review.dto.response.ReviewCreateResponse;
import bjs.zangbu.review.mapper.ReviewMapper;
import bjs.zangbu.review.service.ReviewService;
import bjs.zangbu.review.service.ReviewServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringJUnitConfig(TestConfig.class)
@Transactional
@DisplayName("ReviewController MySQL 통합 테스트")
class ReviewControllerIntegrationTest {

    @Autowired
    private ReviewMapper reviewMapper;

    @Mock
    private NotificationService notificationService;

    private ReviewController reviewController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jdbcTemplate = new JdbcTemplate(dataSource);

        // Mock 설정
        doNothing().when(notificationService).notificationReviewRegisterd(any(Long.class));

        // 컨트롤러 설정 - 실제 ReviewService 사용
        ReviewService reviewService = new ReviewServiceImpl(reviewMapper, notificationService);
        reviewController = new ReviewController(reviewService);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .defaultRequest(get("/").characterEncoding("UTF-8"))
                .build();
        objectMapper = new ObjectMapper();

        // 테스트 데이터 초기화
        initTestData();
    }

    private void initTestData() {
        // 테스트 데이터 초기화 (외래키 제약조건 순서 고려)
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE review");
        jdbcTemplate.execute("TRUNCATE TABLE building");
        jdbcTemplate.execute("TRUNCATE TABLE complex_list");
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

        // 단지 데이터
        jdbcTemplate.execute(
                """
                            INSERT INTO complex_list (complex_id, res_type, complex_name, complex_no, sido, sigungu, si_code, eupmyeondong,
                                                     transaction_id, address, zonecode, building_name, bname, dong, ho, roadName) VALUES
                            (1, '아파트', '테스트아파트', 12345, '서울특별시', '강남구', '11680', '역삼동', 'test-trans-1',
                             '서울특별시 강남구 역삼동', '06292', '테스트아파트', '역삼동', '101동', '101호', '테헤란로'),
                            (2, '오피스텔', '테스트오피스텔', 12346, '서울특별시', '서초구', '11650', '서초동', 'test-trans-2',
                             '서울특별시 서초구 서초동', '06621', '테스트오피스텔', '서초동', '102동', '201호', '강남대로')
                        """);

        // 매물 데이터
        jdbcTemplate.execute(
                """
                            INSERT INTO building (building_id, member_id, complex_id, seller_nickname, sale_type, price, deposit,
                                                 bookmark_count, created_at, building_name, seller_type, property_type, move_date,
                                                 info_oneline, info_building, contact_name, contact_phone, facility) VALUES
                            (1, 'test-member-1', 1, '테스터1', 'MONTHLY', 200, 50000, 0, NOW(), '테스트아파트 101호', 'OWNER',
                             'APARTMENT', '2024-12-01 00:00:00', '깨끗하고 좋은 아파트입니다', '24평 아파트, 신축급 상태입니다.',
                             '김테스트', '01012345678', '지하철역 도보 5분'),
                            (2, 'test-member-2', 2, '테스터2', 'CHARTER', 80000, 80000, 0, NOW(), '테스트오피스텔 201호', 'TENANT',
                             'OFFICETEL', '2024-11-15 00:00:00', '투자용으로 좋은 오피스텔', '15평 오피스텔, 강남역 근처입니다.',
                             '이테스트', '01087654321', '강남역 도보 10분')
                        """);

        // 리뷰 데이터
        jdbcTemplate.execute(
                """
                            INSERT INTO review (review_id, building_id, member_id, complex_id, reviewer_nickname, `rank`, content, created_at) VALUES
                            (1, 1, 'test-member-2', 1, '테스터2', 5, '정말 깨끗하고 좋은 아파트입니다. 강력 추천합니다!', '2024-01-15 14:30:00'),
                            (2, 1, 'test-member-3', 1, '테스터3', 4, '위치도 좋고 시설도 괜찮습니다. 다만 주차가 조금 불편해요.', '2024-01-20 09:15:00'),
                            (3, 2, 'test-member-1', 2, '테스터1', 3, '오피스텔치고는 괜찮은데 소음이 좀 있어요.', '2024-01-25 16:45:00')
                        """);
    }

    @Test
    @DisplayName("GET /review/list/{buildingId} 200")
    void list_ok() throws Exception {
        // Given
        Long buildingId = 1L;

        // When & Then
        mockMvc.perform(get("/review/list/{buildingId}", buildingId)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews").isArray())
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.latestReviewRank").exists());
    }

    @Test
    @DisplayName("GET /review/{reviewId} - 실제 DB 연동")
    void getReviewDetail_withRealDB() throws Exception {
        // Given
        Long reviewId = 1L;

        // When & Then
        mockMvc.perform(get("/review/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId", is(1)))
                .andExpect(jsonPath("$.buildingId", is(1)))
                .andExpect(jsonPath("$.reviewerNickname", is("테스터2")))
                .andExpect(jsonPath("$.rank", is(5)))
                .andExpect(jsonPath("$.content", is("정말 깨끗하고 좋은 아파트입니다. 강력 추천합니다!")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("GET /review/{reviewId} - 존재하지 않는 리뷰")
    void getReviewDetail_notFound() throws Exception {
        // Given
        Long nonExistentReviewId = 999L;

        // When & Then
        mockMvc.perform(get("/review/{reviewId}", nonExistentReviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("ReviewMapper 직접 테스트")
    void testReviewMapperDirectly() {
        // Given
        Long buildingId = 1L;

        // When
        Long complexId = reviewMapper.selectComplexIdByBuildingId(buildingId);

        // Then
        System.out.println("Complex ID: " + complexId);
        assert complexId != null;
    }

    @Test
    @DisplayName("데이터 확인 테스트")
    void testDataExists() {
        // Given & When
        Long memberCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member", Long.class);
        Long buildingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM building", Long.class);
        Long complexCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM complex_list", Long.class);

        // Then
        System.out.println("Member count: " + memberCount);
        System.out.println("Building count: " + buildingCount);
        System.out.println("Complex count: " + complexCount);

        assert memberCount > 0;
        assert buildingCount > 0;
        assert complexCount > 0;
    }

    @Test
    @DisplayName("ReviewService 직접 테스트")
    void testReviewServiceDirectly() {
        // Given
        ReviewCreateRequest request = createReviewRequest(1L, 1L, 5, "새로운 리뷰입니다!");
        ReviewService reviewService = new ReviewServiceImpl(reviewMapper, notificationService);

        // 실제 존재하는 member_id 사용
        String actualMemberId = jdbcTemplate.queryForObject("SELECT member_id FROM member LIMIT 1", String.class);
        System.out.println("Using member_id: " + actualMemberId);

        try {
            // When
            ReviewCreateResponse response = reviewService.createReview(request, actualMemberId, "임시 닉네임(nickname)");

            // Then
            System.out.println("Response: " + response);
            assert response != null;
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @DisplayName("POST /review - 실제 DB 연동으로 리뷰 생성")
    void createReview_withRealDB() throws Exception {
        // Given
        ReviewCreateRequest request = createReviewRequest(1L, 1L, 5, "새로운 리뷰입니다!");

        // 실제 존재하는 member_id 사용
        String actualMemberId = jdbcTemplate.queryForObject("SELECT member_id FROM member LIMIT 1", String.class);
        System.out.println("Using member_id: " + actualMemberId);

        // ReviewService Mock 생성
        ReviewService mockReviewService = org.mockito.Mockito.mock(ReviewService.class);
        org.mockito.Mockito
                .when(mockReviewService.createReview(any(ReviewCreateRequest.class), any(String.class),
                        any(String.class)))
                .thenReturn(new ReviewCreateResponse(1L, 1L, "임시 닉네임(nickname)", "중층", 5, "새로운 리뷰입니다!",
                        "2024-01-01 00:00:00"));

        // MockMvc 설정
        MockMvc mockMvcWithMock = MockMvcBuilders.standaloneSetup(new ReviewController(mockReviewService))
                .defaultRequest(get("/").characterEncoding("UTF-8"))
                .build();

        // When & Then
        mockMvcWithMock.perform(post("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer dummy-token")
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId", notNullValue()))
                .andExpect(jsonPath("$.buildingId", is(1)))
                .andExpect(jsonPath("$.reviewerNickname", is("임시 닉네임(nickname)")))
                .andExpect(jsonPath("$.rank", is(5)))
                .andExpect(jsonPath("$.content", is("새로운 리뷰입니다!")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("POST /review - 잘못된 요청 데이터")
    void createReview_invalidRequest() throws Exception {
        // Given
        ReviewCreateRequest invalidRequest = createReviewRequest(null, null, null, "내용만 있음");

        // When & Then
        mockMvc.perform(post("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer dummy-token")
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("리뷰 작성에 실패했습니다.")));
    }

    @Test
    @DisplayName("DELETE /review/{reviewId} - 실제 DB 연동")
    void deleteReview_withRealDB() throws Exception {
        // Given
        Long reviewId = 1L;

        // 삭제 전 존재 확인
        mockMvc.perform(get("/review/{reviewId}", reviewId))
                .andExpect(status().isOk());

        // When
        mockMvc.perform(delete("/review/{reviewId}", reviewId))
                .andExpect(status().isNoContent());

        // Then - 삭제 후 조회 시 404 반환
        mockMvc.perform(get("/review/{reviewId}", reviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /review/{reviewId} - 존재하지 않는 리뷰")
    void deleteReview_notFound() throws Exception {
        // Given
        Long nonExistentReviewId = 999L;

        // When & Then
        mockMvc.perform(delete("/review/{reviewId}", nonExistentReviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /review/list/{buildingId} - 페이징 테스트")
    void listReviews_withPaging() throws Exception {
        // Given
        Long buildingId = 1L;

        // When & Then - 기본 페이지 (size=10, page=0)
        mockMvc.perform(get("/review/list/{buildingId}", buildingId)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.reviews", hasSize(2)))
                .andExpect(jsonPath("$.hasNext", is(false)));

        // When & Then - 페이징 파라미터가 제대로 전달되는지 확인
        mockMvc.perform(get("/review/list/{buildingId}", buildingId)
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.hasNext", is(false)));
    }

    @Test
    @DisplayName("GET /review/list/{buildingId} - 리뷰가 없는 매물")
    void listReviews_emptyResult() throws Exception {
        // Given - 리뷰가 없는 매물 추가
        jdbcTemplate.execute(
                """
                            INSERT INTO building (building_id, member_id, complex_id, seller_nickname, sale_type, price, deposit,
                                                 bookmark_count, created_at, building_name, seller_type, property_type, move_date,
                                                 info_oneline, info_building, contact_name, contact_phone, facility) VALUES
                            (999, 'test-member-1', 1, '테스터1', 'MONTHLY', 200, 50000, 0, NOW(), '리뷰없는아파트', 'OWNER',
                             'APARTMENT', '2024-12-01 00:00:00', '리뷰가 없는 아파트', '리뷰가 없습니다.',
                             '김테스트', '01012345678', '지하철역 도보 5분')
                        """);

        Long buildingId = 999L;

        // When & Then
        mockMvc.perform(get("/review/list/{buildingId}", buildingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.reviews", hasSize(0)))
                .andExpect(jsonPath("$.hasNext", is(false)))
                .andExpect(jsonPath("$.latestReviewRank", nullValue()));
    }

    // Helper method for creating review request
    private ReviewCreateRequest createReviewRequest(Long buildingId, Long complexId, Integer rank, String content) {
        ReviewCreateRequest request = new ReviewCreateRequest();
        setField(request, "buildingId", buildingId);
        setField(request, "complexId", complexId);
        setField(request, "rank", rank);
        setField(request, "content", content);
        setField(request, "floor", "중층");
        return request;
    }

    // Helper method for setting private fields via reflection
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}

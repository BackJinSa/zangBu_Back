package bjs.zangbu.review.service;

import bjs.zangbu.config.TestConfig;
import bjs.zangbu.notification.service.NotificationService;
import bjs.zangbu.review.dto.request.ReviewCreateRequest;
import bjs.zangbu.review.dto.response.ReviewCreateResponse;
import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.vo.ReviewListResponseVO;
import bjs.zangbu.review.exception.ReviewNotFoundException;
import bjs.zangbu.review.mapper.ReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.List;
import bjs.zangbu.review.dto.response.ReviewListResponse;

@SpringJUnitConfig(TestConfig.class)
@Transactional
@DisplayName("ReviewService MySQL 통합 테스트")
class ReviewServiceIntegrationTest {

    @Autowired
    private ReviewMapper reviewMapper;

    @Mock
    private NotificationService notificationService;

    private ReviewService reviewService;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jdbcTemplate = new JdbcTemplate(dataSource);
        reviewService = new ReviewServiceImpl(reviewMapper, notificationService);

        // Mock 설정
        doNothing().when(notificationService).notificationReviewRegisterd(any(Long.class));

        // 데이터베이스 연결 테스트
        try {
            Integer connectionTest = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            System.out.println("데이터베이스 연결 성공: " + connectionTest);
        } catch (Exception e) {
            System.err.println("데이터베이스 연결 실패: " + e.getMessage());
            throw new RuntimeException("데이터베이스 연결 실패", e);
        }

        // 테스트 데이터 초기화
        initTestData();
    }

    private void initTestData() {
        try {
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

            System.out.println("테스트 데이터 초기화 완료");

            // 데이터 삽입 확인
            Integer memberCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member", Integer.class);
            Integer complexCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM complex_list", Integer.class);
            Integer buildingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM building", Integer.class);
            Integer reviewCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM review", Integer.class);

            System.out.println("삽입된 데이터 수 - 멤버: " + memberCount + ", 단지: " + complexCount +
                    ", 매물: " + buildingCount + ", 리뷰: " + reviewCount);

        } catch (Exception e) {
            System.err.println("테스트 데이터 초기화 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("테스트 데이터 초기화 실패", e);
        }
    }

    @Test
    @DisplayName("리뷰 목록 조회 - 실제 DB 연동")
    void listReviews_withRealDB() {
        // Given
        Long buildingId = 1L;
        int page = 0;
        int size = 10;

        // 디버깅: 실제 데이터 확인
        System.out.println("=== 테스트 데이터 확인 ===");
        List<ReviewListResponse> directQuery = reviewMapper.selectByBuilding(buildingId);
        System.out.println("직접 조회한 리뷰 수: " + directQuery.size());
        directQuery.forEach(review -> System.out.println("리뷰 ID: " + review.getReviewId() +
                ", 평점: " + review.getRank() +
                ", 작성자: " + review.getReviewerNickName() +
                ", 내용: " + review.getContent() +
                ", 층수: " + review.getFloor()));

        Integer latestRank = reviewMapper.selectLatestReviewRank(buildingId);
        System.out.println("최신 리뷰 평점: " + latestRank);

        // When
        ReviewListResult result = reviewService.listReviews(buildingId, page, size);

        // 디버깅: 결과 확인
        System.out.println("=== 리뷰 목록 조회 결과 ===");
        System.out.println("조회된 리뷰 수: " + result.getReviews().size());
        System.out.println("전체 리뷰 수: " + result.getTotal());
        System.out.println("최신 리뷰 평점: " + result.getLatestReviewRank());
        result.getReviews().forEach(review -> System.out.println("리뷰 ID: " + review.getReviewId() +
                ", 평점: " + review.getRank() +
                ", 작성자: " + review.getReviewerNickName() +
                ", 내용: " + review.getContent() +
                ", 층수: " + review.getFloor()));

        // Then
        assertThat(result.getReviews()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getLatestReviewRank()).isEqualTo(4); // 최신 리뷰의 평점
        // 최신순 정렬 확인
        assertThat(result.getReviews().get(0).getReviewId()).isEqualTo(2L);
        assertThat(result.getReviews().get(1).getReviewId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("리뷰 상세 조회 - 실제 DB 연동")
    void getReviewDetail_withRealDB() {
        // Given
        Long reviewId = 1L;

        // When
        ReviewDetailResponse result = reviewService.getReviewDetail(reviewId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReviewId()).isEqualTo(1L);
        assertThat(result.getBuildingId()).isEqualTo(1L);
        assertThat(result.getReviewerNickname()).isEqualTo("테스터2");
        assertThat(result.getRank()).isEqualTo(5);
        assertThat(result.getContent()).isEqualTo("정말 깨끗하고 좋은 아파트입니다. 강력 추천합니다!");
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 조회 시 예외 발생")
    void getReviewDetail_notFound() {
        // Given
        Long nonExistentReviewId = 999L;

        // When & Then
        assertThatThrownBy(() -> reviewService.getReviewDetail(nonExistentReviewId))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessageContaining("해당 리뷰를 찾을 수 없습니다. reviewId: 999");
    }

    @Test
    @DisplayName("리뷰 생성 - 실제 DB 연동 (complexId 자동 조회)")
    void createReview_withRealDB_autoComplexId() {
        // Given
        ReviewCreateRequest request = createReviewRequest(2L, null, 4, "정말 좋은 오피스텔입니다!");
        String memberId = "test-member-3";
        String nickname = "테스터3";

        // When
        ReviewCreateResponse result = reviewService.createReview(request, memberId, nickname);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReviewId()).isNotNull();
        assertThat(result.getBuildingId()).isEqualTo(2L);
        assertThat(result.getReviewerNickname()).isEqualTo("테스터3");
        assertThat(result.getRank()).isEqualTo(4);
        assertThat(result.getContent()).isEqualTo("정말 좋은 오피스텔입니다!");

        // 실제로 DB에 저장되었는지 확인
        ReviewDetailResponse saved = reviewService.getReviewDetail(result.getReviewId());
        assertThat(saved.getBuildingId()).isEqualTo(2L);
        assertThat(saved.getReviewerNickname()).isEqualTo("테스터3");
    }

    @Test
    @DisplayName("리뷰 생성 - 실제 DB 연동 (complexId 직접 제공)")
    void createReview_withRealDB_providedComplexId() {
        // Given
        ReviewCreateRequest request = createReviewRequest(1L, 1L, 5, "아주 만족스러운 아파트입니다!");
        String memberId = "test-member-1";
        String nickname = "테스터1";

        // When
        ReviewCreateResponse result = reviewService.createReview(request, memberId, nickname);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReviewId()).isNotNull();
        assertThat(result.getBuildingId()).isEqualTo(1L);
        assertThat(result.getReviewerNickname()).isEqualTo("테스터1");
        assertThat(result.getRank()).isEqualTo(5);

        // 실제로 DB에 저장되었는지 확인
        ReviewDetailResponse saved = reviewService.getReviewDetail(result.getReviewId());
        assertThat(saved.getContent()).isEqualTo("아주 만족스러운 아파트입니다!");
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 존재하지 않는 매물")
    void createReview_invalidBuilding() {
        // Given
        ReviewCreateRequest request = createReviewRequest(999L, null, 4, "존재하지 않는 매물 리뷰");
        String memberId = "test-member-1";
        String nickname = "테스터1";

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(request, memberId, nickname))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리뷰 작성에 실패했습니다");
    }

    @Test
    @DisplayName("리뷰 삭제 - 실제 DB 연동")
    void deleteReview_withRealDB() {
        // Given
        Long reviewId = 1L;

        // 삭제 전 존재 확인
        ReviewDetailResponse beforeDelete = reviewService.getReviewDetail(reviewId);
        assertThat(beforeDelete).isNotNull();

        // When
        reviewService.deleteReview(reviewId);

        // Then
        assertThatThrownBy(() -> reviewService.getReviewDetail(reviewId))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰 삭제 실패 - 존재하지 않는 리뷰")
    void deleteReview_notFound() {
        // Given
        Long nonExistentReviewId = 999L;

        // When & Then
        assertThatThrownBy(() -> reviewService.deleteReview(nonExistentReviewId))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("유효하지 않은 리뷰 생성 요청")
    void createReview_invalidRequest() {
        // Given
        ReviewCreateRequest invalidRequest = createReviewRequest(null, null, null, "내용");
        String memberId = "test-member-1";
        String nickname = "테스터1";

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(invalidRequest, memberId, nickname))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리뷰 작성에 실패했습니다");
    }

    @Test
    @DisplayName("잘못된 평점으로 리뷰 생성 시도")
    void createReview_invalidRank() {
        // Given
        ReviewCreateRequest request = createReviewRequest(1L, 1L, 6, "평점 6점"); // 1~5 범위 벗어남
        String memberId = "test-member-1";
        String nickname = "테스터1";

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(request, memberId, nickname))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("리뷰 작성에 실패했습니다");
    }

    @Test
    @DisplayName("getRecentReviews - 실제 DB 연동")
    void getRecentReviews_withRealDB() {
        // Given
        Long buildingId = 1L;
        int limit = 3;

        // When
        List<ReviewListResponseVO> result = reviewService.getRecentReviews(buildingId, limit);

        // 디버깅: 결과 확인
        System.out.println("=== 최근 리뷰 조회 결과 ===");
        System.out.println("조회된 리뷰 수: " + result.size());
        result.forEach(review -> System.out.println("리뷰 ID: " + review.getReviewId() +
                ", 평점: " + review.getRank() +
                ", 작성자: " + review.getReviewerNickName() +
                ", 내용: " + review.getContent() +
                ", 층수: " + review.getFloor() +
                ", 작성일: " + review.getCreatedAt()));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isLessThanOrEqualTo(limit);

        // 최신순 정렬 확인 (PageHelper가 작동하지 않을 경우를 대비)
        if (result.size() >= 2) {
            // created_at 기준으로 정렬되어야 함 (XML에서 ORDER BY created_at DESC)
            // 테스트 데이터: review_id=2가 review_id=1보다 최신
            assertThat(result.get(0).getReviewId()).isEqualTo(2L);
            assertThat(result.get(1).getReviewId()).isEqualTo(1L);
        }
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

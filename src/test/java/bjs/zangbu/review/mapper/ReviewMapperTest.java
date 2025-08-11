package bjs.zangbu.review.mapper;

import bjs.zangbu.config.TestConfig;
import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(TestConfig.class)
@Transactional
@DisplayName("ReviewMapper MySQL 통합 테스트")
class ReviewMapperTest {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);

        // 테스트 데이터 초기화 (외래키 제약조건 순서 고려)
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE review");
        jdbcTemplate.execute("TRUNCATE TABLE building");
        jdbcTemplate.execute("TRUNCATE TABLE complex_list");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

        // 테스트 데이터 삽입
        insertTestData();
    }

    private void insertTestData() {
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
    @DisplayName("특정 매물의 리뷰 목록 조회")
    void selectByBuilding() {
        // Given
        Long buildingId = 1L;

        // When
        List<ReviewListResponse> reviews = reviewMapper.selectByBuilding(buildingId);

        // Then
        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getReviewId()).isEqualTo(2L); // 최신순 정렬
        assertThat(reviews.get(0).getReviewerNickName()).isEqualTo("테스터3");
        assertThat(reviews.get(0).getRank()).isEqualTo(4);

        assertThat(reviews.get(1).getReviewId()).isEqualTo(1L);
        assertThat(reviews.get(1).getReviewerNickName()).isEqualTo("테스터2");
        assertThat(reviews.get(1).getRank()).isEqualTo(5);
    }

    @Test
    @DisplayName("특정 매물의 리뷰 개수 조회")
    void countByBuilding() {
        // Given
        Long buildingId = 1L;

        // When
        Long count = reviewMapper.countByBuilding(buildingId);

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("특정 매물의 최신 리뷰 평점 조회")
    void selectLatestReviewRank() {
        // Given
        Long buildingId = 1L;

        // When
        Integer latestRank = reviewMapper.selectLatestReviewRank(buildingId);

        // Then
        assertThat(latestRank).isEqualTo(4); // 가장 최근 리뷰(테스터3)의 평점
    }

    @Test
    @DisplayName("리뷰 상세 조회")
    void selectById() {
        // Given
        Long reviewId = 1L;

        // When
        ReviewDetailResponse review = reviewMapper.selectById(reviewId);

        // Then
        assertThat(review).isNotNull();
        assertThat(review.getReviewId()).isEqualTo(1L);
        assertThat(review.getBuildingId()).isEqualTo(1L);
        assertThat(review.getReviewerNickname()).isEqualTo("테스터2");
        assertThat(review.getRank()).isEqualTo(5);
        assertThat(review.getContent()).isEqualTo("정말 깨끗하고 좋은 아파트입니다. 강력 추천합니다!");
        assertThat(review.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 조회 시 null 반환")
    void selectById_notFound() {
        // Given
        Long nonExistentReviewId = 999L;

        // When
        ReviewDetailResponse review = reviewMapper.selectById(nonExistentReviewId);

        // Then
        assertThat(review).isNull();
    }

    @Test
    @DisplayName("리뷰 생성")
    void insertReview() {
        // Given
        ReviewInsertParam param = new ReviewInsertParam();
        param.setBuildingId(2L);
        param.setMemberId("test-member-3");
        param.setComplexId(2L);
        param.setReviewerNickname("테스터3");
        param.setRank(4);
        param.setContent("새로운 리뷰입니다.");

        // When
        int result = reviewMapper.insertReview(param);

        // Then
        assertThat(result).isEqualTo(1);
        assertThat(param.getReviewId()).isNotNull();
        assertThat(param.getReviewId()).isGreaterThan(0);

        // 실제로 저장되었는지 확인
        ReviewDetailResponse savedReview = reviewMapper.selectById(param.getReviewId());
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getBuildingId()).isEqualTo(2L);
        assertThat(savedReview.getReviewerNickname()).isEqualTo("테스터3");
        assertThat(savedReview.getRank()).isEqualTo(4);
        assertThat(savedReview.getContent()).isEqualTo("새로운 리뷰입니다.");
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview() {
        // Given
        Long reviewId = 1L;

        // 삭제 전 존재 확인
        ReviewDetailResponse beforeDelete = reviewMapper.selectById(reviewId);
        assertThat(beforeDelete).isNotNull();

        // When
        int result = reviewMapper.deleteReview(reviewId);

        // Then
        assertThat(result).isEqualTo(1);

        // 삭제 후 조회 시 null 반환되는지 확인
        ReviewDetailResponse afterDelete = reviewMapper.selectById(reviewId);
        assertThat(afterDelete).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 삭제 시 0 반환")
    void deleteReview_notFound() {
        // Given
        Long nonExistentReviewId = 999L;

        // When
        int result = reviewMapper.deleteReview(nonExistentReviewId);

        // Then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("building_id로 complex_id 조회")
    void selectComplexIdByBuildingId() {
        // Given
        Long buildingId = 1L;

        // When
        Long complexId = reviewMapper.selectComplexIdByBuildingId(buildingId);

        // Then
        assertThat(complexId).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 building_id로 complex_id 조회 시 null 반환")
    void selectComplexIdByBuildingId_notFound() {
        // Given
        Long nonExistentBuildingId = 999L;

        // When
        Long complexId = reviewMapper.selectComplexIdByBuildingId(nonExistentBuildingId);

        // Then
        assertThat(complexId).isNull();
    }
}

package bjs.zangbu.review.service;

import bjs.zangbu.notification.service.NotificationService;
import bjs.zangbu.review.dto.request.ReviewCreateRequest;
import bjs.zangbu.review.dto.response.ReviewCreateResponse;
import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.exception.ReviewNotFoundException;
import bjs.zangbu.review.mapper.ReviewInsertParam;
import bjs.zangbu.review.mapper.ReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // NotificationService Mock을 ReviewServiceImpl에 주입
        reviewService = new ReviewServiceImpl(reviewMapper, notificationService);
    }

    @Test
    @DisplayName("listReviews: 페이지네이션/메타데이터 조립")
    void listReviews_ok() {
        Long buildingId = 100L;
        List<ReviewListResponse> rows = Arrays.asList(
                new ReviewListResponse(1L, "nick1", "좋은 리뷰", 5, "중층"),
                new ReviewListResponse(2L, "nick2", "괜찮은 리뷰", 4, "고층"));

        // PageHelper는 내부에서 limit/offset만 주입하므로 여기서는 목록만 스텁
        given(reviewMapper.selectByBuilding(buildingId)).willReturn(rows);
        given(reviewMapper.selectLatestReviewRank(buildingId)).willReturn(5);

        ReviewListResult result = reviewService.listReviews(buildingId, 0, 10);

        assertThat(result.getReviews()).hasSize(2);
        assertThat(result.getTotal()).isGreaterThanOrEqualTo(2);
        assertThat(result.getLatestReviewRank()).isEqualTo(5);
    }

    @Test
    @DisplayName("getReviewDetail: 유효하지 않은 ID면 IllegalArgumentException")
    void getReviewDetail_illegalId() {
        assertThatThrownBy(() -> reviewService.getReviewDetail(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("getReviewDetail: 조회 결과 없으면 ReviewNotFoundException")
    void getReviewDetail_notFound() {
        Long reviewId = 10L;
        given(reviewMapper.selectById(reviewId)).willReturn(null);

        assertThatThrownBy(() -> reviewService.getReviewDetail(reviewId))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("getReviewDetail: 정상 반환")
    void getReviewDetail_ok() {
        Long reviewId = 11L;
        ReviewDetailResponse detail = new ReviewDetailResponse(
                reviewId, 100L, 200L, "nick", 5, "good", "2024-01-01T12:00:00");
        given(reviewMapper.selectById(reviewId)).willReturn(detail);

        ReviewDetailResponse resp = reviewService.getReviewDetail(reviewId);
        assertThat(resp.getReviewId()).isEqualTo(reviewId);
        assertThat(resp.getReviewerNickname()).isEqualTo("nick");
        assertThat(resp.getRank()).isEqualTo(5);
    }

    @Test
    @DisplayName("createReview: 유효성 실패 시 IllegalArgumentException")
    void createReview_invalid() {
        ReviewCreateRequest req = new ReviewCreateRequest();
        // 비어있는 요청 → 실패
        assertThatThrownBy(() -> reviewService.createReview(req, "u", "n"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("createReview: 정상 생성 시 매퍼/알림 호출 및 응답 조립")
    void createReview_ok() {
        ReviewCreateRequest req = new ReviewCreateRequest();

        // 리플렉션 없이 세터가 없으므로, 빌더/세터가 없는 구조를 고려해 스파이 파라미터로 검증
        // 테스트를 위해 리퀘스트 필드에 접근 가능한 방법이 없어 Mockito의 thenAnswer로 param 캡쳐
        // 대신 매퍼에 전달되는 ReviewInsertParam을 검증한다.

        // given
        // 필드를 강제로 세팅하기 위해 리플렉션 사용 (테스트 한정)
        setField(req, "buildingId", 100L);
        setField(req, "complexId", 200L);
        setField(req, "floor", "중층");
        setField(req, "rank", 5);
        setField(req, "content", "아주 좋아요");

        // 매퍼 insert가 reviewId를 세팅하도록 스텁
        doAnswer(invocation -> {
            ReviewInsertParam p = invocation.getArgument(0);
            setField(p, "reviewId", 999L);
            return 1;
        }).when(reviewMapper).insertReview(any(ReviewInsertParam.class));

        // when
        ReviewCreateResponse resp = reviewService.createReview(req, "member-1", "닉");

        // then
        assertThat(resp.getReviewId()).isEqualTo(999L);
        assertThat(resp.getBuildingId()).isEqualTo(100L);
        assertThat(resp.getReviewerNickname()).isEqualTo("닉");
        assertThat(resp.getRank()).isEqualTo(5);
        assertThat(resp.getContent()).isEqualTo("아주 좋아요");
        assertThat(resp.getCreatedAt()).isNotBlank();

        // 매퍼에 전달된 파라미터 검증
        ArgumentCaptor<ReviewInsertParam> captor = ArgumentCaptor.forClass(ReviewInsertParam.class);
        verify(reviewMapper, times(1)).insertReview(captor.capture());
        ReviewInsertParam saved = captor.getValue();
        assertThat(getField(saved, "buildingId")).isEqualTo(100L);
        assertThat(getField(saved, "memberId")).isEqualTo("member-1");
        assertThat(getField(saved, "complexId")).isEqualTo(200L);
        assertThat(getField(saved, "reviewerNickname")).isEqualTo("닉");
        assertThat(getField(saved, "rank")).isEqualTo(5);
        assertThat(getField(saved, "content")).isEqualTo("아주 좋아요");

        verify(notificationService, times(1)).notificationReviewRegisterd(eq(100L));
    }

    @Test
    @DisplayName("deleteReview: 잘못된 ID면 IllegalArgumentException")
    void deleteReview_invalid() {
        assertThatThrownBy(() -> reviewService.deleteReview(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("deleteReview: 대상 없으면 ReviewNotFoundException")
    void deleteReview_notFound() {
        given(reviewMapper.deleteReview(123L)).willReturn(0);
        assertThatThrownBy(() -> reviewService.deleteReview(123L))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("deleteReview: 정상 삭제")
    void deleteReview_ok() {
        given(reviewMapper.deleteReview(123L)).willReturn(1);
        reviewService.deleteReview(123L);
        verify(reviewMapper, times(1)).deleteReview(123L);
    }

    // --- test utilities ---
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getField(Object target, String fieldName) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

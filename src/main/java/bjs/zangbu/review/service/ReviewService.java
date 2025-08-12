package bjs.zangbu.review.service;

import bjs.zangbu.review.dto.request.ReviewCreateRequest;
import bjs.zangbu.review.dto.response.ReviewCreateResponse;
import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.dto.response.ReviewListResponse;
import bjs.zangbu.review.vo.ReviewListResponseVO;

import java.util.List;

public interface ReviewService {

    // 리뷰 목록 조회 (페이징)
    ReviewListResult listReviews(Long buildingId, int page, int size);

    // 리뷰 상세보기 기능
    ReviewDetailResponse getReviewDetail(Long reviewId);

    // 리뷰 작성 기능
    ReviewCreateResponse createReview(ReviewCreateRequest req, String memberId, String nickname);

    // 리뷰 삭제 기능
    void deleteReview(Long reviewId);

    // 최근 리뷰 조회 (아파트 상세보기용)
    List<ReviewListResponseVO> getRecentReviews(Long buildingId, int limit);
}

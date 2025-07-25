package bjs.zangbu.review.service;

import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;

public interface ReviewService {
    ReviewListResult listReviews(Long buildingId, int page, int size);


    // 리뷰 상세보기 기능
    ReviewDetailResponse getReviewDetail(Long reviewId);
}

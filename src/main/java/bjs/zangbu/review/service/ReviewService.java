package bjs.zangbu.review.service;

import bjs.zangbu.review.dto.response.ReviewListResult;

public interface ReviewService {
    ReviewListResult listReviews(Long buildingId, int page, int size);
}

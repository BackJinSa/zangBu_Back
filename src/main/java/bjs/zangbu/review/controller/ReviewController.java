package bjs.zangbu.review.controller;

import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.exception.ReviewNotFoundException;
import bjs.zangbu.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // GET /review/list/{buildingId}?page={page}&size={size}
    @GetMapping("/list/{buildingId}")
    public ResponseEntity<?> list(
            @PathVariable Long buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            ReviewListResult result = reviewService.listReviews(buildingId, page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("서버에서 리뷰 목록을 불러오는 중 오류가 발생했습니다.");
        }
    }


    // GET /review/{reviewId}
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<?> detail(
            @PathVariable Long reviewId) {
        try {
            ReviewDetailResponse resp = reviewService.getReviewDetail(reviewId);
            return ResponseEntity.ok(resp); // 200
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body("존재하지 않는 리뷰 식별자입니다.");
        } catch (ReviewNotFoundException e) {
            return ResponseEntity
                    .status(404)
                    .body("존재하지 않는 리뷰입니다.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("서버에서 상세 리뷰를 불러오는 중 오류가 발생했습니다.");
        }
    }
}

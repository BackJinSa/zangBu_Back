package bjs.zangbu.review.controller;

import bjs.zangbu.review.dto.request.ReviewCreateRequest;
import bjs.zangbu.review.dto.response.ReviewCreateResponse;
import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.exception.ReviewNotFoundException;
import bjs.zangbu.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
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
    @GetMapping("/{reviewId}")
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


    // POST /review
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ReviewCreateRequest req,
                                    @RequestHeader(value = "Authorization", required = false) String bearerToken) {

        try {
            // 추후 토큰부 개발 시 수정
            //String memberId   = TokenUtil.getMemberId(bearerToken);
            //String nickname = TokenUtil.getNickname(bearerToken);
            ReviewCreateResponse resp =
                    reviewService.createReview(req, "임시 아이디(memberId)", "임시 닉네임(nickname)");

            return ResponseEntity.status(201).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "리뷰 작성에 실패했습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "서버에서 리뷰 작성 중 오류가 발생했습니다."));
        }
    }


    // DELETE /review/{reviewId}
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.noContent().build(); // 204
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("리뷰 삭제에 실패했습니다.");
        } catch (ReviewNotFoundException e) {
            return ResponseEntity.status(404)
                    .body("존재하지 않는 리뷰입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("서버에서 리뷰를 삭제하던중 오류가 발생했습니다.");
        }
    }
}

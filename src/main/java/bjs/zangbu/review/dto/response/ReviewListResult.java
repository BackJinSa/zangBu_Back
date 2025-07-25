package bjs.zangbu.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewListResult {
    private Long total; // 전체 리뷰 개수
    private List<ReviewListResponse> reviews;
    private Boolean hasNext; // 다음 페이지 존재 여부
    private Integer latestReviewRank; // 최신 리뷰의 별점
}

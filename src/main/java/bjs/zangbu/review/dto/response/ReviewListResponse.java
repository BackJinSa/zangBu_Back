package bjs.zangbu.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewListResponse {
    private Long reviewId; // 리뷰 고유 번호
    private String reviewerNickName; // 리뷰 작성자 닉네임
    private String title; // 리뷰 제목
    private Integer rank; // 평점
    private String floor; // 층수
}

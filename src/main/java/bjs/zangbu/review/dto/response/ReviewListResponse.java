package bjs.zangbu.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ReviewListResponse {
    private Long reviewId; // 리뷰 고유 번호
    private String reviewerNickName; // 리뷰 작성자 닉네임
    private String content; // 리뷰 내용
    private Integer rank; // 평점
    private String floor; // 층수
    private Date createdAt; // 리뷰 작성일시
}

package bjs.zangbu.review.vo;

import bjs.zangbu.review.dto.response.ReviewListResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * ReviewListResponse DTO에 대한 VO 클래스
 * 프론트엔드와의 데이터 전송을 위한 래퍼 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponseVO {
    private Long reviewId; // 리뷰 고유 번호
    private String reviewerNickName; // 리뷰 작성자 닉네임
    private String content; // 리뷰 내용
    private Integer rank; // 평점
    private String floor; // 층수
    private Date createdAt; // 리뷰 작성일시

    /**
     * ReviewListResponse DTO를 VO로 변환하는 정적 팩토리 메서드
     */
    public static ReviewListResponseVO from(ReviewListResponse dto) {
        return new ReviewListResponseVO(
                dto.getReviewId(),
                dto.getReviewerNickName(),
                dto.getContent(),
                dto.getRank(),
                dto.getFloor(),
                dto.getCreatedAt());
    }

    /**
     * ReviewListResponse DTO 리스트를 VO 리스트로 변환하는 정적 팩토리 메서드
     */
    public static java.util.List<ReviewListResponseVO> fromList(java.util.List<ReviewListResponse> dtoList) {
        if (dtoList == null) {
            return new java.util.ArrayList<>();
        }
        return dtoList.stream()
                .map(ReviewListResponseVO::from)
                .collect(java.util.stream.Collectors.toList());
    }
}

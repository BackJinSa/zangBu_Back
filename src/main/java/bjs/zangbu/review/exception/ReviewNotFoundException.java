package bjs.zangbu.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long id) {
        super("해당 리뷰를 찾을 수 없습니다. reviewId: " + id);
    }
}
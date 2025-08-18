package bjs.zangbu.review.exception;

/**
 * 리뷰 작성 시 주소 검증에 실패했을 때 발생하는 예외
 */
public class AddressValidationException extends RuntimeException {

    public AddressValidationException(String message) {
        super(message);
    }

    public AddressValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

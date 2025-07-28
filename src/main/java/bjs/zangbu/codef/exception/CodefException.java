package bjs.zangbu.codef.exception;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import io.codef.api.EasyCodefMessageConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * CODEF 관련 예외의 전역 핸들러
 * EasyCodefMessageConstant로 표현되는 모든 예외를 커스텀 CodefServiceException으로 래핑하여 처리합니다.
 */
@RestControllerAdvice
public class CodefException {

    /**
     * CODEF 서비스 오류 처리를 위한 커스텀 예외 클래스.
     * EasyCodefMessageConstant를 캡슐화하여 구체적인 에러 코드와 메시지를 제공합니다.
     */
    public static class CodefServiceException extends RuntimeException {
        private final String errorCode;
        private final String errorMessage;

        /**
         * EasyCodefMessageConstant로부터 CodefServiceException 생성자.
         * @param messageConstant 발생한 에러를 대표하는 EasyCodefMessageConstant 객체
         */
        public CodefServiceException(EasyCodefMessageConstant messageConstant) {
            super(messageConstant.getMessage()); // 예외의 상세 메시지로 설정
            this.errorCode = messageConstant.getCode(); // CODEF 에러 코드 저장
            this.errorMessage = messageConstant.getMessage(); // CODEF 에러 메시지 저장
        }

        /**
         * EasyCodefMessageConstant와 추가 메시지로 CodefServiceException 생성자.
         * @param messageConstant 발생한 에러를 대표하는 EasyCodefMessageConstant 객체
         * @param extraMessage    추가로 전달할 보조 메시지
         */
        public CodefServiceException(EasyCodefMessageConstant messageConstant, String extraMessage) {
            super(messageConstant.getMessage() + " - " + extraMessage); // 원래 메시지와 추가 메시지 결합
            this.errorCode = messageConstant.getCode();
            this.errorMessage = messageConstant.getMessage() + " - " + extraMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * CodefServiceException을 처리해서 적절한 ResponseEntity로 변환합니다.
     * 어떤 EasyCodefMessageConstant에서 기원하였든, 던져진 CodefServiceException을 하나의 핸들러가 처리합니다.
     *
     * @param ex 발생한 CodefServiceException 예외
     * @return 에러 코드와 메시지를 포함하는 ResponseEntity 객체
     */
    @ExceptionHandler(CodefServiceException.class)
    public ResponseEntity<ErrorResponse> handleCodefServiceException(CodefServiceException ex) {
        // 디버깅을 위한 로그 출력
        System.err.println("CodefServiceException 발생: " + ex.getErrorCode() + " - " + ex.getErrorMessage());
        // 커스텀 에러 응답과 함께 400(BAD_REQUEST) 상태 반환
        return new ResponseEntity<>(new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * 일반적인 RuntimeException을 처리해서 적절한 ResponseEntity로 반환합니다.
     * 예상하지 못한 런타임 예외의 마지막 방어선 역할을 합니다.
     *
     * @param ex 발생한 RuntimeException 예외
     * @return 일반적인 에러 코드와 메시지를 포함하는 ResponseEntity 객체
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        // 디버깅을 위한 로그 출력
        System.err.println("RuntimeException 발생: " + ex.getMessage());
        // INTERNAL_SERVER_ERROR(500) 상태와 일반 에러 응답 반환
        return new ResponseEntity<>(new ErrorResponse("CF-99999", "예상치 못한 오류가 발생했습니다: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 클라이언트에게 전달될 에러 응답 구조를 표현하는 내부 클래스입니다.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String code;
        private String message;
    }
}

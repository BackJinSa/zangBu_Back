package bjs.zangbu.global.exception;


import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception ex) {
    log.error("Exception ......." + ex.getMessage(), ex);
    return ResponseEntity.status(500)
        .body("서버 오류 발생: " + ex.getMessage());
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> handle404(HttpServletRequest request, NoHandlerFoundException ex) {
    log.error("404 Not Found: " + request.getRequestURI(), ex);
    return ResponseEntity.status(404)
        .body("해당 경로를 찾을 수 없습니다: " + request.getRequestURI());
  }
}
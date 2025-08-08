package bjs.zangbu.global.exception;


import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<String> handleException(Exception ex) {
//    log.error("Exception ......." + ex.getMessage(), ex);
//    return ResponseEntity.status(500)
//        .body("서버 오류 발생: " + ex.getMessage());
//  }

}
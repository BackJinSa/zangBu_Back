package bjs.zangbu.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

//HTTP 응답을 JSON 형식으로 작성해서 클라이언트에게 보냄
//ResponseEntity 대신, 필터나 예외 처리 필터처럼 컨트롤러 바깥에서
// 직접 HttpServletResponse를 통해 응답을 내려야 할 때
public class JsonResponse {

  public static <T> void send(HttpServletResponse response, T result) throws IOException {
    ObjectMapper om = new ObjectMapper();
    response.setContentType("application/json;charset=UTF-8");
    Writer out = response.getWriter();
    out.write(om.writeValueAsString(result));
    out.flush();
  }

  public static void sendError(HttpServletResponse response, HttpStatus status, String message)
      throws IOException {
    response.setStatus(status.value());
    response.setContentType("application/json;charset=UTF-8");
    Writer out = response.getWriter();
    out.write(message);
    out.flush();
  }
}
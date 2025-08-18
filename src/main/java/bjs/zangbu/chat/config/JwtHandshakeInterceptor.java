package bjs.zangbu.chat.config;

import bjs.zangbu.security.util.JwtProcessor;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

  private final JwtProcessor jwtProcessor; //토큰 파싱, 검증, 사용자 정보 추출 기능을 제공하는 유틸리티 클래스

  public JwtHandshakeInterceptor(JwtProcessor jwtProcessor) {
    this.jwtProcessor = jwtProcessor;
  }

  //TODO: 테스트 후 이거 필요 없으면 삭제하기
  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) {

    // HTTPServletRequest로 변환 가능할 때만 처리(안전한 처리를 위해서)
    if (request instanceof ServletServerHttpRequest servletRequest) {
      HttpServletRequest httpRequest = servletRequest.getServletRequest();
      String authHeader = httpRequest.getHeader("Authorization");

      // Authorization 헤더가 존재하고 "Bearer "로 시작하는지 확인
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);  //Bearer 제거

        //JWT 유효성 검증
        if (jwtProcessor.validateToken(token)) {
          String userId = jwtProcessor.getEmail(token);
          attributes.put("userId", userId); // 사용자 식별 정보(userId) 저장
          return true; //연결 허용
        }
      }
    }

    return false; // 인증 실패
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception ex) {
    // 핸드쉐이크 완료 후 실행, 추후 필요하면 사용하기
  }
}

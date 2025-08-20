package bjs.zangbu.security.filter;

import bjs.zangbu.security.util.JsonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthenticationErrorFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    // WebSocket 업그레이드는 에러 필터에서 건드리지 않고 그대로 통과 - 웹소켓 연결 자꾸 끊겨서 추가
    String upgrade = request.getHeader("Upgrade");
    if (upgrade != null && "websocket".equalsIgnoreCase(upgrade)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException e) {
      JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "토큰의 유효시간이 지났습니다.");
    } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
      JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, e.getMessage());
    } catch (ServletException e) {
      JsonResponse.sendError(response, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}

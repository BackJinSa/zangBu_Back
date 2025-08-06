package bjs.zangbu.security.filter;

import bjs.zangbu.security.account.dto.request.AuthRequest;
import bjs.zangbu.security.handler.LoginFailureHandler;
import bjs.zangbu.security.handler.LoginSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Log4j2
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  public JwtUsernamePasswordAuthenticationFilter(
      AuthenticationManager authenticationManager,
      LoginSuccessHandler loginSuccessHandler,
      LoginFailureHandler loginFailureHandler) {
    super(authenticationManager);
    setFilterProcessesUrl("/auth/login"); // 필터 적용 주소
    setAuthenticationSuccessHandler(loginSuccessHandler); // 성공 시
    setAuthenticationFailureHandler(loginFailureHandler); // 실패 시
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response)
      throws AuthenticationException {

    // 1. HTTP body에 들어온 JSON → DTO로 변환
    AuthRequest.LoginRequest loginRequest = AuthRequest.LoginRequest.of(request);

    // 2. 인증 정보를 담은 토큰 생성
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
            loginRequest.getPassword());

    // 3. 인증 매니저에게 토큰을 넘겨 인증 수행
    return getAuthenticationManager().authenticate(authenticationToken);
  }
}
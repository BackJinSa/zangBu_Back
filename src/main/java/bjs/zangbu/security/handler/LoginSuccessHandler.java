package bjs.zangbu.security.handler;

import bjs.zangbu.security.account.dto.response.AuthResponse;
import bjs.zangbu.security.account.vo.CustomUser;
import bjs.zangbu.security.account.vo.MemberEnum;
import bjs.zangbu.security.token.TokenFacade;   // 공통 파사드
import bjs.zangbu.security.util.JsonResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final TokenFacade tokenFacade; // Jwt 발급 + Redis 저장 + TTL 제공

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication)
          throws IOException, ServletException {

    CustomUser user = (CustomUser) authentication.getPrincipal();
    String email = user.getMember().getEmail();
    MemberEnum role = user.getMember().getRole();

    // 토큰 발급 + Redis 저장
    AuthResponse.LoginResponse result = tokenFacade.issueAndPersist(email, role);

    // refresh 토큰은 쿠키로만 전달 (SameSite=None; Secure; HttpOnly)
    int maxAge = tokenFacade.getRefreshTtlSeconds(); // Facade에서 TTL 제공
    String setCookie = String.format(
            "refreshToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
            result.getRefreshToken(), maxAge
    );
    response.addHeader("Set-Cookie", setCookie);

    // access 토큰은 바디로만 응답 (refresh는 숨김)
    JsonResponse.send(response,
            new AuthResponse.LoginResponse(result.getAccessToken(), null, role));
  }
}

package bjs.zangbu.security.filter;

import bjs.zangbu.security.util.JwtProcessor;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";

  private final JwtProcessor jwtProcessor;
  private final UserDetailsService userDetailsService;

  private Authentication getAuthentication(String token) {
    // JwtException을 여기서 던지지 않고 null 흐름으로
    String username;
    try {
      username = jwtProcessor.getEmail(token);
    } catch (io.jsonwebtoken.JwtException e) {
      log.warn("Invalid JWT while extracting email: {}", e.getMessage());
      return null;
    }

    if (username == null || username.isBlank()) {
      return null;
    }

    UserDetails userDetails;
    try {
      userDetails = userDetailsService.loadUserByUsername(username);
    } catch (Exception e) {
      log.warn("User not found for JWT subject: {}", username);
      return null;
    }

    return new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
          throws ServletException, IOException {

    String header = request.getHeader(AUTHORIZATION_HEADER);

    log.info("[JWT] {} {} Authorization={}", request.getMethod(), request.getRequestURI(), header);

    if (header != null && header.startsWith(BEARER_PREFIX)) {
      String token = header.substring(BEARER_PREFIX.length()).trim(); // 공백 제거 중요

      try {
        Authentication authentication = getAuthentication(token);

        if (authentication != null) {
          SecurityContextHolder.getContext().setAuthentication(authentication);
          filterChain.doFilter(request, response);
          return;
        } else {
          // 토큰은 있었지만 유효하지 않음 → 401 반환
          SecurityContextHolder.clearContext();
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json;charset=UTF-8");
          response.getWriter().write(
                  "{\"status\":401,\"code\":\"INVALID_TOKEN\",\"message\":\"유효하지 않은 또는 만료된 토큰입니다.\"}");
          return;
        }
      } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
        // 파싱/검증 예외는 여기서 반드시 종결 (500으로 올리지 말 것)
        log.warn("JWT parse/validate error: {}", e.getMessage());
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"status\":401,\"code\":\"INVALID_TOKEN\",\"message\":\"유효하지 않은 또는 만료된 토큰입니다.\"}");
        return;
      }
    }

    // Authorization 헤더가 없거나 Bearer가 아니면 그냥 다음 필터로
    filterChain.doFilter(request, response);
  }

  // JwtAuthenticationFilter 내부
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return uri.startsWith("/auth/login") || uri.startsWith("/auth/reissue"); // 필요하면 /auth/refresh 등도 제외
  }

}
package bjs.zangbu.security.token;

import bjs.zangbu.security.account.dto.response.AuthResponse;
import bjs.zangbu.security.account.vo.MemberEnum;
import bjs.zangbu.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Log4j2
public class TokenFacade {
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String LOGIN_TOKEN_PREFIX = "login:";
    private final JwtProcessor jwtProcessor;
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthResponse.LoginResponse issueAndPersist(String email, MemberEnum role) {
        String at = jwtProcessor.generateAccessToken(email, role.name());
        String rt = jwtProcessor.generateRefreshToken(email);

        //Redis에 refresh 토큰 저장
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(
                key, rt, java.time.Duration.ofMillis(jwtProcessor.getRefreshTokenExpiration())
        );
        log.info("[REFRESH:SAVED] key={}", key);

        //Redis에 로그인 상태 저장 (만료시간 2시간)
        redisTemplate.opsForValue().set(LOGIN_TOKEN_PREFIX + email, "true", java.time.Duration.ofHours(2));

        return new AuthResponse.LoginResponse(at, rt, role);
    }

    /** 쿠키 Max-Age로 쓰기 위한 TTL(초) */
    public int getRefreshTtlSeconds() {
        return (int) Duration.ofMillis(jwtProcessor.getRefreshTokenExpiration()).getSeconds();
    }
}

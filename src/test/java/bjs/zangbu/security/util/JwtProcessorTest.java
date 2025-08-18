package bjs.zangbu.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtProcessorTest {

    private JwtProcessor jwt;
    private Key key;  // 테스트에서 직접 파서에 사용할 키
    // HS256에 충분한 길이(>=32바이트). 길이가 짧으면 JJWT가 예외를 던짐
    private static final String SECRET = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789AB";

    @BeforeEach
    void setUp() {
        jwt = new JwtProcessor();
        // @Value 주입 대신 테스트에서 직접 주입
        ReflectionTestUtils.setField(jwt, "secretKeyPlain", SECRET);
        // @PostConstruct 수동 호출
        jwt.init();

        key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void generateAccessToken_containsClaims_andValidSignature() {
        String token = jwt.generateAccessToken("user@test.com", "USER");
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

        assertEquals("user@test.com", claims.getSubject());
        assertEquals("USER", claims.get("role", String.class));
        assertNotNull(claims.getExpiration());

        long now = System.currentTimeMillis();
        long exp = claims.getExpiration().getTime();
        long expected = now + 2 * 60 * 1000; // 2분
        // 생성/파싱 지연을 고려해 ±5초 허용
        assertTrue(Math.abs(exp - expected) < 5000, "access exp not within ±5s");
    }

    @Test
    void generateRefreshToken_valid_andSubjectOnly() {
        String token = jwt.generateRefreshToken("user@test.com");
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

        assertEquals("user@test.com", claims.getSubject());
        // refresh에는 role claim 안 넣었으므로 null이 기대값
        assertNull(claims.get("role"));
        assertNotNull(claims.getExpiration());
    }

    @Test
    void validateToken_returnsTrue_forValidToken() {
        String token = jwt.generateAccessToken("u@t.com", "ADMIN");
        assertTrue(jwt.validateToken(token));
    }

    @Test
    void validateToken_returnsFalse_forTamperedSignature() {
        // 다른 키로 서명한 토큰
        Key otherKey = Keys.hmacShaKeyFor(
                "DIFFERENT_SECRET_0123456789_DIFFERENT_SECRET_012345".getBytes(StandardCharsets.UTF_8)
        );
        String token = Jwts.builder()
                .setSubject("u@t.com")
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(otherKey)
                .compact();

        assertFalse(jwt.validateToken(token));
        assertNull(jwt.getEmail(token)); // 너 코드에서 invalid면 null 반환
    }

    @Test
    void validateToken_returnsFalse_forExpiredToken() {
        // 같은 키로 "이미 만료된" 토큰 생성
        String expired = Jwts.builder()
                .setSubject("u@t.com")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 과거
                .signWith(key)
                .compact();

        assertFalse(jwt.validateToken(expired));
        assertNull(jwt.getEmail(expired));
    }

    @Test
    void getEmail_and_getRole_work_forValidAccessToken() {
        String token = jwt.generateAccessToken("x@y.com", "USER");
        assertEquals("x@y.com", jwt.getEmail(token));
        assertEquals("USER", jwt.getRole(token));
    }
}
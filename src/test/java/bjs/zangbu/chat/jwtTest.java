package bjs.zangbu.chat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class jwtTest {

    // ✅ 앱의 application-*.properties(yml)에 설정한 값과 100% 동일해야 함
    // 예: jwt.secret=ThisIsADevOnlySecretKeyWithEnoughLength1234567890
    private static final String SECRET = "P3BW7gQhgGH0Y6eoDnbHWPair83UCGug";

    // 만료 시간(Access 100분, Refresh 14일)
    private static final long ACCESS_VALID_MS  = 1000L * 60 * 1000;   //테스트 위해 길게 잡음
    private static final long REFRESH_VALID_MS = 1000L * 60 * 60 * 24 * 14;

    // JwtProcessor.init()과 동일한 키 생성 방식
    private Key key() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JwtProcessor.generateAccessToken(email, role) 과 동일한 구조의 토큰 생성
     * - subject = email  (JwtProcessor.getEmail()은 subject를 반환)
     * - claim "role" 포함
     */
    @Test
    void makeDevAccessToken() {
        String email1 = "amanda.harris@example.com";
        String email2 = "john.smith@example.com";
        String email3 = "jessica.anderson@example.com";
        String email4 = "michael.brown@example.com";

        String role  = "ROLE_MEMBER";

        String token1 = Jwts.builder()
                .setSubject(email1)                                    // ✅ subject = email
                .claim("role", role)                                  // ✅ role 클레임
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_VALID_MS))
                .signWith(key())                                      // ✅ JwtProcessor와 동일한 키 방식
                .compact();


        String token2 = Jwts.builder()
                .setSubject(email2)                                    // ✅ subject = email
                .claim("role", role)                                  // ✅ role 클레임
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_VALID_MS))
                .signWith(key())                                      // ✅ JwtProcessor와 동일한 키 방식
                .compact();

        String token3 = Jwts.builder()
                .setSubject(email3)                                    // ✅ subject = email
                .claim("role", role)                                  // ✅ role 클레임
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_VALID_MS))
                .signWith(key())                                      // ✅ JwtProcessor와 동일한 키 방식
                .compact();

        String token4 = Jwts.builder()
                .setSubject(email4)                                    // ✅ subject = email
                .claim("role", role)                                  // ✅ role 클레임
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_VALID_MS))
                .signWith(key())                                      // ✅ JwtProcessor와 동일한 키 방식
                .compact();

        System.out.println("\n=== user1 amanda : DEV ACCESS TOKEN ===\n" + token1 + "\n");
        System.out.println("\n=== user2 johnny : DEV ACCESS TOKEN ===\n" + token2 + "\n");
        System.out.println("\n=== user3 jessica: DEV ACCESS TOKEN ===\n" + token3 + "\n");
        System.out.println("\n=== user4 michael : DEV ACCESS TOKEN ===\n" + token4 + "\n");
    }

    /**
     * JwtProcessor.generateRefreshToken(email) 과 동일한 구조의 토큰 생성
     * - subject = email
     * - 만료 = 14일
     */
    @Test
    void makeDevRefreshToken() {
        String email = "user1@local.test";

        String token = Jwts.builder()
                .setSubject(email)                                    // ✅ subject = email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_VALID_MS))
                .signWith(key())
                .compact();

        System.out.println("\n=== DEV REFRESH TOKEN ===\n" + token + "\n");
    }
}

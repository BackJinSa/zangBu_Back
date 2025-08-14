package bjs.zangbu.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {

    @Value("${jwt.secret}")
    private String secretKeyPlain;

    private final long accessTokenValidMs = 1000L * 60 * 2 * 100;       // 2분
    private final long refreshTokenValidMs = 1000L * 60 * 60 * 24 * 14; // 14일

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    public String generateAccessToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidMs))
                .signWith(key)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidMs))
                .signWith(key)
                .compact();
    }

    //refresh 토큰 유효시간 반환
    public long getRefreshTokenExpiration(){
        return refreshTokenValidMs;
    }

    // username = email 추출
    public String getEmail(String token) {
        if (!validateToken(token)) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }
        return getClaims(token).getSubject();
    }

    // role 추출
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            getClaims(token); // 유효하면 예외 발생 안 함
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Claims 추출 (토큰 파싱)
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) //jwt 생성/검증 시 사용
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

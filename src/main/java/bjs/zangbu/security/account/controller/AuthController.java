package bjs.zangbu.security.account.controller;

import bjs.zangbu.security.account.dto.request.AuthRequest.*;
import bjs.zangbu.security.account.dto.response.AuthResponse.TokenResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.AuthVerify;
import bjs.zangbu.security.account.dto.response.AuthResponse.LoginResponse;

import bjs.zangbu.security.account.service.AuthService;
import bjs.zangbu.security.util.JwtProcessor;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProcessor jwtProcessor;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    // 1. 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);

        //쿠키에 refresh 토큰 담기
        Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true); //JS 접근 방지 - 장기 인증 수단이므로 클라이언트 측 js에서 접근 못하게 해야 함
        refreshCookie.setSecure(true); //Https에서만 전송 가능하도록
        refreshCookie.setPath("/"); //전체 경로에 대해 유효함
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); //7일간 유효(초단위)

        response.addCookie(refreshCookie);

        //access 토큰은 클라이언트에 바디로 전달
        //refresh 토큰은 쿠키로 숨겨서 클라이언트에 저장
        return ResponseEntity.ok(new LoginResponse(loginResponse.getAccessToken(), null, loginResponse.getRole()));
    }

    // 2. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String accessTokenHeader,
            HttpServletResponse response
    ){
        //토큰 추출
        String accessToken = accessTokenHeader.replace("Bearer ", "");
        //이메일(아이디) 추출
        String email = jwtProcessor.getEmail(accessToken);
        //redis에서 refresh 토큰 제거
        redisTemplate.delete(REFRESH_TOKEN_PREFIX+email);

        //refresh 토큰 쿠키 삭제 - js에서 접근 못하므로, 서버에서 삭제 응답 필요
        Cookie deleteCookie = new Cookie("refreshToken", null); // null로 설정
        deleteCookie.setMaxAge(0);             // 쿠키 즉시 만료
        deleteCookie.setPath("/");             // 경로 일치
        deleteCookie.setHttpOnly(true);        // 보안 설정 유지(js 접근 방지)
        deleteCookie.setSecure(true);          // https 환경에서만
        response.addCookie(deleteCookie);      // 삭제 쿠키 클라이언트에 전달

        return ResponseEntity.ok().build();
    }

    // 3. 아이디(이메일) 찾기
    @PostMapping("/email")
    public ResponseEntity<EmailAuthResponse> findEmail(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody EmailAuthRequest request) {

        //헤더에서 jwt만 추출
        String accessToken = accessTokenHeader.replace("Bearer ", "").trim();
        //토큰 유효성 검사
        if (!jwtProcessor.validateToken(accessToken)) {
            return ResponseEntity.status(401).build();
        }

        EmailAuthResponse response = authService.findEmail(request);
        return ResponseEntity.ok(response);
    }

    //4. 비밀번호 재설정
    @PostMapping("/password")
    public ResponseEntity<Void> resetPassword(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody ResetPassword request,
            HttpSession session) {

        //헤더에서 jwt만 추출
        String accessToken = accessTokenHeader.replace("Bearer ", "").trim();
        //토큰 유효성 검사
        if (!jwtProcessor.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        authService.resetPassword(request, session);
        return ResponseEntity.ok().build();
    }

    //5. 본인인증 요청
    @PostMapping("/verify")
    public ResponseEntity<AuthVerify> verifyAuthenticity(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody VerifyRequest request,
            HttpSession session) {
        //헤더에서 jwt만 추출
        String accessToken = accessTokenHeader.replace("Bearer ", "").trim();
        //토큰 유효성 검사
        if (!jwtProcessor.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthVerify result = authService.verifyAuthenticity(request);
        // 진위 확인 성공 시 인증 상태 세션에 저장
        if ("Y".equalsIgnoreCase(result.getResAuthenticity())) {
            session.setAttribute("verifiedEmail", request.getEmail());
        }

        return ResponseEntity.ok(result);
    }

    // 6. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUp request) {
        authService.signUp(request);
        return ResponseEntity.ok().build();
    }

    // 7. 이메일 중복 확인
    @GetMapping("/check/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean isDuplicated = authService.isEmailDuplicated(email);
        return ResponseEntity.ok(isDuplicated);
    }

    // 8. 닉네임 중복 확인
    @GetMapping("/check/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isDuplicated = authService.isNicknameDuplicated(nickname);
        return ResponseEntity.ok(isDuplicated);
    }

    // 9. 토큰 재발급 요청 --access 토큰 만료 시 클라이언트가 refresh 토큰 전송
    // -> redis에 저장된 refresh 토큰과 비교해서 유효 시 새로운 access 토큰 발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @RequestHeader(value = "Authorization", required = false) String accessTokenHeader,
            HttpServletResponse response
            ){
        if(refreshToken == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("쿠키가 존재하지 않습니다");
        }
        try{
            //토큰 재발급
            TokenResponse newTokens = authService.reissue(refreshToken);

            //쿠키 새로 설정 (refresh 토큰 갱신)
            Cookie refreshCookie = new Cookie("refreshToken", newTokens.getRefreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);

            response.addCookie(refreshCookie);

            //재발급 성공하면 리턴 - refresh 토큰은 쿠키에, access 토큰은 body에
            return ResponseEntity.ok(new TokenResponse(newTokens.getAccessToken(), null));
        } catch (ExpiredJwtException e){
            //토큰 유효기간 만료 400 -> 쿠키도 삭제
            Cookie deleteCookie = new Cookie("refreshToken", null);
            deleteCookie.setMaxAge(0);
            deleteCookie.setPath("/");
            deleteCookie.setHttpOnly(true);
            deleteCookie.setSecure(true);
            response.addCookie(deleteCookie);

            return ResponseEntity.badRequest().body("refresh 토큰이 만료되었습니다.");
        } catch (JwtException e){
            //토큰 손상된 경우(위조/서명 검증 실패) 400
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        } catch (IllegalStateException e){
            //Redis에 저장된 refreshToken이 없음 409
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}

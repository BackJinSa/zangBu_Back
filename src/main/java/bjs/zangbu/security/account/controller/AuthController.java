package bjs.zangbu.security.account.controller;

import bjs.zangbu.security.account.dto.request.AuthRequest.*;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.AuthVerify;
import bjs.zangbu.security.account.dto.response.AuthResponse.LoginResponse;

import bjs.zangbu.security.account.service.AuthService;
import bjs.zangbu.security.util.JwtProcessor;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProcessor jwtProcessor;

    // 1. 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 2. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessTokenHeader){
        //토큰 추출
        String accessToken = accessTokenHeader.replace("Bearer", "");
        //이메일(아이디) 추출
        String email = jwtProcessor.getEmail(accessToken);
        //redis에서 refresh 토큰 제거
        redisTemplate.delete("refresh:"+email);

        return ResponseEntity.ok().build();
    }

    // 3. 아이디 찾기
    @PostMapping("/email")
    public ResponseEntity<EmailAuthResponse> findEmail(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody EmailAuthRequest request) {

        String accessToken = accessTokenHeader.replace("Bearer ", "").trim();
        if (!jwtProcessor.validateToken(accessToken)) {
            return ResponseEntity.status(401).build();
        }

        EmailAuthResponse response = authService.findEmail(request);
        return ResponseEntity.ok(response);
    }

    //4. 비밀번호 재설정
    @PostMapping("/password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPassword request, HttpSession session) {
        authService.resetPassword(request, session);
        return ResponseEntity.ok().build();
    }

    //5. 본인인증 요청
    @PostMapping("/verify")
    public ResponseEntity<AuthVerify> verifyAuthenticity(@RequestBody VerifyRequest request, HttpSession session) {
        AuthVerify result = authService.verifyAuthenticity(request);

        // 진위 확인 성공 시 인증 상태 세션에 저장
        if ("Y".equalsIgnoreCase(result.getResAuthenticity())) {
            session.setAttribute("verifiedEmail", request.getEmail()); // or request.getIdentity() 등으로 조정
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

}

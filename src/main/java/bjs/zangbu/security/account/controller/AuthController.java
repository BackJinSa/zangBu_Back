package bjs.zangbu.security.account.controller;

import bjs.zangbu.security.account.dto.request.AuthRequest.*;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.AuthVerify;
import bjs.zangbu.security.account.dto.response.AuthResponse.LoginResponse;

import bjs.zangbu.security.account.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 1. 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 2. 로그아웃
    //

    // 3. 아이디 찾기
    @PostMapping("/email")
    public ResponseEntity<EmailAuthResponse> findEmail(@RequestBody EmailAuthRequest request) {
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

    // 9. 토큰 재발급 요청

}

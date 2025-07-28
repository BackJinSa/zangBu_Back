package bjs.zangbu.security.account.service;

import bjs.zangbu.security.account.dto.request.AuthRequest.EmailAuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.ResetPassword;
import bjs.zangbu.security.account.dto.request.AuthRequest.VerifyRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.SignUp;
import bjs.zangbu.security.account.dto.request.AuthRequest.LoginRequest;
import bjs.zangbu.security.account.dto.response.AuthResponse.TokenResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.LoginResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.AuthVerify;
import jakarta.servlet.http.HttpSession;


public interface AuthService {

    //로그인
    LoginResponse login(LoginRequest loginRequest);

    //로그아웃
    void logout(String accessToken);

    //회원가입
    void signUp(SignUp signUpRequest);

    //이메일 찾기
    EmailAuthResponse findEmail(EmailAuthRequest request);

    //이메일 중복 체크하기
    boolean isEmailDuplicated(String email);

    //닉네임 중복 체크하기
    boolean isNicknameDuplicated(String nickname);

    // 본인 인증 처리 - 응답에 따라 회원가입/비번 변경 가능
    AuthVerify verifyAuthenticity(VerifyRequest request);

    // 비밀번호 변경 처리
    void resetPassword(ResetPassword request, HttpSession session);

    //토큰 재발급 요청
    TokenResponse reissue(String refreshToken);
}

package bjs.zangbu.security.account.service;

import bjs.zangbu.security.account.dto.request.AuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.EmailAuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.SignUp;
import bjs.zangbu.security.account.dto.response.AuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.TokenResponse;


public interface AuthService {

  //로그아웃
  void logout(String accessToken);

  // codef 주민등록 진위확인
  String codefAuthentication(AuthRequest.VerifyCodefRequest request) throws Exception;

  //회원가입
  void signUp(SignUp signUpRequest) throws Exception;

  //이메일 찾기
  EmailAuthResponse findEmail(EmailAuthRequest request);

  //이메일 중복 체크하기
  boolean isEmailDuplicated(String email);

  //닉네임 중복 체크하기
  boolean isNicknameDuplicated(String nickname);

  //토큰 재발급 요청
  TokenResponse reissue(String refreshToken);

  String cacheVerification(AuthRequest.VerifyCodefRequest request) throws Exception;

  void resetPasswordByToken(String resetToken, String newPassword);

  AuthResponse.PasswordVerifyResponse verifyPasswordFlow(String sessionId);

}

package bjs.zangbu.security.account.dto.response;

import bjs.zangbu.security.account.vo.MemberEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponse {

    // /auth/login Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse{
        private String accessToken;
        private String refreshToken;
        private MemberEnum role;
    }

    // /auth/email Response 이메일 찾기 응답
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailAuthResponse{
        private String email;
    }

    // /auth/verify Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthVerify{
        private String resAuthenticity; // 진위확인 결과 (ex: "Y", "N")
        private String resAuthenticityDesc; // 진위확인 내용 (ex: "성공", "주민번호 불일치")
    }

    // /auth/reissue Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse{
        private String accessToken;
        private String refreshToken;
    }
}

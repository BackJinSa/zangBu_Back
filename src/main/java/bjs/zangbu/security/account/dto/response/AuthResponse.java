package bjs.zangbu.security.account.dto.response;

import bjs.zangbu.security.account.vo.MemberEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponse {

    // /auth/login Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "LoginResponse", description = "로그인 응답 DTO")
    public static class LoginResponse{
        @Schema(description = "액세스 토큰",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        private String accessToken;

        @Schema(description = "리프레시 토큰",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWZyZXNoIn0.4IVKn6X2OeTYuQUbPW-QgLkekJQZkp7pJKG_LMWMNoY")
        private String refreshToken;

        @Schema(description = "역할", example = "ROLE_MEMBER", allowableValues = {
                "ROLE_MEMBER", "ROLE_ADMIN"
        })
        private MemberEnum role;
    }

    // /auth/email Response 이메일 찾기 응답
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "EmailAuthResponse", description = "이메일 찾기 응답 DTO")
    public static class EmailAuthResponse{
        @Schema(description = "이메일", example = "example.zangbu.com")
        private String email;
    }

    // /auth/verify Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AuthVerify", description = "본인인증 결과 응답 DTO")
    public static class AuthVerify{
        @Schema(description = "진위확인 결과", example = "Y")
        private String resAuthenticity; // 진위확인 결과 (ex: "Y", "N")

        @Schema(description = "진위확인 내용", example = "성공")
        private String resAuthenticityDesc; // 진위확인 내용 (ex: "성공", "주민번호 불일치")
    }

    // /auth/reissue Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "TokenResponse", description = "토큰 재발급 응답 DTO")
    public static class TokenResponse{
        @Schema(description = "액세스 토큰",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        private String accessToken;

        @Schema(description = "리프레시 토큰",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWZyZXNoIn0.4IVKn6X2OeTYuQUbPW-QgLkekJQZkp7pJKG_LMWMNoY")
        private String refreshToken;
    }
}

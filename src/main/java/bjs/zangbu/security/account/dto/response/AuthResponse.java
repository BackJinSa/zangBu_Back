package bjs.zangbu.security.account.dto.response;

import bjs.zangbu.security.account.vo.MemberEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse{
        private String accessToken;
        private String refreshToken;
        private MemberEnum role;
    }
}

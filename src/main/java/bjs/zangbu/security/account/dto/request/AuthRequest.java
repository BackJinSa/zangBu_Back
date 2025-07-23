package bjs.zangbu.security.account.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequest {

    // /auth/login
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest{
        private String email;
        private String password;
    }

    public static class EmailAuthRequest{
        private String name;
        private String phone;
    }
}

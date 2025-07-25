package bjs.zangbu.security.account.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;

public class AuthRequest {

    // /auth/login Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest{
        private String email;
        private String password;

        public static LoginRequest of(HttpServletRequest request) {
            ObjectMapper om = new ObjectMapper();
            try {
                return om.readValue(request.getInputStream(), LoginRequest.class);
            } catch (IOException e) {
                throw new BadCredentialsException("username 또는 password가 없음");
            } //catch
        } //of
    }

    // /auth/email Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailAuthRequest{
        private String name;
        private String phone;
    }

    // /auth/password Request 비밀번호 재설정
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPassword{
        private String newPassword;
    }

    // /auth/signup Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUp{
        private String email;
        private String nickname;
        private String password;
        private String identity;
        private String birth;
    }

    // /auth/check/email
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailCheck{
        private String email;
    }

}

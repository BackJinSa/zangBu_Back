package bjs.zangbu.security.account.dto.request;

import bjs.zangbu.security.account.vo.Member;
import bjs.zangbu.security.account.vo.MemberEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    // /auth/email Request 이메일 찾기 요청
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
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUp{
        private String email;
        private String nickname;
        private String password;
        private String identity;
        private String birth;
        private boolean consent;

        //vo로 변환하는 메서드
        public static Member toVo(SignUp request, String encodedPassword){
            return new Member(
                    null,
                    request.getEmail(),
                    encodedPassword,
                    null,
                    request.getNickname(),
                    request.getIdentity(),
                    MemberEnum.ROLE_MEMBER,
                    request.getBirth(),
                    null,
                    request.isConsent(),
                    null
            );
        }
    }

    // /auth/check/email
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailCheck{
        private String email;
    }

    // /auth/check/nickname
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NicknameCheck {
        private String nickname;
    }

    //pass로 본인인증 요청 보낼 때
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyRequest  {
        private String name;       // 이름
        private String identity;   // 주민등록번호
        private String phone;      // 휴대폰 번호
        private String email;      // 이메일 -> 비밀번호 재설정 시 요청에만 사용
    }

}

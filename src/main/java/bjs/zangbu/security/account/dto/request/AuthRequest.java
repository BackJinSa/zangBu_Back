package bjs.zangbu.security.account.dto.request;

import bjs.zangbu.security.account.vo.Member;
import bjs.zangbu.security.account.vo.MemberEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(name = "LoginRequest", description = "로그인 요청 DTO")
    public static class LoginRequest{
        @Schema(description = "이메일", example = "example.zangbu.com")
        private String email;

        @Schema(description = "비밀번호", example = "Password123!")
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
    @Schema(name = "EmailAuthRequest", description = "이메일 찾기 요청 DTO")
    public static class EmailAuthRequest{
        @Schema(description = "이름", example = "김철수")
        private String name;

        @Schema(description = "휴대폰 번호", example = "01012345678")
        private String phone;
    }

    // /auth/password Request 비밀번호 재설정
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ResetPassword", description = "비밀번호 재설정 요청 DTO")
    public static class ResetPassword{
        @Schema(description = "새로운 비밀번호", example = "!123Password")
        private String newPassword;
    }

    // /auth/signup Request 회원가입
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "SignUp", description = "회원가입 요청 DTO")
    public static class SignUp{
        @Schema(description = "이메일", example = "example.zangbu.com")
        private String email;

        @Schema(description = "닉네임", example = "김철수123")
        private String nickname;

        @Schema(description = "비밀번호", example = "Password123!")
        private String password;

        @Schema(description = "주민번호", example = "401234")
        private String identity;

        @Schema(description = "생년월일", example = "010203")
        private String birth;

        @Schema(description = "알림 수신 동의 여부", example = "true")
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

    // /auth/check/email 이메일 중복 체크
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "EmailCheck", description = "이메일 중복 확인 요청 DTO")
    public static class EmailCheck{
        @Schema(description = "이메일", example = "example.zangbu.com")
        private String email;
    }

    // /auth/check/nickname
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "NicknameCheck", description = "닉네임 중복 확인 요청 DTO")
    public static class NicknameCheck {
        @Schema(description = "닉네임", example = "김철수123")
        private String nickname;
    }

    //pass로 본인인증 요청 보낼 때
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "VerifyRequest", description = "본인인증 요청 DTO")
    public static class VerifyRequest  {
        @Schema(description = "이름", example = "김철수")
        private String name;       // 이름

        @Schema(description = "주민번호", example = "401234")
        private String identity;   // 주민등록번호

        @Schema(description = "휴대폰 번호", example = "01012345678")
        private String phone;      // 휴대폰 번호

        @Schema(description = "이메일", example = "example.zangbu.com")
        private String email;      // 이메일 -> 비밀번호 재설정 시 요청에만 사용
    }

    //codef 진위확인
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "VerifyCodefRequest", description = "Codef 주민등록 진위인증 요청 DTO")
    public static class VerifyCodefRequest {
        @Schema(description = "이름", example = "김철수")
        private String name;       // 이름
        @Schema(description = "주민번호", example = "401234")
        private String birth;   // 주민등록번호 앞6자리
        @Schema(description = "주민번호", example = "4012345")
        private String identity;   // 주민등록번호 뒷 7자리
        @Schema(description = "휴대폰 번호", example = "01012345678")
        private String phone;      // 휴대폰 번호
        @Schema(description = "통신사", example = "0 or 1 or 2")
        private String telecom;
        @Schema(description = "주민등록 발급일자 ", example = "yyyymmdd")
        private String issueDate;
    }

}

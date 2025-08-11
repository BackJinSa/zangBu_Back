package bjs.zangbu.security.account.dto.request;

import bjs.zangbu.security.account.vo.Member;
import bjs.zangbu.security.account.vo.MemberEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * 인증 관련 API 요청 시 사용되는 DTO들을 모아놓은 클래스.
 */
public class AuthRequest {

  /**
   * 로그인 요청을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "로그인 요청 DTO")
  public static class LoginRequest {

    /**
     * 사용자 이메일.
     */
    @ApiModelProperty(value = "이메일", example = "example.zangbu.com")
    private String email;

    /**
     * 사용자 비밀번호.
     */
    @ApiModelProperty(value = "비밀번호", example = "Password123!")
    private String password;

    /**
     * HttpServletRequest에서 LoginRequest 객체를 생성합니다.
     *
     * @param request HTTP 서블릿 요청 객체
     * @return 생성된 LoginRequest 객체
     * @throws BadCredentialsException 이메일 또는 비밀번호가 없을 경우 발생
     */
    public static LoginRequest of(HttpServletRequest request) {
      ObjectMapper om = new ObjectMapper();
      try {
        return om.readValue(request.getInputStream(), LoginRequest.class);
      } catch (IOException e) {
        throw new BadCredentialsException("username 또는 password가 없음");
      } //catch
    } //of
  }

  /**
   * 이메일 찾기 요청을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "이메일 찾기 요청 DTO")
  public static class EmailAuthRequest {

    /**
     * 사용자 이름.
     */
    @ApiModelProperty(value = "이름", example = "김철수")
    private String name;

    /**
     * 사용자 휴대폰 번호.
     */
    @ApiModelProperty(value = "휴대폰 번호", example = "01012345678")
    private String phone;
  }

  /**
   * 비밀번호 재설정 요청을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "비밀번호 재설정 요청 DTO")
  public static class ResetPassword {

    /**
     * 새로 설정할 비밀번호.
     */
    @ApiModelProperty(value = "새로운 비밀번호", example = "!123Password")
    private String newPassword;
  }

  /**
   * 회원가입 요청을 위한 DTO.
   */
  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "회원가입 요청 DTO")
  public static class SignUp {

    /**
     * 사용자 이메일.
     */
    @ApiModelProperty(value = "이메일", example = "example.zangbu.com")
    private String email;

    /**
     * 사용자 닉네임.
     */
    @ApiModelProperty(value = "닉네임", example = "김철수123")
    private String nickname;

    /**
     * 사용자 비밀번호.
     */
    @ApiModelProperty(value = "비밀번호", example = "Password123!")
    private String password;

    /**
     * 사용자 주민등록번호 뒷자리 (또는 식별 번호).
     */
    @ApiModelProperty(value = "주민번호", example = "401234")
    private String identity;

    /**
     * 사용자 생년월일.
     */
    @ApiModelProperty(value = "생년월일", example = "010203")
    private String birth;

    /**
     * 알림 수신 동의 여부.
     */
    @ApiModelProperty(value = "알림 수신 동의 여부", example = "true")
    private boolean consent;

    /**
     * SignUp DTO를 Member VO로 변환합니다.
     *
     * @param request 회원가입 요청 DTO
     * @param encodedPassword 인코딩된 비밀번호
     * @return Member VO 객체
     */
    public static Member toVo(SignUp request, String encodedPassword, String memberId) {
      return new Member(
              memberId,
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

  /**
   * 이메일 중복 확인 요청을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "이메일 중복 확인 요청 DTO")
  public static class EmailCheck {

    /**
     * 중복 확인을 요청할 이메일.
     */
    @ApiModelProperty(value = "이메일", example = "example.zangbu.com")
    private String email;
  }

  /**
   * 닉네임 중복 확인 요청을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "닉네임 중복 확인 요청 DTO")
  public static class NicknameCheck {

    /**
     * 중복 확인을 요청할 닉네임.
     */
    @ApiModelProperty(value = "닉네임", example = "김철수123")
    private String nickname;
  }

  /**
   * 본인인증 요청을 위한 DTO (Pass 앱 등 사용).
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "본인인증 요청 DTO")
  public static class VerifyRequest {

    /**
     * 사용자 이름.
     */
    @ApiModelProperty(value = "이름", example = "김철수")
    private String name;       // 이름

    /**
     * 사용자 주민등록번호.
     */
    @ApiModelProperty(value = "주민번호", example = "401234")
    private String identity;   // 주민등록번호

    /**
     * 사용자 휴대폰 번호.
     */
    @ApiModelProperty(value = "휴대폰 번호", example = "01012345678")
    private String phone;      // 휴대폰 번호

    /**
     * 사용자 이메일 (비밀번호 재설정 시 요청에만 사용).
     */
    @ApiModelProperty(value = "이메일", example = "example.zangbu.com")
    private String email;      // 이메일 -> 비밀번호 재설정 시 요청에만 사용
  }

  /**
   * CODEF 주민등록 진위인증 요청을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "Codef 주민등록 진위인증 요청 DTO")
  public static class VerifyCodefRequest {

    /**
     * 이름.
     */
    @ApiModelProperty(value = "이름", example = "김철수")
    private String name;       // 이름
    /**
     * 주민등록번호 앞 6자리.
     */
    @ApiModelProperty(value = "주민번호", example = "401234")
    private String birth;   // 주민등록번호 앞6자리
    /**
     * 주민등록번호 뒷 7자리.
     */
//    @ApiModelProperty(value = "주민번호", example = "4012345")
    @ApiModelProperty(value = "주민번호", example = "RSA 암호화된 주민번호")
    private String identity;   // 주민등록번호 뒷 7자리
    /**
     * 휴대폰 번호.
     */
    @ApiModelProperty(value = "휴대폰 번호", example = "01012345678")
    private String phone;      // 휴대폰 번호
    /**
     * 통신사 정보 (0: SKT, 1: KT, 2: LGU+).
     */
    @ApiModelProperty(value = "통신사", example = "0 or 1 or 2")
    private String telecom;
    /**
     * 주민등록증 발급일자 (YYYYMMDD).
     */
    @ApiModelProperty(value = "주민등록 발급일자 ", example = "yyyymmdd")
    private String issueDate;
  }

}

package bjs.zangbu.security.account.dto.response;

import bjs.zangbu.security.account.vo.MemberEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증 관련 API 응답 시 사용되는 DTO들을 모아놓은 클래스.
 */
@ApiModel(description = "CODEF API 응답 DTO")
public class AuthResponse {

  /**
   * 로그인 응답을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "로그인 응답 DTO")
  public static class LoginResponse {

    /**
     * 발급된 액세스 토큰.
     */
    @ApiModelProperty(value = "액세스 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String accessToken;

    /**
     * 발급된 리프레시 토큰.
     */
    @ApiModelProperty(value = "리프레시 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWZyZXNoIn0.4IVKn6X2OeTYuQUbPW-QgLkekJQZkp7pJKG_LMWMNoY")
    private String refreshToken;

    /**
     * 사용자 역할.
     */
    @ApiModelProperty(value = "역할", example = "ROLE_MEMBER", allowableValues = "ROLE_MEMBER,ROLE_ADMIN")
    private MemberEnum role;
  }

  /**
   * 이메일 찾기 응답을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "이메일 찾기 응답 DTO")
  public static class EmailAuthResponse {

    /**
     * 찾은 이메일 주소.
     */
    @ApiModelProperty(value = "이메일", example = "example.zangbu.com")
    private String email;
  }

  /**
   * 본인인증 결과 응답을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "본인인증 결과 응답 DTO")
  public static class VerifyCodefResponse {

    /**
     * Redis가 발급한 세션 id
     */
    @ApiModelProperty(value = "본인인증 세션 ID", example = "f3f8d8b4-1f6a-4b1e-9a0b-1f1c2d3e4f5a")
    private String sessionId;

    /**
     * 진위확인 결과 (예: "Y", "N").
     */
    @ApiModelProperty(value = "진위확인 결과", example = "Y")
    private String resAuthenticity; // 진위확인 결과 (ex: "Y", "N")

    /**
     * 진위확인 내용 (예: "성공", "주민번호 불일치").
     */
    @ApiModelProperty(value = "진위확인 내용", example = "성공")
    private String resAuthenticityDesc; // 진위확인 내용 (ex: "성공", "주민번호 불일치")
  }

  /**
   * 토큰 재발급 응답을 위한 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "토큰 재발급 응답 DTO")
  public static class TokenResponse {

    /**
     * 새로 발급된 액세스 토큰.
     */
    @ApiModelProperty(value = "액세스 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String accessToken;

    /**
     * 새로 발급된 리프레시 토큰.
     */
    @ApiModelProperty(value = "리프레시 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWZyZXNoIn0.4IVKn6X2OeTYuQUbPW-QgLkekJQZkp7pJKG_LMWMNoY")
    private String refreshToken;
  }
}

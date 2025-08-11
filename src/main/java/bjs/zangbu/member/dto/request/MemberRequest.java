package bjs.zangbu.member.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {

  //비밀번호 변경
  // /member/mypage/edit/password
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "비밀번호 변경 요청 DTO")
  public static class EditPassword {

    @ApiModelProperty(value = "현재 비밀번호", example = "OldPassword123!")
    private String currentPassword;

    @ApiModelProperty(value = "새로운 비밀번호", example = "NewPassword123!")
    private String newPassword;
  }

  //닉네임 중복 확인
  // /member/mypage/edit/nickname/check
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "닉네임 중복 확인 요청 DTO")
  public static class EditNicknameCheck {

    @ApiModelProperty(value = "닉네임", example = "김철수123")
    private String nickname;
  }

  //닉네임 변경 요청
  // /member/mypage/edit/nickname Request
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "닉네임 변경 요청 DTO")
  public static class EditNicknameRequest {

    @ApiModelProperty(value = "닉네임", example = "김철수123")
    private String currentNickname;

    @ApiModelProperty(value = "닉네임", example = "123김철수")
    private String newNickname;
  }

  // /member/mypage/edit/notification/consent
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "알림 수신 동의 여부 변경 요청 DTO")
  public static class EditNotificationConsentRequest {

    @ApiModelProperty(value = "알림 수신 동의 여부", example = "true")
    private Boolean consent;
  }

}

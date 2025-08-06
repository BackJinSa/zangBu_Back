package bjs.zangbu.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {

  //비밀번호 변경
  // /member/mypage/edit/password
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(name = "EditPassword", description = "비밀번호 변경 요청 DTO")
  public static class EditPassword {

    //         @Schema(description = "현재 비밀번호", example = "OldPassword123!")
    private String currentPassword;
    //
//     @Schema(description = "새로운 비밀번호", example = "NewPassword123!")
    private String newPassword;
  }

  //닉네임 중복 확인
  // /member/mypage/edit/nickname/check
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(name = "EditNicknameCheck", description = "닉네임 중복 확인 요청 DTO")
  public static class EditNicknameCheck {

    //         @Schema(description = "중복 확인할 닉네임", example = "zangbuUser01")
    private String nickname;
  }

  //닉네임 변경 요청
  // /member/mypage/edit/nickname Request
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(name = "EditNicknameRequest", description = "닉네임 변경 요청 DTO")
  public static class EditNicknameRequest {

    //         @Schema(description = "현재 닉네임", example = "zangbuUser01")
    private String currentNickname;
    //
//     @Schema(description = "새 닉네임", example = "zangbuUser02")
    private String newNickname;
  }

  // /member/mypage/edit/notification/consent
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(name = "EditNotificationConsentRequest", description = "알림 수신 동의 여부 변경 요청 DTO")
  public static class EditNotificationConsentRequest {

    //         @Schema(description = "알림 수신 동의 여부", example = "true")
    private Boolean consent;
  }

}

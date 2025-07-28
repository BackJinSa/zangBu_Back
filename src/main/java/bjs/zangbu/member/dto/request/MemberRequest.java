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
    public static class EditPassword{
        private String currentPassword;
        private String newPassword;
    }

    //닉네임 중복 확인
    // /member/mypage/edit/nickname/check
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNicknameCheck{
        private String nickname;
    }

    //닉네임 변경 요청
    // /member/mypage/edit/nickname Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNicknameRequest{
        private String currentNickname;
        private String newNickname;
    }

    // /member/mypage/edit/notification/consent
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNotificationConsentRequest{
        private Boolean consent;
    }

}

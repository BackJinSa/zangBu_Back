package bjs.zangbu.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {

    // /user/mypage/edit/password
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditPassword{
        private String currentPassword;
        private String newPassword;
    }

    // /user/mypage/edit/nickname/check
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNicknameCheck{
        private String nickname;
    }

    // /user/mypage/edit/nickname Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNicknameRequest{
        private String currentNickname;
        private String newNickname;
    }
}

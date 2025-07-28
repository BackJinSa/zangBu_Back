package bjs.zangbu.member.dto.response;

import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponse {

    // /member/mypage/edit Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditMyPage{
        private String nickName;
        private String password;
    }

    // /member/mypage/edit/nickname Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNicknameResponse{
        private String nickName;
    }

    // /member/mypage/edit/notification/consent
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNotificationConsentResponse{
        private Boolean consent;
    }

    // /member/mypage/notification/consent
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationConsentCheck{
        private Boolean consent;
    }
}

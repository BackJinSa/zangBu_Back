package bjs.zangbu.member.dto.response;

import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponse {

    // /user/mypage/edit Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditMyPage{
        private String nickName;
        private String password;
    }

    // /user/mypage/edit/nickname Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNicknameResponse{
        private String nickName;
    }
}

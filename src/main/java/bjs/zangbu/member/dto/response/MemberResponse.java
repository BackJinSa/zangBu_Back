package bjs.zangbu.member.dto.response;

import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponse {

    // /user/mypage/favorites?page={page}&size={size} Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkBuilding {
        private Long bookmarkId;
        private Long buildingId;
        private Long complexId;
        private String sellerNickname;
        private String saleType;
        private Integer price;
        private Long deposit;
        private Integer bookmarkCount;
        private String createdAt;
        private String buildingName;
        private String sellerType;
        private String propertyType;
        private String moveDate;
        private String infoOneLine;
        private String infoBuilding;
        private String imageUrl;
        private String contactName;
        private String contactPhone;
        private String facility;
    }

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

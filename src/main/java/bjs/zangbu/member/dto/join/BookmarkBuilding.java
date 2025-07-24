package bjs.zangbu.member.dto.join;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// /user/mypage/favorites?page={page}&size={size} Response
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkBuilding {
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
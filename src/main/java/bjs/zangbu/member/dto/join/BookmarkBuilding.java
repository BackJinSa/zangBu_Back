package bjs.zangbu.member.dto.join;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// /user/mypage/favorites?page={page}&size={size} Response
@Setter
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
    private LocalDateTime createdAt;
    private String buildingName;
    private String sellerType;
    private String propertyType;
    private LocalDateTime moveDate;
    private String infoOneLine;
    private String infoBuilding;
    private String imageUrl;
    private String contactName;
    private String contactPhone;
    private String facility;
    private Float size;
}
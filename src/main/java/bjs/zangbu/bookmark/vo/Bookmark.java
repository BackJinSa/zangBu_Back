package bjs.zangbu.bookmark.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    // 찜한 매물 식별키
    private Long bookMarkId;

    // 찜한 매물 시세
    private Integer price;

    // 외래키
    // 빌딩 식별자(BIGINT)
    private Long buildingId;
    // 유저 식별자(UUID)
    private String userId;
    // 주소 식별자(BIGINT)
    private Long addressId;
    // 단지 식별자(BIGINT)
    private Long complexId;
}

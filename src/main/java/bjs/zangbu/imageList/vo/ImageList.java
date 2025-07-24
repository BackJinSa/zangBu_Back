package bjs.zangbu.imageList.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class ImageList {
    // 이미지 고유 ID
    private Long imageId;
    // 매물 고유 식별자
    private Long buildingId;
    // 유저 고유 식별자
    private String memberId;
    // 복합단지 고유 식별자
    private Long complexId;
    // 이미지 URL
    private String imageUrl;
}

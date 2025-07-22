package bjs.zangbu.deal.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Deal {
    // 거래 식별 id
    private Long dealId;

    // 거래 상태
    private DealEnum status;

    // ==== foreign key

    // 채팅방 식별 id
    private String chatRoomId;

    // 매물 식별 id
    private Long buildingId;

    // 유저 식별 id
    private String userId;

    // 단지 식별 id
    private Long complexId;

    // 주소 식별 id
    private Long addressId;
}

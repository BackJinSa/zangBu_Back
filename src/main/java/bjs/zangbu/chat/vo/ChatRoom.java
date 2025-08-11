package bjs.zangbu.chat.vo;

import bjs.zangbu.building.vo.SellerType;
import bjs.zangbu.deal.vo.DealEnum;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatRoom {
    private String chatRoomId;          //채팅방 id(UUID)
    private Long buildingId;            //매물 식별 id
    private String sellerId;            //판매자 id : (building의 member_id)
    private String consumerId;          //구매자 id : (chat_room의 member_id)
    private Long complexId;           //단지 id
    private String sellerNickname;      //판매자 닉네임
    private String consumerNickname;    //구매자 닉네임
    private Boolean sellerVisible;     //판매자 채팅방 목록에서 보일지 유무(채팅방 나가기 구현에 사용)
    private Boolean consumerVisible;   //구매자 채팅방 목록에서 보일지 유무(채팅방 나가기 구현에 사용)
    private String buildingName;       //매물 이름 : (building의 building_name)
    private SellerType sellerType;         //판매자 타입(집주인, 세입자) : (building의 seller_type)
    private DealEnum status;             //거래 상태 : (deal의 status)
}
package bjs.zangbu.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {
    private String chatRoomId;          //채팅방 id
    private Long buildingId;            //매물 식별 id -> 이걸로 판매자 id 얻기
    private String consumerId;          //구매자 id
    private String complexId;           //단지 id
    private String sellerNickname;      //판매자 닉네임
    private String consumerNickname;    //구매자 닉네임
    private Boolean sellerVisible;     //판매자 채팅방 목록에서 보일지 유무(채팅방 나가기 구현에 사용)
    private Boolean consumerVisible;   //구매자 채팅방 목록에서 보일지 유무(채팅방 나가기 구현에 사용)
    private String buildingName;       //매물 이름
    private String sellerType;         //판매자 타입(집주인, 세입자)
}
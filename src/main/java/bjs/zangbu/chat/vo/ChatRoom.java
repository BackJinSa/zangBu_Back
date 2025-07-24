package bjs.zangbu.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    private String chatRoomId;       //채팅방 id
    private Long buildingId;   //매물 식별 id
    private String userId;          //구매자 id
    private String sellerNickname;   //판매자 닉네임
    private String consumerNickname;    //구매자 닉네임
}
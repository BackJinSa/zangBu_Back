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
    private BigInteger buildingId;   //매물 식별 id
    private String sellerId;         //판매자 id
    private String buyerId;          //구매자 id
    private String sellerNickname;   //판매자 닉네임
    private String buyerNickname;    //구매자 닉네임
}
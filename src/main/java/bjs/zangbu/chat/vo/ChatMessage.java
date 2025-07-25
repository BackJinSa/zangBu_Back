package bjs.zangbu.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private Long chatMessageId;   //채팅 메시지 식별 id
    private String chatRoomId;    //채팅방 id
    private Long buildingId;        //매물 id
    private String senderId;     //메시지 작성한 사용자 id
    private Long complexId;      //단지 id
    private String message;     //메시지 내용
    private LocalDateTime createdAt;    //메시지 작성 시간
}
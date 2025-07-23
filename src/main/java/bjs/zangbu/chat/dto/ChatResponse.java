package bjs.zangbu.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

public class ChatMessageResponse {

    // /chat/room/{roomId}?lastMessageId={lastMessageId}&limit={limit}
    public static class ChatMessageListResponse { //roomId에 해당하는 채팅방의 메시지들 마지막부터 limit개 불러오기
        private String buildingName;    //건물 이름
        private String sellerNickname;  //판매자 닉네임
        private String sellerType;      //세입자, 집주인
        private String status;          //거래 상태
        private List<Message> messageList;      //채팅방 내 메시지들
    }

    public static class Message {
        private Long messageId;     //메시지 id
        private String message;     //메시지 내용
        private String senderNickname;      //보낸 사람 닉네임
        private String createdAt;           //메시지 생성 날짜
        //없는 경우 : ""   오늘인 경우 : "HH:mm"  올해인 경우 : "MM/dd"   이외 : "yyyy/MM/dd"
    }

    // /pub/chat.message
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendMessageResponse {
        private String message;          //보낸 메시지 내용
        private String sendNickname;     //보낸 사람 닉네임
        private String createdAt;        //메시지 보낸 시간
    }
}

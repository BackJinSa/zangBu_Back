package bjs.zangbu.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatMessageRequest {

    //  /pub/chat.message
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendMessageRequest {
        private String chatRoomId;   //채팅방 ID
        private String message;      //보낼 메시지 내용
    }
}

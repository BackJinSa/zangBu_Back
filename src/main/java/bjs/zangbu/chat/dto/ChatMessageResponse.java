package bjs.zangbu.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatMessageResponse {

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

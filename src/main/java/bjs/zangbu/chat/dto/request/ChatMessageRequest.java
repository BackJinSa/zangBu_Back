package bjs.zangbu.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatMessageRequest {

    //   /chat/room/{roomId}?lastMessageId={lastMessageId}&limit={limit}
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageListRequest { //roomId에 해당하는 채팅방의 메시지들 마지막부터 limit개 불러오기
        private Long lastMessageId;  // 마지막으로 받은 메시지 ID
        private int limit = 20;      // 기본값 20
        //컨트롤러에서 @RequestParam으로
    }


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

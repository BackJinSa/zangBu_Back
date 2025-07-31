package bjs.zangbu.chat.dto.request;

import bjs.zangbu.chat.vo.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChatRequest {

    //   /chat/room/{roomId}?lastMessageId={lastMessageId}&limit={limit}
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageListRequest {
        //roomId에 해당하는 채팅방의 메시지들 마지막부터 limit개 불러오기
        private Long lastMessageId;  // 마지막으로 받은 메시지 ID
        private int limit;      // 한 번에 불러올 개수
        //컨트롤러 작성해보고 dto작성 없이 @RequestParam으로 할지 생각해보기
    }

    //  /pub/chat.message
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendMessageRequest {
        private String message;
        private String chatRoomId;

        public ChatMessage toEntity(String senderId, LocalDateTime createdAt) {
            return ChatMessage.builder()
                    .chatRoomId(chatRoomId)
                    .senderId(senderId)
                    .message(message)
                    .createdAt(createdAt)
                    .build();
        }
    }

    //  /chat/list?page={page}&size={size}&type={type}
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ChatRoomListRequest {
        private int page;       //페이지
        private int size;       //한 페이지에 보여줄 채팅방 개수
        private String type; // "전체" or "구매" or "판매"
    }
}

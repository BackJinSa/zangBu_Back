package bjs.zangbu.chat.dto.request;

import bjs.zangbu.chat.vo.ChatMessage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRequest {

  //   /chat/room/{roomId}?lastMessageId={lastMessageId}&limit={limit}
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(description = "채팅방 메시지 목록 요청 DTO")
  public static class ChatMessageListRequest { //roomId에 해당하는 채팅방의 메시지들 마지막부터 limit개 불러오기

    //         @Schema(description = "마지막으로 받은 메시지 ID", example = "1005")
    private Long lastMessageId;  // 마지막으로 받은 메시지 ID
    //         @Schema(description = "한 번에 불러올 메시지 수", example = "20")
    private int limit;      // 한 번에 불러올 개수
    //컨트롤러 작성해보고 dto작성 없이 @RequestParam으로 할지 생각해보기
  }

  //  /pub/chat.message
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
//     @Schema(description = "메시지 전송 요청 DTO")
  public static class SendMessageRequest {

    //         @Schema(description = "전송할 메시지 내용", example = "안녕")
    private String message;
    //         @Schema(description = "채팅방 ID", example = "room-uuid-123")
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
//     @Schema(description = "채팅방 목록 요청 DTO")
  public class ChatRoomListRequest {

    //
//     @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    private int page;       //페이지
    //
//     @Schema(description = "한 페이지에 보여줄 채팅방 개수", example = "10")
    private int size;       //한 페이지에 보여줄 채팅방 개수
    //
//     @Schema(description = "필터 타입 (전체, 구매, 판매)", example = "전체")
    private String type; // "전체" or "구매" or "판매"
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  // POST  /chat/room 채팅방 생성
  public static class CreateRoomRequest {
    private Long buildingId;
  }
}

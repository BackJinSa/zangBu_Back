package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.dto.request.ChatRequest;
import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성 후 반환
    // 이미 채팅방 존재하면 그 채팅방 반환
    @PostMapping("/room")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoom chatRoom) {
        ChatRoom room = chatService.createChatRoom(chatRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    // 사용자가 참여하고 있는 채팅방 목록 조회
    @GetMapping("/list")
    public List<ChatResponse.ChatRoomListResponse> getChatRoomList(
            @RequestParam String type, @RequestParam int page, @RequestParam int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return chatService.getChatRoomList(userId, type, page, size);
    }

    // 채팅방 상세 정보 조회
    @GetMapping("/room/info/{roomId}")
    public ResponseEntity<ChatRoom> getChatRoomDetail(@PathVariable String roomId) {
        ChatRoom room = chatService.getChatRoomDetail(roomId);
        return ResponseEntity.ok(room); // 200 OK 응답
    }

    //채팅방 입장 시 채팅방의 메시지들 조회 (한 번에 보여주는 개수는 기본 20개)
    @GetMapping("/room/{roomId}")
    public List<ChatMessage> getMessages(
            @PathVariable String roomId,
            @RequestParam(required = false) Long lastMessageId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return chatService.getMessages(roomId, lastMessageId, limit);
    }

    // 채팅방 나가기
    @PatchMapping("/list/exit/{roomId}")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable String roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        chatService.leaveChatRoom(roomId, userId);
        return ResponseEntity.status(204).build();
    }

}

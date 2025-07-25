package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;
import lombok.RequiredArgsConstructor;
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
    public ChatRoom createChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatService.createChatRoom(chatRoom);
    }

    // 사용자가 참여하고 있는 채팅방 목록 조회
    @GetMapping("/list")
    public List<ChatRoom> getChatRoomList(
            @RequestParam String userId, @RequestParam String type,
            @RequestParam int page, @RequestParam int size) {
        return chatService.getChatRoomList(userId, type, page, size);
    }

    // 채팅방 상세 정보 조회
    @GetMapping("/room/{roomId}")
    public ChatRoom getChatRoomDetail(@PathVariable String roomId) {
        return chatService.getChatRoomDetail(roomId);
    }

    //채팅방의 메시지들 조회 (한 번에 보여주는 개수는 기본 20개)
    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessage> getMessages(
            @PathVariable String roomId,
            @RequestParam(required = false) Long lastMessageId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return chatService.getMessages(roomId, lastMessageId, limit);
    }

    // 채팅방 삭제
    @DeleteMapping("/room/{roomId}")
    public void deleteChatRoom(@PathVariable String roomId) {
        chatService.deleteChatRoom(roomId);
    }

}

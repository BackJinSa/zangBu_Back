package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Chat Rest API", description = "채팅 관련 기능 API")
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성 후 반환, 이미 채팅방 존재하면 그 채팅방 반환
    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "채팅방 생성 성공 후 반환(이미 채팅방 존재하면 기존 채팅방 반환)"),
            @ApiResponse(responseCode = "400", description = "채팅방 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/room")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoom chatRoom) {
        ChatRoom room = chatService.createChatRoom(chatRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    // 사용자가 참여하고 있는 채팅방 목록 조회
    @Operation(summary = "채팅방 목록 조회", description = "사용자의 채팅방 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "채팅방 목록 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/list")
    public ResponseEntity<List<ChatResponse.ChatRoomListResponse>> getChatRoomList(
            @Parameter(description = "전체 or 구매 or 판매") @RequestParam String type,
            @Parameter(description = "페이지 번호") @RequestParam int page,
            @Parameter(description = "한 페이지 당 표시할 개수") @RequestParam int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        List<ChatResponse.ChatRoomListResponse> roomList = chatService.getChatRoomList(userId, type, page, size);
        return ResponseEntity.status(200).body(roomList);
    }

    // 채팅방 상세 정보 조회
    @Operation(summary = "채팅방 상세 조회", description = "채팅방의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 상세 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 id에 대한 채팅방이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/room/info/{roomId}")
    public ResponseEntity<ChatRoom> getChatRoomDetail(
            @Parameter(description = "조회할 채팅방 id") @PathVariable String roomId) {
        ChatRoom room = chatService.getChatRoomDetail(roomId);
        return ResponseEntity.status(200).body(room);
    }

    //채팅방 입장 시 채팅방의 메시지들 조회 (한 번에 보여주는 개수는 기본 20개)
    @Operation(summary = "채팅방 메시지들 조회", description = "채팅방의 메시지들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 메시지들 조회 성공"),
            @ApiResponse(responseCode = "400", description = "채팅방 ID 또는 파라미터 오류"),
            @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChatMessage>> getMessages(
            @Parameter(description = "조회할 채팅방 id") @PathVariable String roomId,
            @Parameter(description = "한 번에 보여주는 메시지 개수") @RequestParam(defaultValue = "20") int limit
    ) {
        List<ChatMessage> messages = chatService.getMessages(roomId, limit);
        return ResponseEntity.status(200).body(messages);
    }

    // 채팅방 나가기
    @Operation(summary = "채팅방 나가기", description = "채팅방을 나갑니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "채팅방 나가기 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 사용자 or 채팅방 ID"),
            @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PatchMapping("/list/exit/{roomId}")
    public ResponseEntity<Void> leaveChatRoom(
            @Parameter(description = "나갈 채팅방 id") @PathVariable String roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        chatService.leaveChatRoom(roomId, userId);
        return ResponseEntity.status(204).build();
    }

}

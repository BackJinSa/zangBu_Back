package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.service.ChatServiceImpl;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.dto.request.ChatRequest;
import bjs.zangbu.chat.vo.ChatRoom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
// @Tag(name="Chat Rest API", description = "채팅 관련 기능 API")
public class ChatController {

  private final ChatService chatService;

  // 채팅방 생성 후 반환, 이미 채팅방 존재하면 그 채팅방 반환
//     @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다.")
//   @ApiResponses({
//       @ApiResponse(responseCode = "201", description = "채팅방 생성 성공 후 반환(이미 채팅방 존재하면 기존 채팅방 반환)"),
//       @ApiResponse(responseCode = "400", description = "채팅방 생성 실패"),
//       @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//})

  @PostMapping(value = "/room/{buildingId}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<ChatRoom> createChatRoom(@PathVariable Long buildingId) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    String consumerId = authentication.getName();  //TODO: 테스트용, 나중에 주석 취소
    String consumerId = "user-001";

    log.info("ChatController - createChatRoom");
    ChatRoom room = chatService.createChatRoom(buildingId, consumerId);
    return ResponseEntity.status(HttpStatus.CREATED).body(room);
  }

// 사용자가 참여하고 있는 채팅방 목록 조회
//     @Operation(summary = "채팅방 목록 조회", description = "사용자의 채팅방 목록을 조회합니다.")
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공"),
//       @ApiResponse(responseCode = "400", description = "채팅방 목록 조회 실패"),
//       @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//  })

  @GetMapping(value=  "/list",  produces = "application/json;charset=UTF-8")
  public ResponseEntity<List<ChatResponse.ChatRoomListResponse>> getChatRoomList(
//    @Parameter(description = "전체 or 구매 or 판매")
      @RequestParam String type,
//    @Parameter(description = "페이지 번호")
      @RequestParam int page,
//    @Parameter(description = "한 페이지 당 표시할 개수")
      @RequestParam int size) {
    log.info("ChatController - getChatRoomList");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //String userId = authentication.getName();  //TODO: 테스트하느라 주석처리함
    String userId = "user-001";
    List<ChatResponse.ChatRoomListResponse> roomList = chatService.getChatRoomList(userId, type,
        page, size);
    return ResponseEntity.status(200).body(roomList);
  }

// 채팅방 상세 정보 조회
//     @Operation(summary = "채팅방 상세 조회", description = "채팅방의 상세 정보를 조회합니다.")
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "채팅방 상세 정보 조회 성공"),
//       @ApiResponse(responseCode = "404", description = "해당 id에 대한 채팅방이 없음"),
//       @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//  })

  @GetMapping("/room/info/{roomId}")
  public ResponseEntity<ChatRoom> getChatRoomDetail(
//    @Parameter(description = "조회할 채팅방 id")
      @PathVariable String roomId) {
    ChatRoom room = chatService.getChatRoomDetail(roomId);
    return ResponseEntity.status(200).body(room);
  }

//채팅방 입장 시 채팅방의 메시지들 조회 (한 번에 보여주는 개수는 기본 20개)
//     @Operation(summary = "채팅방 메시지들 조회", description = "채팅방의 메시지들을 조회합니다.")
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "채팅방 메시지들 조회 성공"),
//       @ApiResponse(responseCode = "400", description = "채팅방 ID 또는 파라미터 오류"),
//       @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않음"),
//       @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//  })

  @GetMapping(value = "/room/{roomId}", produces = "application/json")
  public ResponseEntity<List<ChatMessage>> getMessages(
//    @Parameter(description = "조회할 채팅방 id")
      @PathVariable String roomId,
//    @Parameter(description = "현재 불러온 메시지 중 가장 마지막 메시지 id")
      @RequestParam(required = false) Long lastMessageId,
//    @Parameter(description = "한 번에 보여주는 메시지 개수")
      @RequestParam(defaultValue = "20") int limit
  ) {
    List<ChatMessage> messages = chatService.getMessages(roomId, lastMessageId, limit);

    return ResponseEntity.ok(messages);
  }

// 채팅방 나가기
//     @Operation(summary = "채팅방 나가기", description = "채팅방을 나갑니다.")
//   @ApiResponses({
//       @ApiResponse(responseCode = "204", description = "채팅방 나가기 성공"),
//       @ApiResponse(responseCode = "400", description = "유효하지 않은 사용자 or 채팅방 ID"),
//       @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않음"),
//       @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//  })

  @PatchMapping("/list/exit/{roomId}")
  public ResponseEntity<Void> leaveChatRoom(
//    @Parameter(description = "나갈 채팅방 id")
      @PathVariable String roomId) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    String userId = authentication.getName();
    String userId = "user-010";

    chatService.leaveChatRoom(roomId, userId);
    return ResponseEntity.status(204).build();
  }

//채팅 메시지 읽음 처리
//     @Operation(summary = "채팅 메시지 읽음 처리", description = "채팅방 입장 시 메시지들 읽음 처리를 합니다..")
//   @ApiResponses({
//       @ApiResponse(responseCode = "204", description = "채팅 메시지 읽음 처리 성공"),
//       @ApiResponse(responseCode = "400", description = "유효하지 않은 사용자 or 채팅방 ID"),
//       @ApiResponse(responseCode = "404", description = "채팅방이 존재하지 않음"),
//       @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//  })

  @PutMapping(value = "/room/{roomId}/read")
  public ResponseEntity<Void> markAsRead(
//    @Parameter(description = "채팅방 ID")
      @PathVariable String roomId) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    String userId = authentication.getName();
    String userId = "user-001";
    log.info("ChatController - markAsRead");

    chatService.markAsRead(roomId, userId);
    return ResponseEntity.noContent().build(); // 204
  }

}

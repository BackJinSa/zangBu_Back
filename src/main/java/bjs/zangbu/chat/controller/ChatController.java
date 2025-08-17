package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.service.ChatServiceImpl;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.dto.request.ChatRequest;
import bjs.zangbu.chat.vo.ChatRoom;
import java.util.List;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
// @Tag(name="Chat Rest API", description = "채팅 관련 기능 API")
public class ChatController {

  private final ChatService chatService;

   //채팅방 생성 후 반환, 이미 채팅방 존재하면 그 채팅방 반환
     @ApiOperation(value = "채팅방 생성", notes = "채팅방을 생성합니다.", response = ChatRoom.class)
   @ApiResponses(value = {
       @ApiResponse(code = 201, message = "채팅방 생성 성공 후 반환(이미 채팅방 존재하면 기존 채팅방 반환)"),
       @ApiResponse(code = 400, message = "채팅방 생성 실패"),
       @ApiResponse(code = 500, message = "서버 내부 오류")
})

  @PostMapping(value = "/room/{buildingId}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<ChatRoom> createChatRoom(@PathVariable Long buildingId) {
    //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //String consumerId = authentication.getName();  //TODO: 테스트용, 나중에 주석 취소
    String consumerId = "8h9i0j1k-1111-2222-3333-444455556673";

    log.info("ChatController - createChatRoom");
    ChatRoom room = chatService.createChatRoom(buildingId, consumerId);
    return ResponseEntity.status(HttpStatus.CREATED).body(room);
  }

// 사용자가 참여하고 있는 채팅방 목록 조회
     @ApiOperation(value = "채팅방 목록 조회", notes = "사용자의 채팅방 목록을 조회합니다.")
   @ApiResponses(value = {
       @ApiResponse(code = 200, message = "채팅방 목록 조회 성공"),
       @ApiResponse(code = 400, message = "채팅방 목록 조회 실패"),
       @ApiResponse(code = 500, message = "서버 내부 오류")
  })

  @GetMapping(value=  "/list",  produces = "application/json;charset=UTF-8")
  public  ResponseEntity<ChatResponse.ChatRoomListPage> getChatRoomList(
    @ApiParam(value = "판매 타입", example = "ALL or BUY or SELL")
      @RequestParam String type,
    @ApiParam(value = "페이지 번호")
      @RequestParam int page,
    @ApiParam(value = "한 페이지 당 표시할 개수")
      @RequestParam int size) {
    log.info("ChatController - getChatRoomList");

    //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //String userId = authentication.getName();  //TODO: 테스트하느라 주석처리함
    String userId = "8h9i0j1k-1111-2222-3333-444455556673";

    // 프론트 → 매퍼 타입 매핑
    String mapped = switch (type) {
      case "BUY"  -> "BUYER";
      case "SELL" -> "SELLER";
      default     -> "ALL";
    };

    List<ChatResponse.ChatRoomListResponse> roomList = chatService.getChatRoomList(userId, mapped,
        page, size);

    // 전체 개수/hasNext
    long total = chatService.countChatRoomList(userId, mapped);
    boolean hasNext = (long) page * size < total;

// 탭 카운트
    ChatResponse.ChatRoomListPage.Counts counts = ChatResponse.ChatRoomListPage.Counts.builder()
            .ALL(ChatResponse.ChatRoomListPage.SimpleCount.builder()
                    .count((int) chatService.countChatRoomList(userId, "ALL"))
                    .unread(chatService.countUnreadRooms(userId, "ALL"))
                    .build())
            .BUY(ChatResponse.ChatRoomListPage.SimpleCount.builder()
                    .count((int) chatService.countChatRoomList(userId, "BUYER"))
                    .unread(chatService.countUnreadRooms(userId, "BUYER"))
                    .build())
            .SELL(ChatResponse.ChatRoomListPage.SimpleCount.builder()
                    .count((int) chatService.countChatRoomList(userId, "SELLER"))
                    .unread(chatService.countUnreadRooms(userId, "SELLER"))
                    .build())
            .build();

    ChatResponse.ChatRoomListPage body = ChatResponse.ChatRoomListPage.builder()
            .items(roomList)
            .total(total)
            .hasNext(hasNext)
            .counts(counts)
            .build();


    return ResponseEntity.ok(body);
  }

// 채팅방 상세 정보 조회
     @ApiOperation(value = "채팅방 상세 조회", notes = "채팅방의 상세 정보를 조회합니다.")
   @ApiResponses(value = {
       @ApiResponse(code = 200, message = "채팅방 상세 정보 조회 성공"),
       @ApiResponse(code = 404, message = "해당 id에 대한 채팅방이 없음"),
       @ApiResponse(code = 500, message = "서버 내부 오류")
  })

  @GetMapping("/room/info/{roomId}")
  public ResponseEntity<ChatRoom> getChatRoomDetail(
//    @Parameter(description = "조회할 채팅방 id")
      @PathVariable String roomId) {
    ChatRoom room = chatService.getChatRoomDetail(roomId);
    return ResponseEntity.status(200).body(room);
  }

//채팅방 입장 시 채팅방의 메시지들 조회 (한 번에 보여주는 개수는 기본 20개)
     @ApiOperation(value = "채팅방 메시지들 조회", notes = "채팅방의 메시지들을 조회합니다.")
   @ApiResponses(value = {
       @ApiResponse(code = 200, message = "채팅방 메시지들 조회 성공"),
       @ApiResponse(code = 400, message = "채팅방 ID 또는 파라미터 오류"),
       @ApiResponse(code = 404, message = "채팅방이 존재하지 않음"),
       @ApiResponse(code = 500, message = "서버 내부 오류")
  })

  @GetMapping(value = "/room/{roomId}", produces = "application/json")
  public ResponseEntity<List<ChatMessage>> getMessages(
    @ApiParam(value = "조회할 채팅방 id")
      @PathVariable String roomId,
    @ApiParam(value = "현재 불러온 메시지 중 가장 마지막 메시지 id")
      @RequestParam(required = false) Long lastMessageId,
    @ApiParam(value = "한 번에 보여주는 메시지 개수")
      @RequestParam(defaultValue = "20") int limit
  ) {
    List<ChatMessage> messages = chatService.getMessages(roomId, lastMessageId, limit);

    return ResponseEntity.ok(messages);
  }

// 채팅방 나가기
     @ApiOperation(value = "채팅방 나가기", notes = "채팅방을 나갑니다.")
   @ApiResponses(value = {
       @ApiResponse(code = 204, message = "채팅방 나가기 성공"),
       @ApiResponse(code = 400, message = "유효하지 않은 사용자 or 채팅방 ID"),
       @ApiResponse(code = 404, message = "채팅방이 존재하지 않음"),
       @ApiResponse(code = 500, message = "서버 내부 오류")
  })

  @PatchMapping("/list/exit/{roomId}")
  public ResponseEntity<Void> leaveChatRoom(
    @ApiParam(value = "나갈 채팅방 id")
      @PathVariable String roomId) {
    //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //String userId = authentication.getName();
    String userId = "8h9i0j1k-1111-2222-3333-444455556673";

    chatService.leaveChatRoom(roomId, userId);
    return ResponseEntity.status(204).build();
  }

//채팅 메시지 읽음 처리
     @ApiOperation(value = "채팅 메시지 읽음 처리", notes = "채팅방 입장 시 메시지들 읽음 처리를 합니다..")
   @ApiResponses(value = {
       @ApiResponse(code = 204, message = "채팅 메시지 읽음 처리 성공"),
       @ApiResponse(code = 400, message = "유효하지 않은 사용자 or 채팅방 ID"),
       @ApiResponse(code = 404, message = "채팅방이 존재하지 않음"),
       @ApiResponse(code = 500, message = "서버 내부 오류")
  })

  @PutMapping(value = "/room/{roomId}/read")
  public ResponseEntity<Void> markAsRead(
    @ApiParam(value = "채팅방 ID")
      @PathVariable String roomId) {
    //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
   //String userId = authentication.getName();
    String userId = "8h9i0j1k-1111-2222-3333-444455556673";
    log.info("ChatController - markAsRead");

    chatService.markAsRead(roomId, userId);
    return ResponseEntity.noContent().build(); // 204
  }

    @DeleteMapping("/room/{chatRoomId}")
    @Transactional
    public ResponseEntity<Void> deleteChatRoom(@PathVariable String chatRoomId) {
        int affected = chatService.deleteChatRoom(chatRoomId);
        if (affected == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/room/{buildingId}/{consumerId}")
    public ResponseEntity<ChatResponse.ChatRoomExistResponse> existsChatRoom(@PathVariable Long buildingId, @PathVariable String consumerId) {
        ChatRoom chatRoom = chatService.existsChatRoom(buildingId, consumerId);
        if (chatRoom != null) {
            return ResponseEntity.ok(new ChatResponse.ChatRoomExistResponse(true, chatRoom.getChatRoomId()));
        } else {
            return ResponseEntity.ok(new ChatResponse.ChatRoomExistResponse(false, null));
        }
    }

}

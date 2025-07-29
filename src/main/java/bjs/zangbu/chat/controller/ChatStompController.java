package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.dto.request.ChatRequest;
import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.vo.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatStompController {  //WebSocket 메시지 수신 컨트롤러

    private final ChatService chatService;
    //서버에서 클라이언트로 STOMP 메시지를 보낼 때 사용하는 도구
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send/{roomId}")
    public void handleSendMessage(
            //@MessageMapping 경로의 {roomId} 부분
            //@DestinationVariable은 @PathVariable의 WebSocket(STOMP) 버전 
            @DestinationVariable String roomId,
            //클라이언트가 보낸 채팅 메시지(JSON 형식) -> @Payload로 역직렬화됨.
            //@Payload는 메시지 본문 추출
            @Payload ChatRequest.SendMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String senderId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (senderId == null) {
            throw new IllegalArgumentException("인증된 사용자 정보가 없습니다.");
        }

        // 받은 메시지 DB에 저장 -> 클라이언트에게 보낼 메시지 형태인 응답 DTO(SendMessageResponse)로 가공
        ChatResponse.SendMessageResponse response = chatService.sendMessage(senderId, request);

        // 채팅방 구독자(/topic/chat/{roomId}를 구독하고 있는 모든 클라이언트)에게 메시지 브로드캐스트(response를 실시간 전송)
        messagingTemplate.convertAndSend("/exchange/chat.exchange/chat.room." + roomId, response);
    }

}

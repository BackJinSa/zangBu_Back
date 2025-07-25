package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.vo.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    //SimpMessagingTemplate가 @SendTo보다 유연한 처리 가능
    private final SimpMessagingTemplate messagingTemplate;

    //클라이언트가 /pub/chat/send/{roomId}로 메시지를 보낼 때 이 메소드가 처리
    @MessageMapping("/chat/send/{roomId}") //구독자에게 브로드캐스트
    //@Payload : 클라이언트가 보낸 JSON 데이터가 매핑되어 chatMessage 객체로 저장하는 걸 명시적으로 표시
    public ChatMessage sendMessage(
            @DestinationVariable String chatRoomId, @Payload ChatMessage chatMessage) {
        //chatRoomId 담아서 db에 ChatMessage 저장
        ChatMessage message = new ChatMessage(
                chatMessage.getChatMessageId(),
                chatRoomId,
                chatMessage.getBuildingId(),
                chatMessage.getSenderId(),
                chatMessage.getComplexId(),
                chatMessage.getMessage(),
                chatMessage.getCreatedAt()
        );
       
        chatService.sendMessage(message);       //메시지 db에 저장
        // 채팅방 구독자에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId, message);
        
        return message;
    }

}

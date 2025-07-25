package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.vo.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    //클라이언트가 /pub/chat.message로 메시지를 보낼 때 이 메소드가 처리
    @MessageMapping("/chat.message")
    @SendTo("/topic/chat/{roomId}")   //구독자에게 메시지 브로드캐스트
    //@Payload : 클라이언트가 보낸 JSON 데이터가 매핑되어 chatMessage 객체로 저장
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatService.sendMessage(chatMessage);       //메시지 db에 저장
        return chatMessage;
    }

}

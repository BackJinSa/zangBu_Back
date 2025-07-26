package bjs.zangbu.chat.controller;

import bjs.zangbu.chat.dto.request.ChatRequest;
import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.service.ChatProducer;
import bjs.zangbu.chat.service.ChatService;
import bjs.zangbu.chat.vo.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatProducer chatProducer;

    @MessageMapping("/chat.send")
    public void handleChat(ChatMessage message) {
        chatProducer.sendMessage(message);  //RabbitMQ로 전달
    }

}

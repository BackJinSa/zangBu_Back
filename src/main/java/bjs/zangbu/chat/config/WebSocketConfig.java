package bjs.zangbu.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /topic으로 시작하는 주소로 서버가 클라이언트에게 메시지를 전달하도록 설정
        //ex: /topic/chat/123을 구독하면 서버는 그 주소로 메시지르 보낼 수 있음.
        config.enableSimpleBroker("/topic");    //simpleBroker : Spring에서 제공하는 메모리 기반 브로커
        // 클라이언트가 메시지를 보낼 때 사용하는 경로의 접두어
        //ex: 클라이언트가 /pub/chat.message로 메시지를 보내면 Controller의 @MessageMapping("/chat.message")로 매핑
        config.setApplicationDestinationPrefixes("/pub");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //클라이언트가 WebSocket에 연결할 수 있는 엔드포인트를 등록
        registry.addEndpoint("/chat") // 접속 엔드포인트, ws://localhost:8080/chat
                .setAllowedOrigins("*"); // 모든 도메인에서 WebSocket 연결을 허용(CORS 허용), 배포할 때는 특정 도메인만 허용
    }
}

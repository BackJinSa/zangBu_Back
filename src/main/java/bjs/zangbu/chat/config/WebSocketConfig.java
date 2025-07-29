package bjs.zangbu.chat.config;

import bjs.zangbu.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProcessor jwtProcessor;

    //Spring은 메시지를 RabbitMQ로 넘기고, RabbitMQ가 구독자들에게 메시지를 브로드캐스트
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //클라이언트가 /topic으로 시작하는 경로를 구독하면 Spring 서버는 메시지를 자체 메모리가 아니라
        //RabbitMQ로 중계(relay)하도록 설정
        config.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")   //중계할 RabbitMQ 브로커의 호스트(보통 로컬 개발환경에서는 localhost)
                .setRelayPort(61613)         //STOMP 프로토콜을 사용하는 RabbitMQ 포트 (기본 STOMP 포트는 61613)
                .setClientLogin("guest")     //RabbitMQ 로그인 아이디 (기본값은 guest)
                .setClientPasscode("guest"); //RabbitMQ 로그인 비밀번호 (기본값은 guest)

        config.setApplicationDestinationPrefixes("/app");  // 클라이언트가 메시지 보낼 prefix
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //클라이언트가 WebSocket에 연결할 수 있는 엔드포인트(주소)를 등록
        registry.addEndpoint("/chat") //접속 엔드포인트, ws://localhost:8080/chat
                .addInterceptors(new JwtHandshakeInterceptor(jwtProcessor))  //인터셉터 등록
                .setAllowedOrigins("*") // 모든 도메인에서 WebSocket 연결을 허용(CORS 허용), 배포할 때는 특정 도메인만 허용
                .withSockJS();
    }
}

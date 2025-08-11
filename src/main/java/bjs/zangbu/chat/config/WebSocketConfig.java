package bjs.zangbu.chat.config;

import bjs.zangbu.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;

@Profile("!test")
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtProcessor jwtProcessor;

    //application.yml에서 불러옴
    @Value("${rabbitmq.stomp.username}")
    private String username;
    @Value("${rabbitmq.stomp.password}")
    private String password;
    @Value("${rabbitmq.stomp.host}")
    private String host;
    @Value("${rabbitmq.stomp.port}")
    private int port;

    //Spring은 메시지를 RabbitMQ로 넘기고, RabbitMQ가 구독자들에게 메시지를 브로드캐스트
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        //클라이언트가 /topic으로 시작하는 경로를 구독하면 Spring 서버는 메시지를 자체 메모리가 아니라
        //RabbitMQ로 중계(relay)하도록 설정
        config.enableStompBrokerRelay("/topic", "/queue", "/exchange")
                .setRelayHost(host)   //중계할 RabbitMQ 브로커의 호스트(보통 로컬 개발환경에서는 localhost)
                .setRelayPort(port)         //STOMP 프로토콜을 사용하는 RabbitMQ 포트 (기본 STOMP 포트는 61613)
                .setClientLogin(username)     //RabbitMQ 로그인 아이디
                .setClientPasscode(password); //RabbitMQ 로그인 비밀번호

        config.setApplicationDestinationPrefixes("/app");  // 클라이언트가 메시지 보낼 prefix
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //클라이언트가 WebSocket에 연결할 수 있는 엔드포인트(주소)를 등록
        registry.addEndpoint("/chat") //접속 엔드포인트, ws://localhost:8080/chat
//                .addInterceptors(new JwtHandshakeInterceptor(jwtProcessor))  //TODO: 인터셉터 등록 -> configureClientInboundChannel로 수정 : 테스트후 삭제하기
                .setAllowedOrigins("*"); // 모든 도메인에서 WebSocket 연결을 허용(CORS 허용), 배포할 때는 특정 도메인만 허용
    }

    // STOMP CONNECT 헤더에서 JWT 검증
    @Override
    public void configureClientInboundChannel(ChannelRegistration reg) {
        reg.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                var acc = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(acc.getCommand())) {
                    System.out.println("[WS] CONNECT native headers = " + acc.toNativeHeaderMap());
                    // ↓ Authorization / authorization 둘 다 시도
                    String auth = acc.getFirstNativeHeader("Authorization");
                    if (auth == null) auth = acc.getFirstNativeHeader("authorization");

                    if (auth != null && auth.startsWith("Bearer ")) {
                        String token = auth.substring(7);
                        // 임시: 토큰 앞 16자만 로깅(전부 찍지 말기)
                        System.out.println("[WS] token prefix = " + token.substring(0, Math.min(16, token.length())));
                        if (jwtProcessor.validateToken(token)) {
                            String userId = jwtProcessor.getEmail(token);
                            acc.setUser(new UsernamePasswordAuthenticationToken(userId, null, java.util.Collections.emptyList()));
                            return message;
                        } else {
                            System.out.println("[WS] validateToken=false");
                        }
                    } else {
                        System.out.println("[WS] Authorization header missing or bad format: " + auth);
                    }
                    throw new IllegalArgumentException("Invalid or missing JWT");
                }
                return message;
            }
        });
    }
}

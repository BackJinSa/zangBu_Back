package bjs.zangbu.chat.config;

import bjs.zangbu.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;

@Log4j2
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
                .setAllowedOrigins("*"); // 모든 도메인에서 WebSocket 연결을 허용(CORS 허용), 배포할 때는 특정 도메인만 허용
    }

    // STOMP CONNECT 헤더에서 JWT 검증
    @Override
    public void configureClientInboundChannel(ChannelRegistration reg) {
        reg.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);
                StompCommand cmd = acc.getCommand();
                if (cmd == null) return message;

                switch (cmd) {
                    case CONNECT: {
                        log.info("[WS][CONNECT] native headers={}", acc.toNativeHeaderMap());

                        String auth = acc.getFirstNativeHeader("Authorization");
                        if (auth == null) auth = acc.getFirstNativeHeader("authorization");

                        if (auth == null || !auth.startsWith("Bearer ")) {
                            throw new IllegalArgumentException("Invalid or missing JWT");
                        }

                        String token = auth.substring(7);
                        log.info("[WS][CONNECT] token prefix={}", token.substring(0, Math.min(16, token.length())));

                        // ❗검증 실패 원인 보이도록 try/catch 권장
                        if (!jwtProcessor.validateToken(token)) {
                            log.warn("[WS][CONNECT] validateToken=false");
                            throw new IllegalArgumentException("Invalid or missing JWT");
                        }

                        // Principal 주입 (email → 그대로 / 필요시 memberId로 변환)
                        String userId = jwtProcessor.getEmail(token); // 또는 getClaim(token, "member_id")
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userId, null, java.util.Collections.emptyList());
                        acc.setUser(authentication);

                        // 수정된 헤더로 메시지 재생성
                        return MessageBuilder.createMessage(message.getPayload(), acc.getMessageHeaders());
                    }

                    case SEND: {
                        if (acc.getUser() == null) {
                            throw new org.springframework.security.access.AccessDeniedException("Unauthenticated SEND");
                        }
                        log.info("[WS][SEND] user={}, dest={}", principalName(acc), acc.getDestination());
                        return message;
                    }

                    case SUBSCRIBE: {
                        log.info("[WS][SUBSCRIBE] user={}, dest={}", principalName(acc), acc.getDestination());
                        return message;
                    }

                    case DISCONNECT: {
                        log.info("[WS][DISCONNECT] user={}", principalName(acc));
                        return message;
                    }

                    default:
                        return message;
                }
            }

            private String principalName(StompHeaderAccessor acc) {
                java.security.Principal p = acc.getUser();
                return (p != null ? p.getName() : "null");
            }
        });
    }


}

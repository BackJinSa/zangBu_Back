package bjs.zangbu.chat.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "chat.queue";           //메시지를 저장하고 소비하는 큐 이름
    public static final String EXCHANGE_NAME = "chat.exchange";     //메시지를 전달할 때 거치는 라우터 역할
    public static final String ROUTING_KEY = "chat.key";            //메시지를 어떤 큐로 보낼지 지정하는 주소 역할

    @Bean
    public Queue chatQueue() {
        //QUEUE_NAME에 들어있는 값을 이름으로 가지는 큐 하나 생성
        //true: 지속성 o(서버가 꺼졌다 켜져도 큐 사라지지 않음)
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange chatExchange() {
        //EXCHANGE_NAME 값을 이름으로 하는 topic 익스체인지를 생성.
        //익스체인지 : 메시지를 큐에 전달해주는 중간 라우터.
        //라우팅 키 패턴을 기반으로 여러 큐에 메시지를 전달할 수 있음
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding chatBinding() {
        //익스체인지와 큐를 라우팅 키(chat.key)로 연결하는 설정
        //chat.exchange로 들어온 메시지 중 라우팅 키가 chat.key인 메시지를 chat.queue에 넣어줌.
        return BindingBuilder.bind(chatQueue()).to(chatExchange()).with(ROUTING_KEY);
    }
}

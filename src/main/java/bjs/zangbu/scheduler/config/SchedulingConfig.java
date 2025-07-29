package bjs.zangbu.scheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// 스프링 설정 클래스
@Configuration

// 스케줄러 어노테이션을 사용한다고 선언함
@EnableScheduling
public class SchedulingConfig {
    // 빈 등록 없어도 됨
}

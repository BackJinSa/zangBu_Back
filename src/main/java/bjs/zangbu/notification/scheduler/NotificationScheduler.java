package bjs.zangbu.notification.scheduler;

import bjs.zangbu.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    /**
     * 1. 시세 변동 감지 (매 3시간마다)
     */
    @Scheduled(cron = "0 0 0/3 * * *") // 매 3시간마다 실행
    public void detectPriceChanges() {
        log.info("시세 변동 감지 스케줄러 실행");
        notificationService.detectPriceChangeForAllBookmarks();
    }
}

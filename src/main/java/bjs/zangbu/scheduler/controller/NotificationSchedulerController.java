package bjs.zangbu.scheduler.controller;

import bjs.zangbu.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class NotificationSchedulerController {

  private final NotificationService notificationService;

  /**
   * 시세 변동 감지 (매 3시간마다)
   */
  @Scheduled(cron = "0 0 0/3 * * *") // 매 3시간마다 실행
  public void detectPriceChanges() {
    log.info("시세 변동 감지 스케줄러 실행");
    notificationService.detectPriceChangeForAllBookmarks();
  }
}

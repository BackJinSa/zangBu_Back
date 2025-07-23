package bjs.zangbu.notification.service;

import bjs.zangbu.notification.dto.response.NotificationResponse;
import bjs.zangbu.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    // ====================== API 전용 ======================
    // [API] 전체 알림 조회
    @Override
    public NotificationResponse.All getAllNotifications(String userId) {
        return null;
    }

    // [API] 하나의 알림 읽음 처리 (DB 업데이트)
    @Override
    public boolean markAsRead(String userId, Long notificationId) {
        return false;
    }

    // [API] 전체 알림 읽음 처리 (DB 업데이트) return 값 : 읽음 처리된 알림 개수
    @Override
    public NotificationResponse.MarkAllReadResult markAllAsRead(String userId) {
        return null;
    }

    // [API] 알림 삭제 (DB 삭제)
    @Override
    public boolean removeNotification(String userId, Long notificationId) {
        return false;
    }

    // ====================== 트리거 전용 ===========================

    // 1. 시세 변동 감지
    // 2. 스케줄러 + FCM 메시지 발송(예정)
    @Override
    public void detectPriceChangeForAllBookmarks() {

    }

    // 1. 실거래 발생 감지
    // 2. 스케줄러 + FCM 메시지 발송(예정)
    @Override
    public void detecTradeHappenedTody() {

    }

    // 1. 리뷰 등록 감지
    // 2. 실시간 트리거 (Review 등록 서비스 내부에서 실행) + FCM 메시지 발송(예정)
    @Override
    public void notificationReviewRegisterd(Long buildingId) {

    }
}

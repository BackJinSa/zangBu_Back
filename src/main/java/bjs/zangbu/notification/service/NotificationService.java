package bjs.zangbu.notification.service;

import bjs.zangbu.notification.dto.response.NotificationResponse;
import bjs.zangbu.notification.vo.Notification;

import java.util.List;

public interface NotificationService {

    // ====================== API 전용 ======================
    // [API] 전체 알림 조회
    NotificationResponse.All getAllNotifications(String userId);

    // [API] 하나의 알림 읽음 처리 (DB 업데이트)
    boolean markAsRead(String userId, Long notificationId);

    // [API] 전체 알림 읽음 처리 (DB 업데이트) return 값 : 읽음 처리된 알림 개수
    NotificationResponse.MarkAllReadResult markAllAsRead(String userId);  // 처리된 개수 반환

    // [API] 알림 삭제 (DB 삭제)
    boolean removeNotification(String userId, Long notificationId);

    // ====================== 트리거 전용 ===========================

    // [트리거] 시세 변동 감지 (스케줄러 + FCM 메시지 발송)
    void detectPriceChangeForAllBookmarks();

    // [트리거] 실거래 발생 감지 (스케줄러 + FCM 메시지 발송)
    void detecTradeHappenedTody();

    // [실시간 트리거] 리뷰 등록 감지
    // (Review 등록 서비스 내부에서 실행 + FCM 메시지 발송)
    void notificationReviewRegisterd(Long buildingId);

}

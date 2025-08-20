package bjs.zangbu.notification.service;

import bjs.zangbu.notification.dto.response.NotificationResponse.*;

public interface NotificationService {

    // ====================== API 전용 ======================

    /** [API] 전체 알림 조회 */
    NotificationAll getAllNotifications(String memberId, String type);

    /** [API] 하나의 알림 읽음 처리 */
    boolean markAsRead(String memberId, Long notificationId);

    /** [API] 전체 알림 읽음 처리 (읽음 처리된 개수 반환) */
    MarkAllReadResult markAllAsRead(String memberId);

    /** [API] 알림 삭제 */
    boolean removeNotification(String memberId, Long notificationId);

    // ====================== 트리거 전용 ===========================

    /** [트리거] 시세 변동 감지 (스케줄러 + FCM 메시지 발송) */
    void detectPriceChangeForAllBookmarks();

    /** [트리거] 실거래 발생 감지 (스케줄러 + FCM 메시지 발송) */
    void detectTradeHappenedNow(Long dealId);

    /** [트리거] 리뷰 등록 감지 (즉시 실행 + FCM 메시지 발송) */
    void notificationReviewRegisterd(Long buildingId);

    // ====================== 내부 공용 기능 ===========================

    // 중복 알림 여부를 확인한 뒤 알림 저장 및 FCM 전송

    void sendNotificationIfNotExists(String memberId,
                                     bjs.zangbu.building.vo.Building building,
                                     bjs.zangbu.notification.vo.Type type,
                                     String title,
                                     String message,
                                     int currentPrice);

    // ======================== 채팅 알림 전송 ==============================
    void sendChatNotification(String memberId, String roomId, String message);
}

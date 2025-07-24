package bjs.zangbu.notification.mapper;

import bjs.zangbu.notification.vo.Notification;

import java.util.List;

public interface NotificationMapper {
    // ====================== API 전용 ======================

    // 전체 알림 조회
    List<Notification> getAllNotifications(String userId);

    // 알림 읽음 처리
    int updateIsRead(String userId, Long notificationId);

    // 전체 알림 읽음 처리
    int updateAllIsRead(String userId);

    // 알림 삭제
    int removeNotification(String userId, Long notificationId);

    // ====================== 트리거 전용 ===========================

    // 알림 등록 (시세 변동, 실거래, 리뷰 모두 같은 쿼리 사용)
    int insertNotification(Notification notification);


}

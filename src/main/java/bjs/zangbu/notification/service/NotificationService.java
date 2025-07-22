package bjs.zangbu.notification.service;

import bjs.zangbu.notification.dto.response.NotificationResponse;
import bjs.zangbu.notification.vo.Notification;

import java.util.List;

public interface NotificationService {

    // 전체 알림 조회
    List<NotificationResponse.NotificationElement> getAllNotifications(String userId);

    // 찜한 매물 시세 변동 감지
    void notificationPriceChanged(Long buildingId, int oldPrice, int newPrice);

    // 실거래 발생 감지
    void notificationTradeHappened(Long buiildingId, int price);

    // 리뷰 등록 알림
    void notificationReviewPosted(Long buildingId);

    // 하나의 알림 읽음 처리
    void bookmarkRead(String userId, Long notificationId);

    // 알림 전체 읽음 처리
    void bookmarkAllRead(String userId);

    // 알림 삭제
    void removeNotification(String userId, Long notificationId);

    // 매물에 대한 알림 등록
    void registerNotification(String userId, Long buildingId);


}

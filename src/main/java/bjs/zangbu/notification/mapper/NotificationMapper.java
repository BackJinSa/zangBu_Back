package bjs.zangbu.notification.mapper;

import bjs.zangbu.notification.vo.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotificationMapper {
    // ====================== API 전용 ======================

    // 전체 알림 조회
    List<Notification> selectAllByMemberId(String memberId);

    // 하나의 알림 읽음 처리
    int updateIsRead(@Param("memberId") String memberId, @Param("notificationId") Long notificationId);

    // 전체 알림 읽음 처리
    int updateAllIsRead(String memberId);

    // 알림 삭제
    int removeNotification(@Param("memberId") String memberId, @Param("notificationId") Long notificationId);

    // ====================== 트리거 전용 ===========================

    // 알림 등록 (시세 변동, 실거래, 리뷰 모두 같은 쿼리 사용)
    int insertNotification(Notification notification);

    // 오늘 같은 건물/가격/타입의 알림이 있는지 확인 (시세 변동 중복 방지)
    boolean existsSamePriceNotificationToday(@Param("memberId") String memberId,
                                             @Param("buildingId") Long buildingId,
                                             @Param("type") String type,
                                             @Param("price") int price);

}

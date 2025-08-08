package bjs.zangbu.notification.mapper;

//
//import bjs.zangbu.global.config.RootConfig;
//import bjs.zangbu.notification.vo.Notification;
//import bjs.zangbu.notification.vo.SaleType;
//import bjs.zangbu.notification.vo.Type;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {RootConfig.class})
//@Transactional
//@Log4j2
class NotificationMapperTest {
//
//    @Autowired
//    NotificationMapper notificationMapper;
//
//    @Test
//    void selectAllByMemberId() {
//        String memberId = "testUser";
//        List<Notification> list = notificationMapper.selectAllByMemberId(memberId);
//        assertNotNull(list);
//        log.info("조회된 알림 개수: {}", list.size());
//    }
//
//    @Test
//    void updateIsRead() {
//        String memberId = "testUser";
//        Long notificationId = 1L;
//        int updated = notificationMapper.updateIsRead(memberId, notificationId);
//        assertEquals(1, updated);
//        log.info("알림 읽음 처리 결과: {}", updated);
//    }
//
//    @Test
//    void updateAllIsRead() {
//        String memberId = "testUser";
//        int updatedCount = notificationMapper.updateAllIsRead(memberId);
//        log.info("전체 알림 읽음 처리 결과: {}", updatedCount);
//        assertTrue(updatedCount >= 0);
//    }
//
//    @Test
//    void removeNotification() {
//        String memberId = "testUser";
//        Long notificationId = 1L;
//        int deleted = notificationMapper.removeNotification(memberId, notificationId);
//        log.info("알림 삭제 결과: {}", deleted);
//        assertTrue(deleted >= 0);
//    }
//
//    @Test
//    void insertNotification() {
//        Notification notification = new Notification(
//                null,
//                "테스트 알림입니다.",
//                false,
//                Type.BUILDING,
//                new Date(),
//                SaleType.TRADING,
//                100000,
//                "서울시 강남구",
//                0,
//                "testUser",
//                1L
//        );
//        int inserted = notificationMapper.insertNotification(notification);
//        log.info("알림 삽입 결과: {}", inserted);
//        assertEquals(1, inserted);
//    }
//
//    @Test
//    void existsSamePriceNotificationToday() {
//        boolean exists = notificationMapper.existsSamePriceNotificationToday(
//                "testUser", 1L, "BUILDING", 100000
//        );
//        log.info("오늘 중복 알림 존재 여부: {}", exists);
//        assertNotNull(exists);
//    }
}
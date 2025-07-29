package bjs.zangbu.notification.mapper;

import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.notification.vo.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class })
class NotificationMapperTest {

    @Autowired
    private NotificationMapper notificationMapper;

    @Test
    void testSelectAllNotifications() {
        // given
        String memberId = "testUser"; // 실제 user 테이블에 존재하는 userId

        // when
        List<Notification> result = notificationMapper.selectAllByMemberId(memberId);

        // then
        assertNotNull(result, "알림 목록은 null이면 안 됩니다.");
        assertFalse(result.isEmpty(), "알림이 최소 1개 이상 있어야 합니다.");
    }

    @Test
    void a() {
        System.out.println("aaaa");
    }
}
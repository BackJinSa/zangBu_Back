package bjs.zangbu.notification.service;

import bjs.zangbu.bookmark.service.BookMarkService;
import bjs.zangbu.fcm.service.FcmSender;
import bjs.zangbu.notification.dto.response.NotificationResponse.NotificationAll;
import bjs.zangbu.notification.mapper.NotificationMapper;
import bjs.zangbu.notification.vo.Notification;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * 간단 통합 테스트 (Mock 없음)
 * - 실제 MySQL + MyBatis + Service 빈 주입
 * - DB에 이미 더미데이터가 있다고 가정
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = NotificationServiceImplTest.Config.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
@Slf4j
class NotificationServiceImplTest {

    private static final String MEMBER_ID = "a0030658-24f8-42cd-8d78-a9fd06bf02b2";

    @Configuration
    @EnableTransactionManagement
    @MapperScan(basePackages = {
            "bjs.zangbu.notification.mapper",
            "bjs.zangbu.fcm.mapper",
            "bjs.zangbu.member.mapper",
            "bjs.zangbu.building.mapper",
            "bjs.zangbu.deal.mapper",
            "bjs.zangbu.review.mapper",
            "bjs.zangbu.bookmark.mapper"
    })
    @ComponentScan(
            basePackageClasses = { bjs.zangbu.notification.service.NotificationServiceImpl.class },
            // 빌딩 서비스 등 불필요한 빈은 아예 스캔 제외
            excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
                    "bjs\\.zangbu\\.building\\.service\\..*"
            })
    )
    static class Config {
        @Bean
        public DataSource dataSource() {
            HikariConfig cfg = new HikariConfig();
            cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
            cfg.setJdbcUrl("jdbc:mysql://localhost:3306/zangbu?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8");
            cfg.setUsername("zangbu");
            cfg.setPassword("12341234");
            cfg.setMaximumPoolSize(5);
            return new HikariDataSource(cfg);
        }

        @Bean
        public org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory(ApplicationContext ctx, DataSource ds) throws Exception {
            SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
            fb.setDataSource(ds);
            fb.setConfigLocation(ctx.getResource("classpath:/mybatis-config.xml"));
            return fb.getObject();
        }

        @Bean
        public DataSourceTransactionManager transactionManager(DataSource ds) {
            return new DataSourceTransactionManager(ds);
        }
    }

    // 서비스가 가지고 있지만 이 테스트에선 안 쓰는 의존성만 막기
    @MockBean
    BookMarkService bookMarkService;
    @MockBean
    FcmSender fcmSender;
    @MockBean
    RedisTemplate<String, Object> redisTemplate;

    // 실제 빈 주입
    @Resource
    private NotificationServiceImpl notificationService;

    @Resource
    private NotificationMapper notificationMapper;

    /*
    * getAllNotifications(String memberId)
    * */
    @Test
    @Order(1)
    @DisplayName("getAllNotifications: DTO 반환 확인")
    void getAllNotifications() {
        NotificationAll all = notificationService.getAllNotifications(MEMBER_ID);
        log.info("NotifiactionAll {}", all);
        assertNotNull(all);
    }

    /*
    *
    * */
    @Test
    @Order(2)
    @DisplayName("markAsRead: 단건 읽음 처리")
    void markAsRead() {
        List<Notification> list = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assumeTrue(!list.isEmpty(), "알림 더미데이터가 필요합니다.");
        Long targetId = list.get(0).getNotificationId();

        boolean ok = notificationService.markAsRead(MEMBER_ID, targetId);
        assertTrue(ok);

        Notification after = notificationMapper.selectAllByMemberId(MEMBER_ID).stream()
                .filter(n -> n.getNotificationId().equals(targetId))
                .findFirst().orElse(null);
        assertNotNull(after);
        assertTrue(after.isRead());
    }

    @Test
    @Order(3)
    @DisplayName("markAllAsRead: 전체 읽음 처리")
    void markAllAsRead() {
        var result = notificationService.markAllAsRead(MEMBER_ID);
        assertNotNull(result);

        List<Notification> list = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertTrue(list.stream().allMatch(Notification::isRead));
    }

    @Test
    @Order(4)
    @DisplayName("removeNotification: 단건 삭제")
    void removeNotification() {
        List<Notification> list = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assumeTrue(!list.isEmpty(), "삭제할 알림 더미데이터가 필요합니다.");
        Long targetId = list.get(0).getNotificationId();

        boolean ok = notificationService.removeNotification(MEMBER_ID, targetId);
        assertTrue(ok);

        List<Notification> left = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertTrue(left.stream().noneMatch(n -> n.getNotificationId().equals(targetId)));
    }

    @Test
    void sendChatNotification() {
    }

    @Test
    void sendNotificationIfNotExists() {
    }

    @Test
    void detectPriceChangeForAllBookmarks() {
    }

    @Test
    void detectTradeHappenedNow() {
    }

    @Test
    void notificationReviewRegisterd() {
    }
}
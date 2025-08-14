package bjs.zangbu.notification.mapper;

import bjs.zangbu.notification.vo.Notification;
import bjs.zangbu.notification.vo.SaleType;
import bjs.zangbu.notification.vo.Type;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationMapper 단위 테스트
 * - 스프링 레거시 환경에서 MyBatis Mapper만 로딩하여 DB 연동 테스트
 * - MySQL 실제 DB를 사용
 */
@ExtendWith(SpringExtension.class) // JUnit5 + Spring TestContext Framework 통합
@ContextConfiguration(classes = NotificationMapperTest.MyBatisTestConfig.class) // MyBatis 전용 설정 클래스 로드
//@Transactional // 각 테스트 실행 후 롤백 (DB 변경 사항 테스트 간 독립성 유지)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // @Order로 테스트 실행 순서 고정
@Log4j2
class NotificationMapperTest {

    /**
     * MyBatis + HikariCP + 트랜잭션 매니저 설정
     * - 스프링 부트가 아니므로 수동으로 Bean 등록
     */
    @Configuration
    @MapperScan(basePackages = "bjs.zangbu.notification.mapper") // Mapper 인터페이스 스캔
    static class MyBatisTestConfig {
        @Bean
        public DataSource dataSource() {
            // HikariCP 연결 설정
            HikariConfig cfg = new HikariConfig();
            cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
            cfg.setJdbcUrl("jdbc:mysql://localhost:3306/zangbu?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8");
            cfg.setUsername("zangbu");      // DB 계정
            cfg.setPassword("12341234");    // DB 비밀번호
            cfg.setMaximumPoolSize(5);
            return new HikariDataSource(cfg);
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory(ApplicationContext ctx, DataSource ds) throws Exception {
            // MyBatis SqlSessionFactoryBean 등록
            SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
            fb.setDataSource(ds);
            // MyBatis 설정 파일 경로 (매핑, ENUM 핸들러 등)
            fb.setConfigLocation(ctx.getResource("classpath:/mybatis-config.xml"));
            return fb.getObject();
        }

        @Bean
        public DataSourceTransactionManager transactionManager(DataSource ds) {
            // @Transactional 지원을 위한 트랜잭션 매니저
            return new DataSourceTransactionManager(ds);
        }
    }

    // Mapper 주입
    @Resource
    private NotificationMapper notificationMapper;

    // 테스트용 회원 ID (DB에 미리 더미 데이터 존재해야 함)
    private static final String MEMBER_ID = "a0030658-24f8-42cd-8d78-a9fd06bf02b2";


    /**
     * 회원 알림 전체 조회 테스트
     * - DB에 a0030658-24f8-42cd-8d78-a9fd06bf02b2의 알림이 1건 이상 존재해야 성공
     */
    @Test
    @Order(1)
    @DisplayName("selectAllByMemberId: 회원 알림 목록 조회")
    void selectAllByMemberId() {
        List<Notification> list = notificationMapper.selectAllByMemberId(MEMBER_ID, "");
        log.info("조회된 알림 개수 = {}", list.size());

        // 기대값: 알림이 최소 1건 이상
        assertEquals(19, list.size());
        // 모든 결과가 해당 MEMBER_ID를 가져야 함
        assertTrue(list.stream().allMatch(n -> MEMBER_ID.equals(n.getMemberId())));
    }

    /**
     * 전체 알림 읽음 처리 테스트
     * - 해당 회원의 모든 미읽음 알림을 읽음 처리
     */
    @Test
    @Order(2)
    @DisplayName("updateAllIsRead: 전체 알림 읽음 처리")
    void updateAllIsRead() {
        int updated = notificationMapper.updateAllIsRead(MEMBER_ID);
        // 최소 1건 이상 업데이트되어야 함
        assertTrue(updated >= 1);

        // 모든 알림이 읽음 상태인지 검증
        List<Notification> list = notificationMapper.selectAllByMemberId(MEMBER_ID, "");
        assertTrue(list.stream().allMatch(Notification::isRead));
    }

    /**
     * 단일 알림 읽음 처리 테스트
     * - 첫 번째 알림의 isRead 값을 true로 변경 후 검증
     */
    @Test
    @Order(3)
    @DisplayName("updateIsRead: 단일 알림 읽음 처리")
    void updateIsRead() {
        // 1) 테스트 대상 알림 ID 가져오기
        Long targetId = notificationMapper.selectAllByMemberId(MEMBER_ID, "").get(0).getNotificationId();
        // 2) 해당 알림 읽음 처리
        int updated = notificationMapper.updateIsRead(MEMBER_ID, targetId);
        assertEquals(1, updated);
        // 3) 다시 조회하여 isRead 값 검증
        Notification after = notificationMapper.selectAllByMemberId(MEMBER_ID, "").stream()
                .filter(n -> n.getNotificationId().equals(targetId))
                .findFirst().orElseThrow();
        assertTrue(after.isRead());
    }

    /**
     * 신규 알림 삽입 테스트
     * - INSERT 후 해당 메시지가 존재하는지 확인
     */
    @Test
    @Order(4)
    @DisplayName("insertNotification: 신규 알림 삽입")
    void insertNotification() {
        Notification n = new Notification(
                null,                       // PK (Auto Increment)
                "관심 매물의 시세가 변동되었습니다.",            // message
                false,                      // isRead
                Type.BUILDING,               // type ENUM
                new Date(),                  // createdAt
                SaleType.MONTHLY,            // saleType ENUM
                34500,                       // price
                "송파구 잠실동",               // address
                3,                           // rank
                MEMBER_ID,                   // memberId
                1L                         // buildingId
        );
        int inserted = notificationMapper.insertNotification(n);
        assertEquals(1, inserted);

        // 삽입된 알림이 존재하는지 확인
        boolean exists = notificationMapper.selectAllByMemberId(MEMBER_ID, "").stream()
                .anyMatch(x -> "관심 매물의 시세가 변동되었습니다.".equals(x.getMessage()));
        assertTrue(exists);
    }

    /**
     * 오늘 동일 건물/타입/가격 알림 존재 여부 테스트
     * - 같은 조건이면 true, 다른 가격이면 false
     */
    @Test
    @Order(5)
    @DisplayName("existsSamePriceNotificationToday: 오늘 동일 시세 알림 중복 체크")
    void existsSamePriceNotificationToday() {
        boolean exists = notificationMapper.existsSamePriceNotificationToday(
                MEMBER_ID, 1L, Type.BUILDING.name(), 34500
        );
        assertTrue(exists);

        boolean notExists = notificationMapper.existsSamePriceNotificationToday(
                MEMBER_ID, 1L, Type.BUILDING.name(), 345000
        );
        assertFalse(notExists);
    }

    /**
     * 단일 알림 삭제 테스트
     * - 첫 번째 알림 삭제 후 목록에 존재하지 않는지 확인
     */
    @Test
    @Order(6)
    @DisplayName("removeNotification: 단일 삭제")
    void removeNotification() {
        Long targetId = notificationMapper.selectAllByMemberId(MEMBER_ID, "").get(0).getNotificationId();
        int deleted = notificationMapper.removeNotification(MEMBER_ID, targetId);
        assertEquals(1, deleted);

        boolean stillThere = notificationMapper.selectAllByMemberId(MEMBER_ID, "").stream()
                .anyMatch(n -> n.getNotificationId().equals(targetId));
        assertFalse(stillThere);
    }
}

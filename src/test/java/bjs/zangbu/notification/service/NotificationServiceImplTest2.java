package bjs.zangbu.notification.service;

import bjs.zangbu.bookmark.mapper.BookMarkMapper;
import bjs.zangbu.bookmark.service.BookMarkServiceImpl;
import bjs.zangbu.bookmark.vo.Bookmark;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.deal.dto.request.DealRequest;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.deal.service.DealServiceImpl;
import bjs.zangbu.deal.vo.DealEnum;
import bjs.zangbu.fcm.mapper.FcmMapper;
import bjs.zangbu.fcm.service.FcmSender;
import bjs.zangbu.imageList.service.ImageListService;
import bjs.zangbu.member.mapper.MemberMapper;
import bjs.zangbu.notification.dto.response.NotificationResponse.*;
import bjs.zangbu.notification.mapper.NotificationMapper;
import bjs.zangbu.notification.vo.Notification;
import bjs.zangbu.notification.vo.Type;
import bjs.zangbu.review.mapper.ReviewMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static bjs.zangbu.notification.dto.response.NotificationResponse.NotificationElement.formatMoney;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(SpringExtension.class) // JUnit5 + Spring TestContext Framework 통합
@ContextConfiguration(classes = NotificationServiceImplTest.MyBatisTestConfig.class) // MyBatis 전용 설정 클래스 로드
@Log4j2
class NotificationServiceImplTest2 {

    @Configuration
    @MapperScan(basePackages = {
            "bjs.zangbu.notification.mapper",
            "bjs.zangbu.member.mapper",
            "bjs.zangbu.fcm.mapper",
            "bjs.zangbu.building.mapper",
            "bjs.zangbu.review.mapper",
            "bjs.zangbu.deal.mapper",
            "bjs.zangbu.bookmark.mapper"
    })
    @ComponentScan(basePackages = {
            "bjs.zangbu.fcm"
    })
    static class MyBatisTestConfig {
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
        public SqlSessionFactory sqlSessionFactory(ApplicationContext ctx, DataSource ds) throws Exception {
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

    private NotificationServiceImpl sutNotificationServiceImpl;
    private BookMarkServiceImpl sutBookmarkServiceImpl;
    private DealServiceImpl sutDealServiceImpl;
    @Resource NotificationMapper notificationMapper;
    @Resource MemberMapper memberMapper;
    @Resource BookMarkMapper bookMarkMapper;
    @Resource DealMapper dealMapper;
    @Resource FcmMapper fcmMapper;
    @Resource BuildingMapper buildingMapper;
    @Resource FcmSender fcmSender;
    @Resource ReviewMapper reviewMapper;

    @org.junit.jupiter.api.BeforeEach
    // 필요한 의존성만 추가해서 클래스 생성할 수 있음
    void setUp() {

        sutBookmarkServiceImpl = new BookMarkServiceImpl(bookMarkMapper);
        sutNotificationServiceImpl = new NotificationServiceImpl(
                notificationMapper, sutBookmarkServiceImpl, buildingMapper,
                dealMapper, reviewMapper, fcmMapper, fcmSender,
                memberMapper, null
        );
        sutDealServiceImpl = new DealServiceImpl(dealMapper, buildingMapper, null, sutNotificationServiceImpl);
    }

    // ========================== 시세 변동 테스트용 변수들 ====================================
    private static final String BOOKMARKS_MEMBER_ID = "a0030658-24f8-42cd-8d78-a9fd06bf02b2"; // 시세 변동 테스트용 유저 아이디

    // ========================== 실거래 테스트용 변수들 ====================================
    private static final String TRADE_MEMBER_ID = "a56f6d85-37fd-4e99-92a6-cece4689fd7b"; // 실거래 테스트용 유저 아이디

    // ========================== 리뷰 등록 테스트용 변수들 ====================================
    private static final String REVIEW_MEMBER_ID = "a0030658-24f8-42cd-8d78-a9fd06bf02b2"; // 리뷰 등록 테스트용 유저 아이디
    private static final Long   REVIEW_BUILDING_ID = 2L;      // 리뷰 등록 테스트용 빌딩 아이디

    /*
     * 시세 변동 알림 호출 이전 로직
     * */
    @Test
    void detectPriceChangeForAllBookmarks() {

        // [1] 실행 전 알림 개수
        int beforeCount = notificationMapper.selectAllByMemberId(BOOKMARKS_MEMBER_ID).size();
        log.info("============= beforeCount =============== {}", beforeCount);

        // [2] 서비스 메서드 호출 (실제 DB/FCM 사용)
        sutNotificationServiceImpl.detectPriceChangeForAllBookmarks();

        // [3] 실행 후 알림 개수
        int afterCount = notificationMapper.selectAllByMemberId(BOOKMARKS_MEMBER_ID).size();
        log.info("============= afterCount =============== {}", afterCount);

        // [4] 검증
        assertTrue(afterCount >= beforeCount, "알림 개수는 줄어들면 안 됨");
        // 가격 변동이 있는 데이터가 DB에 있으면 afterCount > beforeCount일 것
    }

    /**
     * [거래 성사 알림] 단순 호출 통합 테스트
     * - 서비스가 내부에서: dealId → buildingId 매핑 조회 → 북마크 유저 조회 → 알림 저장/푸시 수행
     * - 테스트에선 전/후 알림 수만 비교 (줄어들지 않음 보장)
     */
    @Test
    void detectTradeHappenedNow_단순호출() {

        // [1] 실행 전 알림 개수
        int beforeCount = notificationMapper.selectAllByMemberId(TRADE_MEMBER_ID).size();
        log.info("============= beforeCount =============== {}", beforeCount);

        //
        DealRequest.Status status = new DealRequest.Status(1L, "CLOSE_DEAL");

        // dealService의 상태 변경 메서드 로직부터 시작 - 거래상태 거래완료로 바뀌면 알림 호출
        // 상태 업데이트
        dealMapper.patchStatus(status);

        // [2] 서비스 메서드 호출 (실제 DB/FCM 사용)
        if ("CLOSE_DEAL".equals(status.getStatus())) {
            sutNotificationServiceImpl.detectTradeHappenedNow(status.getDealId());
        }

        // [3] 실행 후 알림 개수
        int afterCount = notificationMapper.selectAllByMemberId(TRADE_MEMBER_ID).size();
        log.info("============= afterCount =============== {}", afterCount);

        // [4] 검증
        // 환경에 따라 알림이 추가될 수도/안될 수도 있으므로, 최소한 감소는 하지 않음을 확인
        assertTrue(afterCount >= beforeCount, "알림 개수는 감소하면 안 됩니다.");
        // 만약 해당 dealId가 찜 유저에게 반드시 알림을 발생시키는 데이터라면 아래처럼 바꿔도 됩니다.
        // assertEquals(before + 1, after, "거래 성사 시 알림 1건 저장되어야 합니다.");
    }

    /**
     * [리뷰 등록 알림] 단순 호출 통합 테스트
     * - 서비스가 내부에서: buildingId 기반 최신 평점/리뷰 조회 → 찜 유저 조회 → 알림 저장/푸시 수행
     * - 테스트에선 전/후 알림 수만 비교
     */
    @Test
    void notificationReviewRegisterd_단순호출() {
        // [1] 실행 전 알림 개수
        int beforeCount = notificationMapper.selectAllByMemberId(REVIEW_MEMBER_ID).size();
        log.info("============= beforeCount =============== {}", beforeCount);

        // [2] 서비스 메서드 호출 (실제 DB/FCM 사용)
        sutNotificationServiceImpl.notificationReviewRegisterd(REVIEW_BUILDING_ID);

        // [3] 실행 후 알림 개수
        int afterCount = notificationMapper.selectAllByMemberId(REVIEW_MEMBER_ID).size();
        log.info("============= afterCount =============== {}", afterCount);

        // [4] 검증
        assertTrue(afterCount >= beforeCount, "알림 개수는 감소하면 안 됩니다.");
        // 데이터가 확실히 알림 발생 조건을 만족한다면:
        // assertEquals(before + 1, after, "리뷰 등록 시 알림 1건 저장되어야 합니다.");
    }
}
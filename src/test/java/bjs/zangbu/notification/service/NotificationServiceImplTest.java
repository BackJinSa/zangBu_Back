package bjs.zangbu.notification.service;

import bjs.zangbu.bookmark.mapper.BookMarkMapper;
import bjs.zangbu.bookmark.service.BookMarkService;
import bjs.zangbu.bookmark.vo.Bookmark;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.fcm.mapper.FcmMapper;
import bjs.zangbu.fcm.service.FcmSender;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static bjs.zangbu.notification.dto.response.NotificationResponse.NotificationElement.formatMoney;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(SpringExtension.class) // JUnit5 + Spring TestContext Framework 통합
@ContextConfiguration(classes = NotificationServiceImplTest.MyBatisTestConfig.class) // MyBatis 전용 설정 클래스 로드
//@Transactional // 각 테스트 실행 후 롤백 (DB 변경 사항 테스트 간 독립성 유지)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // @Order로 테스트 실행 순서 고정
@Log4j2
class NotificationServiceImplTest {

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
            "bjs.zangbu.fcm"   // <- 여기에 패키지 스캔 추가
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

    private NotificationServiceImpl sut;
    @Resource NotificationMapper notificationMapper;
    @Resource MemberMapper memberMapper;
    @Resource BookMarkMapper bookMarkMapper;
    @Resource FcmMapper fcmMapper;
    @Resource BuildingMapper buildingMapper;
    @Resource FcmSender fcmSender;
    @Resource ReviewMapper reviewMapper;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // 지금 테스트하는 메서드들은 mapper 하나만 필요
        sut = new NotificationServiceImpl(
                notificationMapper,
                null, buildingMapper, null, null, fcmMapper, null, memberMapper, null
        );
    }

    private static final String MEMBER_ID = "a0030658-24f8-42cd-8d78-a9fd06bf02b2";
    private static final Long NOTIFICATION_ID = 6L;
    private static final Long DUMMY_BUILDING_ID = 5L;

    @Test
    void getAllNotifications_returnsDto() {
        // given (DB에 더미 데이터가 있다고 가정)
        List<Notification> expected = notificationMapper.selectAllByMemberId(MEMBER_ID);
        log.info("Baseline rows from DB for {} = {}", MEMBER_ID, expected.size());

        // when
        NotificationAll dto = sut.getAllNotifications(MEMBER_ID);

        // then
        assertNotNull(dto, "DTO가 null이면 안 됩니다.");
    }

    @Test
    void markAsRead_updatesOnceThenZero() {
        // when: 처음 읽음 처리
        boolean first = sut.markAsRead(MEMBER_ID, NOTIFICATION_ID);

        // then: 1건 업데이트 → true
        assertTrue(first, "첫 호출은 읽지 않은 알림을 읽음 처리하므로 true여야 합니다. " +
                "만약 false라면 해당 알림이 이미 읽음이거나 ID/회원 매칭이 아닐 가능성이 있습니다.");

        // when: 같은 알림을 다시 읽음 처리
        boolean second = sut.markAsRead(MEMBER_ID, NOTIFICATION_ID);
        log.info("second============================{}", second);

        // then: 이미 읽음 → 0건 업데이트 → false
        assertFalse(second, "같은 알림을 다시 읽으면 변경 사항이 없어 false여야 합니다.");
    }

    @Test
    void markAllAsRead_realDb() {
        // 첫 호출: 현재 미읽음 개수만큼 업데이트(없으면 0)
        MarkAllReadResult first = sut.markAllAsRead(MEMBER_ID);
        assertNotNull(first);

        // 두 번째 호출: 이미 다 읽음 처리됐으므로 0이어야 함
        MarkAllReadResult second = sut.markAllAsRead(MEMBER_ID);
        assertNotNull(second);
        assertEquals(0, second.getProcessedCount(), "두 번째 호출은 0이어야 합니다.");

        // 첫 호출 값은 0 이상이면 충분(테이블 상태에 따라 0일 수도 있음)
        assertTrue(first.getProcessedCount() >= 0);
    }

    @Test
    void removeNotification() {
        // 테스트용 알림 하나 확보 (DB에 더미가 있다고 가정)
        List<Notification> list = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertFalse(list.isEmpty(), "테스트용 알림이 없습니다. MEMBER_ID에 알림 하나를 준비해주세요.");

        Long notificationId = list.get(0).getNotificationId();

        // 1) 첫 삭제: 성공해야 함
        boolean first = sut.removeNotification(MEMBER_ID, notificationId);
        assertTrue(first, "첫 번째 삭제는 true 여야 합니다.");

        // 2) 같은 ID 재삭제: 이미 지웠으므로 false 여야 함
        boolean second = sut.removeNotification(MEMBER_ID, notificationId);
        assertFalse(second, "두 번째 삭제는 false 여야 합니다.");
    }

    @Test
    void sendChatNotification() {
    }

    @Test
    void sendNotificationIfNotExists_type_building() {
        Building building = buildingMapper.getBuildingById(DUMMY_BUILDING_ID);

        int firstPrice = building.getPrice();          // 첫 호출 가격
        int secondPrice = firstPrice + 1_000_000;      // 다른 가격

        String message = building.getBuildingName() + " 시세가 "
                + formatMoney(firstPrice) + " → " + formatMoney(secondPrice) + "으로 변동되었습니다.";

        // Act 1) 첫 저장
        sut.sendNotificationIfNotExists(MEMBER_ID, building, Type.BUILDING,
                "[시세 변동 알림]", message, firstPrice);

        // Assert: 1건 저장
        List<Notification> saved = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertEquals(1, saved.size());              // 알림 등록했기 때문에 크기 1
        assertEquals(firstPrice, saved.get(0).getPrice());   // 가격 확인
        assertEquals(Type.BUILDING, saved.get(0).getType()); // 타입 확인

        // Act 2) 같은 조건(같은 가격) 재호출 → 중복 금지
        sut.sendNotificationIfNotExists(MEMBER_ID, building, Type.BUILDING,
                "[시세 변동 알림]", "msg-duplicate", firstPrice);

        // Assert: 여전히 1건
        saved = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertEquals(1, saved.size(), "같은 날/같은 가격은 중복 저장되면 안 됨");

        // Act 3) 다른 가격으로 호출 → 새로 저장
        sut.sendNotificationIfNotExists(MEMBER_ID, building, Type.BUILDING,
                "[시세 변동 알림]", message, secondPrice);

        // Assert: 2건(두 번째는 다른 가격)
        saved = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertEquals(2, saved.size(), "가격이 달라지면 새 알림 저장");
        assertTrue(saved.stream().anyMatch(n -> n.getPrice() == secondPrice));

        fcmSender.send("dp50yXn_wTDuFLWE0ORoPE:APA91bH0CF44UE552qPkzNeKYA5Y-XqAMnrZkmEuQVCxlpPyEO5UIvCtNU_kz5NUHYNccHQOBvFW3IN_6vcZ-wI3FCXLXyxdsB88rIQfe_LpxTIssqKHFTU",
                "[시세 변동 알림]",
                message,
                "테스트 URL");
    }

    @Test
    void sendNotificationIfNotExists_type_trade() {

        Building building = buildingMapper.getBuildingById(DUMMY_BUILDING_ID);
        int tradePrice = building.getPrice();

        String message = building.getBuildingName() + " 매물이 "
                + formatMoney(tradePrice) + "에 거래되었습니다.";

        // Act 1) 저장
        sut.sendNotificationIfNotExists(
                MEMBER_ID, building, Type.TRADE, "[실거래 발생 알림]", message, tradePrice);

        // Assert: 1건
        List<Notification> saved = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertEquals(Type.TRADE, saved.get(0).getType());
        assertEquals(tradePrice, saved.get(0).getPrice());

        fcmSender.send("dp50yXn_wTDuFLWE0ORoPE:APA91bH0CF44UE552qPkzNeKYA5Y-XqAMnrZkmEuQVCxlpPyEO5UIvCtNU_kz5NUHYNccHQOBvFW3IN_6vcZ-wI3FCXLXyxdsB88rIQfe_LpxTIssqKHFTU",
                "[실거래 발생 알림]",
                message,
                "테스트 URL");
    }

    @Test
    void sendNotificationIfNotExists_type_review() {

        Building building = buildingMapper.getBuildingById(DUMMY_BUILDING_ID);
        int price = building.getPrice();
        Integer rank = Optional.ofNullable(
                reviewMapper.selectLatestReviewRank(building.getBuildingId())
        ).orElse(0);

        String message1 = "관심 매물 " + building.getBuildingName() + "에 새로운 리뷰가 등록되었습니다. (평점 " + rank + "점)";
        String message2 = "관심 매물 " + building.getBuildingName() + "에 새로운 리뷰가 등록되었습니다. (평점 " + rank + "점)";

        // Act 1) 첫 저장
        sut.sendNotificationIfNotExists(
                MEMBER_ID, building, Type.REVIEW, "[리뷰 등록 알림]", message1, price);

        // Assert: 1건 저장 (REVIEW는 중복 방지 로직 없음)
        List<Notification> saved = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertEquals(Type.REVIEW, saved.get(0).getType());

        // Act 2) 같은 조건 재호출 → REVIEW는 중복 허용
        sut.sendNotificationIfNotExists(
                MEMBER_ID, building, Type.REVIEW, "[리뷰 등록 알림]", message2, price);

        // Assert: 2건
        saved = notificationMapper.selectAllByMemberId(MEMBER_ID);
        assertTrue(saved.stream().allMatch(n -> n.getType() == Type.REVIEW));

        fcmSender.send("dp50yXn_wTDuFLWE0ORoPE:APA91bH0CF44UE552qPkzNeKYA5Y-XqAMnrZkmEuQVCxlpPyEO5UIvCtNU_kz5NUHYNccHQOBvFW3IN_6vcZ-wI3FCXLXyxdsB88rIQfe_LpxTIssqKHFTU",
                "[리뷰 등록 알림]",
                message1,
                "테스트 URL");
    }

    @Test
    void detectPriceChangeForAllBookmarks() {
        // FCM이 실제로 나가야 하므로: 동의 O && 토큰 존재해야 테스트 진행
        boolean consent = false;
        try { consent = memberMapper.selectFcmConsentByMemberId(MEMBER_ID); } catch (Exception ignore) {}
        List<String> tokens = null;
        try { tokens = fcmMapper.selectTokensByMemberId(MEMBER_ID); } catch (Exception ignore) {}
        assumeTrue(consent && tokens != null && !tokens.isEmpty(),
                "FCM 동의+토큰이 있어야 실제 푸시가 전송됩니다.");

        // Fake 북마크 1건: 과거 123,000,000 → 현재 125,000,000
        var fakeBms = new FakeBookMarkService();
        Bookmark bm = new Bookmark();
        bm.setBookMarkId(777L);
        bm.setUserId(MEMBER_ID);
        bm.setBuildingId(DUMMY_BUILDING_ID);
        bm.setPrice(123_000_000);
        fakeBms.bookmarks.add(bm);

        var fakeBm = new FakeBuildingMapper();
        fakeBm.buildings.put(DUMMY_BUILDING_ID,
                makeBuilding(DUMMY_BUILDING_ID, "테스트빌딩", "APARTMENT", "서울 어딘가", 125_000_000));

        // 실제 FCM 사용!
        NotificationService svc = makeServiceWithFakes(fakeBms, fakeBm, null, null);

        int before = notificationMapper.selectAllByMemberId(MEMBER_ID).size();

        svc.detectPriceChangeForAllBookmarks(); // ← 여기서 FCM까지 나감

        int after = notificationMapper.selectAllByMemberId(MEMBER_ID).size();
        assertEquals(before + 1, after, "가격 변동이면 알림 1건 저장되어야 함.");
        assertTrue(fakeBms.updateCalled.get(), "북마크 가격 갱신이 호출되어야 함.");
        assertEquals(125_000_000, fakeBms.updatedPrice.get());
        assertEquals(777L, fakeBms.updatedBookmarkId.get());
    }

    @Test
    void detectTradeHappenedNow() {
        // push 안전장치
        boolean consent = false;
        try { consent = memberMapper.selectFcmConsentByMemberId(MEMBER_ID); } catch (Exception ignore) {}
        List<String> tokens = Collections.emptyList();
        try { tokens = fcmMapper.selectTokensByMemberId(MEMBER_ID); } catch (Exception ignore) {}
        assumeTrue(!consent || tokens == null || tokens.isEmpty(),
                "이 계정은 FCM 동의가 꺼져있거나 토큰이 없어야 합니다.");

        Long dealId = 999L;

        var fakeDm = new FakeDealMapper();
        fakeDm.mapping.put(dealId, DUMMY_BUILDING_ID);

        var fakeBms = new FakeBookMarkService();
        fakeBms.userIdsByBuilding.put(DUMMY_BUILDING_ID, List.of(MEMBER_ID)); // 한 명만 테스트

        var fakeBm = new FakeBuildingMapper();
        fakeBm.buildings.put(DUMMY_BUILDING_ID,
                makeBuilding(DUMMY_BUILDING_ID, "테스트빌딩", "APARTMENT", "서울 어딘가", 330_000_000));

        NotificationService svc = makeServiceWithFakes(fakeBms, fakeBm, fakeDm, null);

        int before = notificationMapper.selectAllByMemberId(MEMBER_ID).size();

        svc.detectTradeHappenedNow(dealId);

        int after = notificationMapper.selectAllByMemberId(MEMBER_ID).size();
        assertEquals(before + 1, after, "거래 성사 시 해당 유저에게 알림 1건 저장.");
    }

    @Test
    void notificationReviewRegisterd() {
        // push 안전장치
        boolean consent = false;
        try { consent = memberMapper.selectFcmConsentByMemberId(MEMBER_ID); } catch (Exception ignore) {}
        List<String> tokens = Collections.emptyList();
        try { tokens = fcmMapper.selectTokensByMemberId(MEMBER_ID); } catch (Exception ignore) {}
        assumeTrue(!consent || tokens == null || tokens.isEmpty(),
                "이 계정은 FCM 동의가 꺼져있거나 토큰이 없어야 합니다.");

        var fakeBms = new FakeBookMarkService();
        fakeBms.userIdsByBuilding.put(DUMMY_BUILDING_ID, List.of(MEMBER_ID));

        var fakeRm = new FakeReviewMapper();
        fakeRm.rankByBuilding.put(DUMMY_BUILDING_ID, 4); // 최신 평점 4점

        var fakeBm = new FakeBuildingMapper();
        fakeBm.buildings.put(DUMMY_BUILDING_ID,
                makeBuilding(DUMMY_BUILDING_ID, "테스트빌딩", "APARTMENT", "서울 어딘가", 500_000_000));

        NotificationService svc = makeServiceWithFakes(fakeBms, fakeBm, null, fakeRm);

        int before = notificationMapper.selectAllByMemberId(MEMBER_ID).size();

        svc.notificationReviewRegisterd(DUMMY_BUILDING_ID);

        int after = notificationMapper.selectAllByMemberId(MEMBER_ID).size();
        assertEquals(before + 1, after, "리뷰 등록 시 알림 1건 저장.");
    }
}
package bjs.zangbu.fcm.mapper;

import bjs.zangbu.fcm.vo.Fcm;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FcmMapper 단위 테스트
 * - 스프링 레거시 환경에서 MyBatis Mapper만 로딩하여 DB 연동 테스트
 * - MySQL 실제 DB를 사용
 */
@ExtendWith(SpringExtension.class) // JUnit5 + Spring TestContext Framework 통합
@ContextConfiguration(classes = FcmMapperTest.MyBatisTestConfig.class) // MyBatis 전용 설정 클래스 로드
//@Transactional // 각 테스트 실행 후 롤백 (DB 변경 사항 테스트 간 독립성 유지)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // @Order로 테스트 실행 순서 고정
@Log4j2
class FcmMapperTest {

    /**
     * MyBatis + HikariCP + 트랜잭션 매니저 설정
     * - 스프링 부트가 아니므로 수동으로 Bean 등록
     */
    @Configuration
    @MapperScan(basePackages = "bjs.zangbu.fcm.mapper") // FcmMapper 인터페이스 패키지 스캔
    static class MyBatisTestConfig {
        @Bean
        public DataSource dataSource() {
            // HikariCP 연결 설정 (프로젝트 환경에 맞게 수정)
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
            fb.setConfigLocation(ctx.getResource("classpath:/mybatis-config.xml")); // 타입핸들러/별칭 등 공통 설정
            return fb.getObject();
        }

        @Bean
        public DataSourceTransactionManager transactionManager(DataSource ds) {
            // @Transactional 지원용 (원하면 테스트 클래스에 @Transactional 추가)
            return new DataSourceTransactionManager(ds);
        }
    }

    // Mapper 주입
    @Resource
    private FcmMapper fcmMapper;

    // 테스트용 회원 ID (DB에 미리 더미 데이터 존재해야 함)
    private static final String MEMBER_ID = "a0030658-24f8-42cd-8d78-a9fd06bf02b2";
    private static final String TOKEN = "dp50yXn_wTDuFLWE0ORoPE:APA91bH0CF44UE552qPkzNeKYA5Y-XqAMnrZkmEuQVCxlpPyEO5UIvCtNU_kz5NUHYNccHQOBvFW3IN_6vcZ-wI3FCXLXyxdsB88rIQfe_LpxTIssqKHFTU";

    /**
     * 1) FCM 토큰 삽입 테스트
     * - 같은 MEMBER_ID에 대해 TOKEN1, TOKEN2를 삽입
     * - insertFcmToken(Fcm fcm) → int (영향 행수) 가정
     */
    @Test
    @Order(1)
    @DisplayName("insertFcmToken: 신규 FCM 토큰 등록")
    void insertFcmToken() {
        Fcm fcm = new Fcm(
                null,
                TOKEN,
                null,
                null,
                new Date(),
                MEMBER_ID);

        int result = fcmMapper.insertFcmToken(MEMBER_ID, fcm);

        assertEquals(1, result, "TOKEN 등록 성공");
    }

    /**
     * 2) 존재 여부 테스트
     * - existsByMemberIdAndToken(String memberId, String token) → boolean 가정
     */
    @Test
    @Order(2)
    @DisplayName("existsByMemberIdAndToken: 멤버/토큰 존재 여부")
    void existsByMemberIdAndToken() {
        boolean e1 = fcmMapper.existsByMemberIdAndToken(MEMBER_ID, TOKEN1);
        boolean e2 = fcmMapper.existsByMemberIdAndToken(MEMBER_ID, TOKEN2);
        boolean e3 = fcmMapper.existsByMemberIdAndToken(MEMBER_ID, "no-such-token");

        assertTrue(e1, "TOKEN1 존재해야 함");
        assertTrue(e2, "TOKEN2 존재해야 함");
        assertFalse(e3, "없는 토큰은 존재하면 안 됨");
    }

    /**
     * 3) 토큰 목록 조회 테스트
     * - selectTokensByMemberId(String memberId) → List<String> 가정
     *   * 만약 Mapper가 List<Fcm>을 반환한다면 타입/검증 로직을 맞게 변경하세요.
     */
    @Test
    @Order(3)
    @DisplayName("selectTokensByMemberId: 멤버의 모든 토큰 조회")
    void selectTokensByMemberId() {
        List<String> tokens = fcmMapper.selectTokensByMemberId(MEMBER_ID);
        assertNotNull(tokens);
        assertTrue(tokens.size() >= 2, "최소 2개 이상 있어야 함");
        assertTrue(tokens.contains(TOKEN1), "TOKEN1이 목록에 있어야 함");
        assertTrue(tokens.contains(TOKEN2), "TOKEN2가 목록에 있어야 함");

        log.info("토큰 목록: {}", tokens);
    }

    /**
     * 4) 모든 토큰 삭제 테스트
     * - deleteAllTokensByMemberId(String memberId) → int (삭제 행수) 가정
     * - 삭제 후 exists/select로 빈 상태 검증
     */
    @Test
    @Order(4)
    @DisplayName("deleteAllTokensByMemberId: 멤버 토큰 전체 삭제")
    void deleteAllTokensByMemberId() {
        int deleted = fcmMapper.deleteAllTokensByMemberId(MEMBER_ID);
        assertTrue(deleted >= 1, "삭제된 행이 1 이상이어야 함");

        boolean stillExists1 = fcmMapper.existsByMemberIdAndToken(MEMBER_ID, TOKEN1);
        boolean stillExists2 = fcmMapper.existsByMemberIdAndToken(MEMBER_ID, TOKEN2);
        List<String> tokensAfter = fcmMapper.selectTokensByMemberId(MEMBER_ID);

        assertFalse(stillExists1, "삭제 후 TOKEN1이 남아 있으면 안 됨");
        assertFalse(stillExists2, "삭제 후 TOKEN2가 남아 있으면 안 됨");
        assertTrue(tokensAfter == null || tokensAfter.isEmpty(), "삭제 후 토큰 목록이 비어야 함");
    }
}

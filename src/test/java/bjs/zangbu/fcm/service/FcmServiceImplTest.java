package bjs.zangbu.fcm.service;

import bjs.zangbu.fcm.dto.request.FcmRequest.FcmRegisterRequest;
import bjs.zangbu.fcm.mapper.FcmMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FcmServiceImplTest.MyBatisTestConfig.class)
@Log4j2
class FcmServiceImplTest {

    @Configuration
    @MapperScan(basePackages = "bjs.zangbu.fcm.mapper")
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
        // Service 빈 직접 등록(필요 시)
        @Bean
        public FcmService fcmService(FcmMapper fcmMapper) {
            return new FcmServiceImpl(fcmMapper);
        }
    }

    private static final String MEMBER_ID = "a0030658-24f8-42cd-8d78-a9fd06bf02b2";
    private static final String TOKEN1   = "dp50yXn_wTDuFLWE0ORoPE:APA91bH0CF44UE552qPkzNeKYA5Y-XqAMnrZkmEuQVCxlpPyEO5UIvCtNU_kz5NUHYNccHQOBvFW3IN_6vcZ-wI3FCXLXyxdsB88rIQfe_LpxTIssqKHFTU";
    private static final String TOKEN2   = "000000dp50yXn_wTDuFLWE0ORoPE:APA91bH0CF44UE552qPkzNeKYA5Y-XqAMnrZkmEuQVCxlpPyEO5UIvCtNU_kz5NUHYNccHQOBvFW3IN_6vcZ-wI3FCXLXyxdsB88rIQfe_LpxTIssqKHFTU";

    @Resource FcmMapper fcmMapper;
    @Resource FcmService fcmService;

    @BeforeEach
    void setUp() {
        // 필요한 의존성만 추가해서 클래스 생성
    }

    @Test
    void registerToken() {

        // [1] 실행 전 토큰 개수
        int beforeCount = fcmMapper.selectTokensByMemberId(MEMBER_ID).size();

        // 요청 DTO 생성
        FcmRegisterRequest req = new FcmRegisterRequest(TOKEN1, null, null);
        fcmService.registerToken(MEMBER_ID, req);

        int afterFirst = fcmMapper.selectTokensByMemberId(MEMBER_ID).size();
        assertEquals(beforeCount + 1, afterFirst, "첫 등록은 1건 증가해야 함");

        // [2] 같은 토큰 다시 등록 → 증가 없어야 함 (idempotent)
        fcmService.registerToken(MEMBER_ID, new FcmRegisterRequest(TOKEN1, null, null));
        int afterSecond = fcmMapper.selectTokensByMemberId(MEMBER_ID).size();
        assertEquals(afterFirst, afterSecond, "중복 토큰은 추가되면 안 됨");

        // [3] 다른 토큰 추가 → +1
        fcmService.registerToken(MEMBER_ID, new FcmRegisterRequest(TOKEN2, null, null));
        int afterThird = fcmMapper.selectTokensByMemberId(MEMBER_ID).size();
        assertEquals(afterSecond + 1, afterThird, "다른 토큰 등록은 1건 증가해야 함");
    }

    @Test
    void deleteAllTokensByMemberId() {

        // [1] 토큰 2개 등록
        fcmService.registerToken(MEMBER_ID, new FcmRegisterRequest(TOKEN1, null, null));
        fcmService.registerToken(MEMBER_ID, new FcmRegisterRequest(TOKEN2, null, null));

        // [2] MEMBER_ID의 토큰 모두 불러오기
        List<String> before = fcmMapper.selectTokensByMemberId(MEMBER_ID);

        // [3] 토큰 2개 추가했기 때문에 최소 2개 이상의 토큰 존재
        assertTrue(before.size() >= 2);

        // [4] MEMBER_ID의 토큰 모두 삭제
        fcmService.deleteAllTokensByMemberId(MEMBER_ID);

        // [5] MEMBER_ID의 토큰 모두 불러오기
        List<String> after = fcmMapper.selectTokensByMemberId(MEMBER_ID);

        // [6] 토큰 모두 삭제했기 때문에 존재하지않음
        assertTrue(after.isEmpty(), "모든 토큰이 삭제되어야 함");
    }
}

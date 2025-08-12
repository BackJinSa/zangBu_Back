package bjs.zangbu.config;

import bjs.zangbu.notification.service.NotificationService;
import bjs.zangbu.review.controller.ReviewController;
import bjs.zangbu.review.mapper.ReviewMapper;
import bjs.zangbu.review.service.ReviewService;
import bjs.zangbu.review.service.ReviewServiceImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan("bjs.zangbu.**.mapper")
public class TestConfig {

    // 테스트용 데이터베이스 설정 (직접 설정)
    private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/zangBu";
    private static final String USERNAME = "backjinsa";
    private static final String PASSWORD = "backjinsa1234";

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(DRIVER_CLASS_NAME);
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);

        // 테스트용 커넥션 풀 설정
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);

        return new HikariDataSource(config);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // MyBatis 설정 파일
        sessionFactory.setConfigLocation(
                new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));

        // 매퍼 XML 파일들
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:bjs/zangbu/**/mapper/*.xml"));

        return sessionFactory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public ReviewService reviewService(ReviewMapper reviewMapper, NotificationService notificationService) {
        return new ReviewServiceImpl(reviewMapper, notificationService);
    }

    @Bean
    public ReviewController reviewController(ReviewService reviewService) {
        return new ReviewController(reviewService);
    }

    @Bean
    public NotificationService notificationService() {
        return Mockito.mock(NotificationService.class);
    }

}

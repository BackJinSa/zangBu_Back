package bjs.zangbu.testconfig;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * 테스트에서 필요한 빈만 최소로 올리는 전용 구성
 * - Swagger, WebMvc 등은 전혀 로딩하지 않음
 * - H2 메모리 DB 사용 (MySQL 호환 모드)
 * - mapper XML은 classpath:mapper/** 를 스캔 (프로젝트에 맞게 수정 가능)
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "bjs.zangbu.**.mapper") // Mapper 인터페이스 패키지
public class TestMyBatisConfig {

    @Bean
    public DataSource dataSource() {
        // H2 메모리 DB (MySQL 호환). 시작 시 schema/data를 즉시 실행.
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl(
                "jdbc:h2:mem:zangbu;" +
                        "MODE=MySQL;" +
                        "DB_CLOSE_DELAY=-1;" +
                        "DATABASE_TO_UPPER=false;" +
                        "INIT=RUNSCRIPT FROM 'classpath:/sql/notification/schema.sql'\\;" +
                        "RUNSCRIPT FROM 'classpath:/sql/notification/data.sql'"
        );
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(dataSource);

        // (선택) 프로젝트의 mybatis-config.xml 을 쓰고 싶으면 주석 해제
        // fb.setConfigLocation(new PathMatchingResourcePatternResolver()
        //         .getResource("classpath:/mybatis-config.xml"));

        // XML 매퍼 스캔 경로 (프로젝트 구조 맞게 조정)
        fb.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/**/*.xml"));

        // PageHelper 같은 플러그인 쓰면 여기서 등록 (필요 없으면 생략)
        // fb.setPlugins(new Interceptor[]{ pageInterceptor() });

        return fb.getObject();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    // 필요 시 PageHelper 등록 예시
    // @Bean
    // public Interceptor pageInterceptor() {
    //     com.github.pagehelper.PageInterceptor interceptor = new com.github.pagehelper.PageInterceptor();
    //     java.util.Properties properties = new java.util.Properties();
    //     properties.setProperty("helperDialect", "mysql");
    //     properties.setProperty("reasonable", "true");
    //     properties.setProperty("supportMethodsArguments", "true");
    //     properties.setProperty("params", "count=countSql");
    //     interceptor.setProperties(properties);
    //     return interceptor;
    // }
}

package bjs.zangbu.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInterceptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@PropertySource(value = "classpath:/application.yml", factory = YamlPropertyConfig.class)
@MapperScan(basePackages = {
    "bjs.zangbu.addressChange.mapper",
    "bjs.zangbu.bookmark.mapper",
    "bjs.zangbu.building.mapper",
    "bjs.zangbu.chat.mapper",
    "bjs.zangbu.complexList.mapper",
    "bjs.zangbu.deal.mapper",
    "bjs.zangbu.documentReport.mapper",
    "bjs.zangbu.fcm.mapper",
    "bjs.zangbu.imageList.mapper",
    "bjs.zangbu.map.mapper",
    "bjs.zangbu.member.mapper",
    "bjs.zangbu.notification.mapper",
    "bjs.zangbu.payment.mapper",
    "bjs.zangbu.review.mapper",
})
/* 컨트롤러를 제외하고 전역 스캔: 서비스/컴포넌트/클라이언트 등 등록 */
@ComponentScan(
    basePackages = "bjs.zangbu",
    excludeFilters = @ComponentScan.Filter(org.springframework.stereotype.Controller.class)
)
public class RootConfig {

  @Value("${jdbc.driver}")
  String driver;
  @Value("${jdbc.url}")
  String url;
  @Value("${jdbc.username}")
  String username;
  @Value("${jdbc.password}")
  String password;
  @Autowired
  ApplicationContext applicationContext;

  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();

    config.setDriverClassName(driver);
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);

    return new HikariDataSource(config);
  }

  @Bean
  public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
    sqlSessionFactory.setConfigLocation(
        applicationContext.getResource("classpath:/mybatis-config.xml"));
    sqlSessionFactory.setDataSource(dataSource());

    // PageHelper 플러그인 등록
    sqlSessionFactory.setPlugins(new Interceptor[]{pageInterceptor()});

    return sqlSessionFactory.getObject();
  }

  @Bean
  public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public PageInterceptor pageInterceptor() {
    PageInterceptor interceptor = new PageInterceptor();
    Properties properties = new Properties();

    properties.setProperty("helperDialect", "mysql");
    properties.setProperty("reasonable", "true");
    properties.setProperty("supportMethodsArguments", "true");
    properties.setProperty("params", "count=countSql");

    interceptor.setProperties(properties);
    return interceptor;

  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}

package bjs.zangbu.global.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2  // Springfox 필수 어노테이션
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("bjs.zangbu"))
        .paths(PathSelectors.any())
        .build()
        .securitySchemes(List.of(apiKey())) // 추가
        .securityContexts(List.of(securityContext())); // 추가
  }


  @Bean
  public ApiKey apiKey() {
    return new ApiKey("Authorization", "Authorization", "header");
  }

  @Bean
  public SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .build();
  }

  private List<SecurityReference> defaultAuth() {
    return List.of(new SecurityReference("Authorization",
        new springfox.documentation.service.AuthorizationScope[0]));
  }
}

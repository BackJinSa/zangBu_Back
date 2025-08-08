package bjs.zangbu.global.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2  // Springfox 필수 어노테이션
@Profile("!test")
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo()) // 추가

        .useDefaultResponseMessages(false) // 기본 응답 제거
        .forCodeGeneration(true)           // DTO 필드 인식 강화
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

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Zangbu API 문서")
        .description("부동산 거래 플랫폼 Swagger 문서입니다.")
        .version("1.0.0")
        .contact(new Contact("개발팀", "www.zangbu.site", "support@zangbu.site"))
        .build();
  }
}

package bjs.zangbu.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity
//-------보안 설정 클래스임을 선언
@Log4j2
@MapperScan(basePackages = {"bjs.zangbu.security.account.mapper"})
@ComponentScan(basePackages = {"bjs.zangbu.security"})
@RequiredArgsConstructor //final 필드 생성자 주입

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // SecurityConfig : 전체 보안 정책 설정 클래스

    private final UserDetailsService userDetailsService;

    //비밀번호 암호화에 사용하는 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 한글 인코딩 필터 -> UTF-8 인코딩 적용
    public CharacterEncodingFilter encodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return encodingFilter;
    }

    // 접근 제한무시경로설정–resource
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/*", "/api/member/**",
                "/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs"
                // Swagger 관련url은보안에서제외
        );
    }

    //configure(HttpSecurity http) : http 요청별 보안 정책 설정
    @Override
    public void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(encodingFilter(), CsrfFilter.class); --WebConfig에 추가 되어있어서 삭제
        http
                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, JwtUsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class);

        // 경로별 접근 권한 설정
        http.httpBasic().disable() // 기본 브라우저 팝업 로그인 창 비활성화
                .csrf().disable() // CSRF 보호 기능 비활성화 (보통 API 개발 시 사용)
                .formLogin().disable() // 스프링 시큐리티 기본 로그인 폼 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 사용 안 함 (JWT 등 사용할 때)
        //-> JWT 기반 인증 : 매 요청마다 토큰으로 인증

        http.authorizeRequests()
                .antMatchers("/security/all").permitAll() // 누구나 접근 허용
                .antMatchers("/security/admin").hasRole("ADMIN") // ADMIN 권한 필요
                .antMatchers("/security/member").hasAnyRole("ADMIN", "MEMBER") // ADMIN 또는 MEMBER 권한 필요
                .anyRequest().permitAll(); // 나머지 요청은 모두 허용

        http.formLogin()
                .loginPage("/security/login") // 커스텀 로그인 페이지 URL
                .loginProcessingUrl("/security/login") // 로그인 form의 action URL (POST로 처리됨)
                .defaultSuccessUrl("/"); // 로그인 성공 시 이동할 기본 경로

        http.logout()
                .logoutUrl("/security/logout") // 로그아웃 처리 URL
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("remember-me", "JSESSION-ID") // 쿠키 제거
                .logoutSuccessUrl("/security/logout"); // 로그아웃 성공 후 이동 경로
    }


    //configure(AuthenticationManagerBuilder) : 사용자 인증 방법/암호화 방식 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("configure .........................................");
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    //있어야 authenticationManager() 관련 기능이 제대로 동작
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

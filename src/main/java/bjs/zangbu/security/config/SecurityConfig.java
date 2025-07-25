package bjs.zangbu.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    //configure(HttpSecurity http) : http 요청별 보안 정책 설정
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(encodingFilter(), CsrfFilter.class);

        // 경로별 접근 권한 설정
        http.authorizeRequests()
                .antMatchers("/security/all").permitAll()
                .antMatchers("/security/admin").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/security/member").access("hasAnyRole('ROLE_MEMBER', 'ROLE_ADMIN')");

        http.formLogin() //로그인 설정
                .loginPage("/security/login") // 커스텀 로그인 페이지
                .loginProcessingUrl("/security/login") // 로그인 요청 URL
                .defaultSuccessUrl("/"); // 로그인 성공 시 이동할 URL

        http.logout() // 로그아웃 설정 시작
                .logoutUrl("/security/logout") // POST: 로그아웃 호출 url
                .invalidateHttpSession(true) // 세션 invalidate
                .deleteCookies("remember-me", "JSESSION-ID") // 삭제할 쿠키 목록
                .logoutSuccessUrl("/security/logout"); // GET: 로그아웃 이후 이동할 페이지
    }

    //configure(AuthenticationManagerBuilder) : 사용자 인증 방법/암호화 방식 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("configure .........................................");
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}

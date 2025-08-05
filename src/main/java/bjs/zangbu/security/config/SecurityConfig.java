package bjs.zangbu.security.config;

import bjs.zangbu.security.filter.AuthenticationErrorFilter;
import bjs.zangbu.security.filter.JwtAuthenticationFilter;
import bjs.zangbu.security.filter.AuthenticationErrorFilter;
import bjs.zangbu.security.filter.JwtAuthenticationFilter;
import bjs.zangbu.security.filter.JwtUsernamePasswordAuthenticationFilter;
import bjs.zangbu.security.handler.LoginFailureHandler;
import bjs.zangbu.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@Log4j2
@MapperScan(basePackages = {"bjs.zangbu.security.account.mapper"})
@ComponentScan(basePackages = {"bjs.zangbu.security"})
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationErrorFilter authenticationErrorFilter;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    //비밀번호 암호화에 사용하는 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(http);

        JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter =
                new JwtUsernamePasswordAuthenticationFilter(
                        authenticationManager,
                        loginSuccessHandler,
                        loginFailureHandler
                );

        http
                // JWT 필터들 순서에 맞게 등록
                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, JwtUsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class)

                //cors 설정 추가
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of( //요청 허용할 출처 리스트
                            "https://www.zangbu.site"      // 배포한 서버 주소
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));//허용할 메서드 종류
                    config.setAllowedHeaders(List.of("*")); //요청에 사용할 수 있는 헤더
                    config.setAllowCredentials(true);  // 쿠키 포함 허용
                    config.setMaxAge(3600L); // preflight 결과 캐싱 시간 1시간
                    return config;
                }))

                // 보안 설정
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/webjars/**", "/swagger-resources/**", "/v2/api-docs"
                        ).permitAll()
                        .requestMatchers("/security/all").permitAll()
                        .requestMatchers("/security/admin").hasRole("ADMIN")
                        .requestMatchers("/security/member").hasAnyRole("ADMIN", "MEMBER")
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}

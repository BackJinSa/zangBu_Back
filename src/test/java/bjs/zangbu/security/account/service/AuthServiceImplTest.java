package bjs.zangbu.security.account.service;

import static org.junit.jupiter.api.Assertions.*;

import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.security.account.dto.request.AuthRequest.EmailAuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.LoginRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.ResetPassword;
import bjs.zangbu.security.account.dto.request.AuthRequest.SignUp;
import bjs.zangbu.security.account.dto.response.AuthResponse.LoginResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.TokenResponse;
import bjs.zangbu.security.account.mapper.AuthMapper;
import bjs.zangbu.security.account.vo.Member;
import bjs.zangbu.security.account.vo.MemberEnum;
import bjs.zangbu.security.util.JwtProcessor;
import java.time.Duration;
import java.util.*;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServiceImpl 통합 테스트
 * - 실제 Spring 컨텍스트 구동
 * - DB: 실제 테스트 DB (로컬/도커/CI 환경)
 * - Redis: 실제 인스턴스
 * - JWT: 테스트용 Stub 빈 주입
 */
@ActiveProfiles("test")
@SpringJUnitConfig(classes = {
        RootConfig.class,
        AuthServiceImplTest.StubJwtConfig.class,
        AuthServiceImplTest.RedisTestConfig.class
})
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:log4jdbc:mysql://localhost:3306/zangBu",
        "spring.datasource.username=scoula",
        "spring.datasource.password=1234",
        "spring.datasource.driver-class-name=net.sf.log4jdbc.sql.jdbcapi.DriverSpy",

        // Redis 연결 정보 (RootConfig가 가져다 씀)
        "spring.redis.host=127.0.0.1",
        "spring.redis.port=6379",
        "spring.redis.database=15" // 테스트 전용 DB 슬롯
})
@Log4j2
class AuthServiceImplTest {

    @Configuration
    static class RedisTestConfig {
        @Bean @Primary
        org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory() {
            var conf = new org.springframework.data.redis.connection.RedisStandaloneConfiguration("127.0.0.1", 6379);
            conf.setDatabase(15);
            conf.setPassword(org.springframework.data.redis.connection.RedisPassword.of("1234")); // 실제 비번
            return new org.springframework.data.redis.connection.jedis.JedisConnectionFactory(conf);
        }

        @Bean @Primary
        org.springframework.data.redis.core.RedisTemplate<String,Object> redisTemplate(
                org.springframework.data.redis.connection.RedisConnectionFactory cf) {
            var t = new org.springframework.data.redis.core.RedisTemplate<String,Object>();
            t.setConnectionFactory(cf);
            return t;
        }
    }

    @Configuration
    static class StubJwtConfig {
        /**
         * 테스트 전용 JwtProcessor Stub
         * - 토큰을 "ACC:<email>", "REF:<email>" 형태로 생성
         * - validateToken 항상 true
         * - getEmail은 토큰에서 ':' 뒤를 이메일로 파싱
         * - refresh TTL: 1시간
         */
        @Bean
        @Primary
        public JwtProcessor jwtProcessorStub() {
            return new JwtProcessor() {
                @Override
                public String generateAccessToken(String email, String role) {
                    return "ACC:" + email;
                }
                @Override
                public String generateRefreshToken(String email) {
                    return "REF_NEW:" + email + ":" + UUID.randomUUID();
                }
                @Override
                public long getRefreshTokenExpiration() {
                    return 3_600_000L;
                }
                @Override
                public boolean validateToken(String token) {
                    return token != null && token.contains(":");
                }
                @Override
                public String getEmail(String token) {
                    int i = token.indexOf(':');
                    return i > -1 ? token.substring(i + 1) : null;
                }
            };
        }
    }

    @Autowired AuthService authService;        // 대상 서비스
    @Autowired AuthMapper authMapper;          // 조회 검증용
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired RedisTemplate<String, Object> redisTemplate;
    @Autowired DataSource dataSource;

    JdbcTemplate jdbc;
    ValueOperations<String, Object> valueOps;
    HashOperations<String, Object, Object> hashOps;

    @BeforeEach
    void setUp() {
        jdbc = new JdbcTemplate(dataSource);
        valueOps = redisTemplate.opsForValue();
        hashOps = redisTemplate.opsForHash();
    }

    /* ----------------- 헬퍼: 픽스처 ----------------- */

    private String insertMember(String email, String rawPw, String phone, String nick,
                                String birth, String name, boolean consent, String telecom,
                                String identity) {
        String id = UUID.randomUUID().toString();
        String enc = passwordEncoder.encode(rawPw);
        jdbc.update("""
        INSERT INTO member(member_id,email,password,phone,nickname,identity,`role`,birth,name,consent,telecom)
        VALUES (?,?,?,?,?,?, 'ROLE_MEMBER', ?, ?, ?, ?)
        """, id, email, enc, phone, nick, identity, birth, name, consent, telecom);
        return id;
    }


    /* ----------------- 테스트들 ----------------- */

//    @Test
//    @DisplayName("login: 성공 시 토큰 발급 + Redis 키/TTL 저장")
//    void login_success() {
//        String email = "login_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
//        insertMember(email, "Pw1!", "01011112222", "nick", "950101", "홍길동", true, "SKT", "1234567");
//
//        LoginResponse res = authService.login(new LoginRequest(email, "Pw1!"));
//
//        log.info("accessToken={}", res.getAccessToken());
//        log.info("refreshToken={}", res.getRefreshToken());
//
//        assertNotNull(res.getAccessToken());
//        assertNotNull(res.getRefreshToken());
//
//        // Redis 확인
//        String refreshKey = "refresh:" + email;
//        assertEquals("REF:" + email, valueOps.get(refreshKey));
//        Long ttlMs = redisTemplate.getExpire(refreshKey);
//        log.info("savedKey={}, ttlMs={}", refreshKey, ttlMs);
//        // null/음수만 아니면 OK로
//        assertNotNull(ttlMs);
//
//        assertEquals("true", valueOps.get("login:" + email));
//    }

    @Test
    @DisplayName("logout: Redis 키 삭제")
    void logout_success() {
//        String email = "logout_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String email = "login_2298d6b1@t.com";
        valueOps.set("refresh:" + email, "REF:" + email, Duration.ofHours(1));
        valueOps.set("login:" + email, "true", Duration.ofHours(2));

        authService.logout(email);

        assertNull(valueOps.get("refresh:" + email));
        assertNull(valueOps.get("login:" + email));
    }

    @Test
    @DisplayName("signUp: Redis 본인인증 해시 → 회원가입 성공 → Redis 키 삭제")
    void signUp_success() throws Exception {
        // 1) Redis에 본인인증 성공 값 넣기
        String sessionId = "sess_" + UUID.randomUUID();
        String key = "signup:verify:" + sessionId;
        hashOps.putAll(key, Map.of(
                "status", "Y",
                "name", "강강강",
                "birth", "990101",
                "identity", "ENCRYPTED_ID",
                "phone", "01099998888",
                "telecom", "KT",
                "issueDate", "20250101"
        ));
        redisTemplate.expire(key, Duration.ofMinutes(10));

        // 저장 직후 redis 상태 로그
        var beforeEntries = hashOps.entries(key);
        log.info("[REDIS BEFORE] key={}, exists={}, entries={}",
                key, redisTemplate.hasKey(key), beforeEntries);

        // 2) signUp 요청
        SignUp req = new SignUp();
        req.setSessionId(sessionId);
        req.setEmail("join_" + UUID.randomUUID().toString().substring(0,8) + "@t.com");
        req.setNickname("kangkangkang");
        req.setPassword("Pw@12345");

        authService.signUp(req);

        // 3) DB에 들어갔는지 확인
        Member found = authMapper.findByEmail(req.getEmail());
        assertNotNull(found);
        assertEquals("강강강", found.getName());
        assertEquals("990101", found.getBirth());
        assertEquals("ENCRYPTED_ID", found.getIdentity());
        assertEquals("01099998888", found.getPhone());
        assertEquals("KT", found.getTelecom());
        assertEquals(MemberEnum.ROLE_MEMBER, found.getRole());
        assertTrue(passwordEncoder.matches("Pw@12345", found.getPassword()));

        // 삭제 후 redis 상태 로그
        Boolean existsAfter = redisTemplate.hasKey(key);
        var afterEntries = hashOps.entries(key); // 삭제됐다면 빈 map
        log.info("[REDIS AFTER] key={}, exists={}, entries={}", key, existsAfter, afterEntries);

        // 4) Redis 키 삭제
        assertFalse(Boolean.TRUE.equals(existsAfter));
        assertTrue(afterEntries.isEmpty());
    }

    @Test
    @DisplayName("findEmail: 이름/전화로 이메일 찾기")
    void findEmail_success() {
//        String email = "find_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
//        insertMember(email, "p", "01022223333", "n", "950101", "김철수", true, "SKT", "1234567");
        String email = "login_2298d6b1@t.com";

        var res = authService.findEmail(new EmailAuthRequest("홍길동", "01011112222"));

        log.info("email={}", res.getEmail());
        assertEquals(email, res.getEmail());
    }

    @Test
    @DisplayName("중복 체크: 이메일/닉네임")
    void duplicateChecks() {
//        String email = "dup_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
//        insertMember(email, "p", "01000000000", "dupNick", "950101", "홍길동", true, "SKT", "1234567");
        String email = "login_2298d6b1@t.com";
        String nickname = "nick";

        boolean isDup = authService.isEmailDuplicated(email); //있는거 중복 확인
        log.info("isDup={}", isDup);

        boolean isNotDup = authService.isEmailDuplicated("dkananf@t.com");
        log.info("isNotDup={}", isNotDup);

        boolean isNickDup = authService.isNicknameDuplicated(nickname);
        log.info("isNickDup={}", isNickDup);

        boolean isNotNickDup = authService.isNicknameDuplicated("dkanakf");

        assertTrue(authService.isEmailDuplicated(email)); //있는거 중복 확인
        assertFalse(authService.isEmailDuplicated("dkananf@t.com")); //없는거 중복 x 확인

        assertTrue(authService.isNicknameDuplicated(nickname)); //있는거 중복 확인
        assertFalse(authService.isNicknameDuplicated("dkanakf")); //없는거 중복 x 확인
    }

    @Test
    @DisplayName("resetPassword: 세션 검증 → 비번 변경 → 세션 제거")
    void resetPassword_success() {
        String email = "reset_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        insertMember(email, "OldPw1!", "01011112222", "n", "950101", "홍길동", true, "SKT", "1234567");

        // 가짜 세션
        HttpSession session = new MockHttpSessionLike();
        session.setAttribute("verifiedEmail", email);

        // password 재설정 전
        Member before = authMapper.findByEmail(email);
        log.info("[BEFORE] email={}, pwMatchesOld={}, pwMatchesNew={}, sessionAttr={}",
                before.getEmail(),
                passwordEncoder.matches("OldPw1!", before.getPassword()),
                passwordEncoder.matches("NewPw1!", before.getPassword()),
                session.getAttribute("verifiedEmail"));
        //old랑 매치

        //재설정
        authService.resetPassword(new ResetPassword("NewPw1!"), session);

        //password 재설정 후
        Member after = authMapper.findByEmail(email);
        log.info("[AFTER] email={}, pwMatchesOld={}, pwMatchesNew={}, sessionAttr={}",
                after.getEmail(),
                passwordEncoder.matches("OldPw1!", after.getPassword()),
                passwordEncoder.matches("NewPw1!", after.getPassword()),
                session.getAttribute("verifiedEmail"));
        //new랑 매치

        assertTrue(passwordEncoder.matches("NewPw1!", after.getPassword()));
        assertNull(session.getAttribute("verifiedEmail"));
    }

    @Test
    @DisplayName("reissue: 유효한 refresh → 저장값 일치 → 신규 access, refresh 토큰 발급하고 저장")
    void reissue_success() {
        String email = "re_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        insertMember(email, "Pw1!", "01011112299", "n", "950101", "길동홍홍", true, "SKT", "1234567");

        // 로그인 과정 없이 그냥 refresh 저장
        String oldRefresh = "REF:" + email;
        valueOps.set("refresh:" + email, oldRefresh, Duration.ofHours(1));
        log.info("oldRefresh={}", oldRefresh);

        TokenResponse out = authService.reissue(oldRefresh);
        assertEquals("ACC:" + email, out.getAccessToken());
        assertEquals("REF:" + email, out.getRefreshToken());
        assertEquals("REF:" + email, valueOps.get("refresh:" + email)); // 갱신된 토큰 저장되었는지 확인
    }

    static class MockHttpSessionLike implements HttpSession {
        private final Map<String, Object> attrs = new HashMap<>();

        @Override public Object getAttribute(String name) { return attrs.get(name); }
        @Override public void setAttribute(String name, Object value) { attrs.put(name, value); }
        @Override public void removeAttribute(String name) { attrs.remove(name); }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }

        // 이하 더미 구현
        @Override public long getCreationTime() { return 0; }
        @Override public String getId() { return "mock"; }
        @Override public long getLastAccessedTime() { return 0; }
        @Override public javax.servlet.ServletContext getServletContext() { return null; }
        @Override public void setMaxInactiveInterval(int interval) {}
        @Override public int getMaxInactiveInterval() { return 0; }
        @Override public javax.servlet.http.HttpSessionContext getSessionContext() { return null; }
        @Override public Object getValue(String name) { return null; }
        @Override public String[] getValueNames() { return new String[0]; }
        @Override public void putValue(String name, Object value) {}
        @Override public void removeValue(String name) {}
        @Override public void invalidate() {}
        @Override public boolean isNew() { return false; }
    }

}
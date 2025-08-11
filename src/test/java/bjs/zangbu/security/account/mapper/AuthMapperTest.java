package bjs.zangbu.security.account.mapper;

import static bjs.zangbu.global.formatter.PrettyJsonFormatter.toPrettyJson;
import static org.junit.jupiter.api.Assertions.*;

import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.security.account.vo.Member;
import java.util.UUID;
import javax.sql.DataSource;

import bjs.zangbu.security.account.vo.MemberEnum;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;


/**
 * {@link AuthMapper} 통합 테스트
 *
 * - Spring TestContext + JUnit5
 * - 각 테스트는 트랜잭션 롤백
 * - 픽스처는 JdbcTemplate로 직접 INSERT하여 상태를 명확히 통제
 */
@SpringJUnitConfig(classes = RootConfig.class)
@Transactional
@ActiveProfiles("test")
@Log4j2
class AuthMapperTest {

    @Autowired private AuthMapper authMapper;
    @Autowired private DataSource dataSource;
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        assertNotNull(authMapper, "AuthMapper 주입 실패");
        jdbc = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("insertMember: 신규 회원 가입 성공")
    void insertMember_success() {
        // given
        String memberId = UUID.randomUUID().toString();
        String email = "join_" + memberId.substring(0, 8) + "@ex.com";

        Member m = new Member();
        m.setMemberId(memberId);
        m.setEmail(email);
        m.setPassword("$2a$10$dummybcrypt"); // 테스트 해시(혹은 평문) – 검증만 할 거라 값 자체는 중요치 않음
        m.setPhone("010-1111-2222");
        m.setNickname("nick_join");
        m.setIdentity("1234567"); // 주민번호 뒷자리만
        m.setRole(MemberEnum.valueOf("ROLE_MEMBER"));
        m.setBirth("950101");     // YYMMDD
        m.setName("홍길동");
        m.setConsent(true);
        m.setTelecom("SKT");

        // when
        int affected = authMapper.insertMember(m);

        // then
        assertEquals(1, affected);

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM member WHERE member_id = ? AND email = ?",
                Integer.class, memberId, email);
        assertEquals(1, count);

        Member found = authMapper.findByEmail(email);
        assertNotNull(found);
        assertEquals(email, found.getEmail());
        log.info("\ninserted=\n{}", toPrettyJson(found));
    }

    @Test
    @DisplayName("updatePassword: 비밀번호 변경 성공")
    void updatePassword_success() {
        // given
        String memberId = insertMemberFixture("user_pw@t.com", "nick_pw");
        String newPw = "$2a$10$newbcrypt";

        // when
        int rows = authMapper.updatePassword(memberId, newPw);

        // then
        assertEquals(1, rows);
        String dbPw = jdbc.queryForObject(
                "SELECT password FROM member WHERE member_id = ?",
                String.class, memberId);
        assertEquals(newPw, dbPw);
    }

    @Test
    @DisplayName("findByEmail: 로그인용 회원 조회")
    void findByEmail_success() {
        // given
        String email = "login_" + UUID.randomUUID().toString().substring(0, 8) + "@t.com";
        insertMemberFixture(email, "nick_login");

        // when
        Member found = authMapper.findByEmail(email);

        // then
        assertNotNull(found);
        assertEquals(email, found.getEmail());
        assertNotNull(found.getPassword());
    }

    @Test
    @DisplayName("findEmailByNameAndPhone: 이름, 전화번호로 이메일 찾기")
    void findEmailByNameAndPhone_success() {
        // given
        String email = "find_" + UUID.randomUUID().toString().substring(0, 8) + "@t.com";
        String name = "김철수";
        String phone = "010-2222-3333";
        insertMemberFixture(email, "nick_find", name, phone);

        // when
        String foundEmail = authMapper.findEmailByNameAndPhone(name, phone);

        // then
        assertEquals(email, foundEmail);
    }

    @Test
    @DisplayName("countByEmail: 이메일 중복 카운트")
    void countByEmail_success() {
        // given
        String email = "dup_" + UUID.randomUUID().toString().substring(0, 8) + "@t.com";
        assertEquals(0, authMapper.countByEmail(email));

        insertMemberFixture(email, "nick_dup");

        // when & then
        assertEquals(1, authMapper.countByEmail(email));
    }

    @Test
    @DisplayName("countByNickname: 닉네임 중복 카운트")
    void countByNickname_success() {
        // given
        String nick = "nick_" + UUID.randomUUID().toString().substring(0, 6);
        assertEquals(0, authMapper.countByNickname(nick));

        String email = "nick_" + UUID.randomUUID().toString().substring(0, 8) + "@t.com";
        insertMemberFixture(email, nick);

        // when & then
        assertEquals(1, authMapper.countByNickname(nick));
    }

    // =========================
    // 픽스처 INSERT helper
    // =========================

    /**
     * 최소 필드로 member 한 건 삽입
     * @return 생성한 member_id
     */
    private String insertMemberFixture(String email, String nickname) {
        return insertMemberFixture(email, nickname, "홍길동", "010-0000-0000");
    }

    /**
     * 이름/전화번호까지 지정해서 member 한 건 삽입
     * @return 생성한 member_id
     */
    private String insertMemberFixture(String email, String nickname, String name, String phone) {
        String memberId = UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO member(member_id,email,password,phone,nickname,identity,`role`,birth,name,consent,telecom) " +
                        "VALUES (?,?,?,?,?,'1234567','ROLE_MEMBER','950101',?,1,'SKT')",
                memberId, email, "$2a$10$fixture", phone, nickname, name
        );
        return memberId;
    }
}

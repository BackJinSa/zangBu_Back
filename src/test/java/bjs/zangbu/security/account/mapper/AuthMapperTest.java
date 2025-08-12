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
        m.setPhone("01099998888");
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
    @DisplayName("updatePassword: 비밀번호 재설정 성공")
    void updatePassword_success() {
        // given
        insertMemberFixture("user_pw99@t.com", "nick_pw");
        String email = "user_pw99@t.com";
        String newPw = "$2a$10$newbcrypt";

        // when
        int rows = authMapper.updatePassword(email, newPw);

        // then
        assertEquals(1, rows);
        String dbPw = jdbc.queryForObject(
                "SELECT password FROM member WHERE email = ?",
                String.class, email);
        assertEquals(newPw, dbPw);
    }

    @Test
    @DisplayName("findByEmail: 모든 필드 매핑 확인")
    void findByEmail_returnsFullMember() {
        // given: 모든 필드 세팅한 레코드 삽입
        String memberId = UUID.randomUUID().toString();
        String email    = "full_" + memberId.substring(0, 8) + "@t.com";
        String password = "$2a$10$fixtureHash";
        String phone    = "01033334444";
        String nickname = "nick_full";
        String identity = "2345678";                 // 주민번호 뒷 7자리
        MemberEnum role = MemberEnum.ROLE_MEMBER;    // Enum 매핑 확인
        String birth    = "990101";                  // YYMMDD
        String name     = "김민수";
        Boolean consent = true;
        String telecom  = "KT";

        jdbc.update(
                "INSERT INTO member(member_id,email,password,phone,nickname,identity,`role`,birth,name,consent,telecom) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                memberId, email, password, phone, nickname, identity, role.name(), birth, name, consent, telecom
        );

        // when
        Member found = authMapper.findByEmail(email);

        // then
        assertNotNull(found, "Member should not be null");

        assertAll("모든 컬럼 매핑 검증",
                () -> assertEquals(memberId, found.getMemberId(), "memberId"),
                () -> assertEquals(email,    found.getEmail(),    "email"),
                () -> assertEquals(password, found.getPassword(), "password"),
                () -> assertEquals(phone,    found.getPhone(),    "phone"),
                () -> assertEquals(nickname, found.getNickname(), "nickname"),
                () -> assertEquals(identity, found.getIdentity(), "identity"),
                () -> assertEquals(role,     found.getRole(),     "role(enum)"),
                () -> assertEquals(birth,    found.getBirth(),    "birth"),
                () -> assertEquals(name,     found.getName(),     "name"),
                () -> assertEquals(consent,  found.isConsent(),  "consent"),
                () -> assertEquals(telecom,  found.getTelecom(),  "telecom")
        );

        log.info("\nfound=\n{}", toPrettyJson(found));
    }


    @Test
    @DisplayName("findEmailByNameAndPhone: 이름, 전화번호로 이메일 찾기")
    void findEmailByNameAndPhone_success() {
        // given
        String email = "find_" + UUID.randomUUID().toString().substring(0, 8) + "@t.com";
        String name = "할명수";
        String phone = "01022223333";
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

        // 같은 이메일 있는지 카운트
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

        // 같은 닉네임 있는지 카운트
        assertEquals(1, authMapper.countByNickname(nick));
    }

    // =========================
    // 픽스처 INSERT helper
    // =========================

    /**
     * member 한 건 삽입
     * @return 생성한 member_id
     */
    private String insertMemberFixture(String email, String nickname) {
        return insertMemberFixture(email, nickname, "홍길동", "01000000000");
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

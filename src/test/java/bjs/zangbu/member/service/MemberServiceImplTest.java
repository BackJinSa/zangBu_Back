package bjs.zangbu.member.service;

import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameRequest;
import bjs.zangbu.member.dto.request.MemberRequest.EditPassword;
import bjs.zangbu.member.dto.response.MemberResponse.EditMyPage;
import bjs.zangbu.member.mapper.MemberMapper;
import bjs.zangbu.security.account.vo.Member;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringJUnitConfig(classes = {
        RootConfig.class,
        MemberServiceImplTest.class,
})
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:log4jdbc:mysql://localhost:3306/zangBu",
        "spring.datasource.username=scoula",
        "spring.datasource.password=1234",
        "spring.datasource.driver-class-name=net.sf.log4jdbc.sql.jdbcapi.DriverSpy"
})
@Log4j2
//@Transactional
class MemberServiceImplTest {

    @Autowired MemberService memberService;
    @Autowired MemberMapper memberMapper;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired DataSource dataSource;

    JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc = new JdbcTemplate(dataSource);
    }

    private String insertMember(String email, String rawPw, String phone, String nickname, String birth,
                                String name, boolean consent, String telecom, String identity) {
        String memberId = UUID.randomUUID().toString();
        String encoded = passwordEncoder.encode(rawPw);
        jdbc.update("""
            INSERT INTO member(member_id,email,password,phone,nickname,identity,`role`,birth,name,consent,telecom)
            VALUES (?,?,?,?,?,?,'ROLE_MEMBER',?,?,?,?)
        """, memberId, email, encoded, phone, nickname, identity, birth, name, consent, telecom);
        return memberId;
    }

    /* -------------------- getMyPageInfo -------------------- */

    @Test
    @DisplayName("getMyPageInfo: 성공(닉네임/암호 반환)")
    void getMyPageInfo_success() {
        String email = "mypage_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String memberId = insertMember(email, "Pw1!", "01011119998", "nickAb", "950101", "홍길동", true, "SKT", "1234567");

        EditMyPage out = memberService.getMyPageInfo(email);
        log.info("nickname={}", out.getNickName());

        assertEquals("nickAb", out.getNickName());
        assertTrue(passwordEncoder.matches("Pw1!", out.getPassword()));
    }

    @Test
    @DisplayName("getMyPageInfo: 회원 없으면 400")
    void getMyPageInfo_notFound_throws() {
        assertThrows(ResponseStatusException.class,
                () -> memberService.getMyPageInfo("nope_" + UUID.randomUUID()+"@t.com"));
    }


    /* -------------------- editPassword -------------------- */

    @Test
    @DisplayName("editPassword: 현재 비밀번호 일치 → 변경 성공")
    void editPassword_success() {
        String email = "pw_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String memberId = insertMember(email, "OldPw1!", "01011112222", "nickB", "950101", "홍길동", true, "SKT", "1234567");

        EditPassword req = new EditPassword();
        req.setCurrentPassword("OldPw1!");
        req.setNewPassword("NewPw1!");

        memberService.editPassword(memberId, req);

        // DB에 실제로 변경되었는지 확인
        String enc = memberMapper.findPasswordByMemberId(memberId);
        log.info("newPassword={}", enc);
        assertTrue(passwordEncoder.matches("NewPw1!", enc));
    }

    @Test
    @DisplayName("editNickname: 중복이면 실패")
    void editNickname_duplicated_throws() {
        String email = "nn_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String nick  = "dup_" + UUID.randomUUID().toString().substring(0,6);
        String memberId = insertMember(email, "Pw1!", "01011112222", nick, "950101", "홍길동", true, "SKT", "1234567");

        EditNicknameRequest r = new EditNicknameRequest();
        r.setNewNickname(nick);
        assertThrows(IllegalArgumentException.class, () -> memberService.editNickname(memberId, r));
    }

    @Test
    @DisplayName("editNickname: 성공")
    void editNickname_success() {
        String email = "nn2_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String memberId = insertMember(email, "Pw1!", "01011112222", "oldNick", "950101", "홍길동", true, "SKT", "1234567");

        EditNicknameRequest r = new EditNicknameRequest();
        r.setNewNickname("newNick_"+UUID.randomUUID().toString().substring(0,6));
        memberService.editNickname(memberId, r);

        assertEquals(r.getNewNickname(), memberService.getNickname(memberId));
    }


    /* -------------------- 닉네임 중복/수정 -------------------- */

    @Test
    @DisplayName("isNicknameDuplicated: 중복 true/false")
    void nicknameDuplicated() {
        String email = "dup_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String nick  = "nickDup_" + UUID.randomUUID().toString().substring(0,6);
        log.info("nick={}", nick);
        insertMember(email, "Pw1!", "01000000000", nick, "950101", "홍길동", true, "SKT", "1234567");

        assertTrue(memberService.isNicknameDuplicated(nick)); //중복이므로 true
        assertFalse(memberService.isNicknameDuplicated("newNick_" + UUID.randomUUID().toString().substring(0,6)));
    }

    /* -------------------- 회원 탈퇴 -------------------- */

    @Test
    @DisplayName("removeMember: 성공(실제 삭제)")
    void removeMember_success() {
        String email = "rm_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String memberId = insertMember(email, "Pw1!", "01011112222", "nickR", "950101", "홍길동", true, "SKT", "1234567");

        memberService.removeMember(memberId);

        int count = jdbc.queryForObject("SELECT COUNT(*) FROM member WHERE member_id=?", Integer.class, memberId);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("removeMember: 삭제 대상 없으면 실패")
    void removeMember_notFound_throws() {
        assertThrows(IllegalStateException.class, () -> memberService.removeMember(UUID.randomUUID().toString()));
    }


    /* -------------------- 닉네임/동의/생년/주민 -------------------- */

    @Test
    @DisplayName("getNickname: 성공")
    void getNickname_success() {
        String email = "gn_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String memberId = insertMember(email, "Pw1!", "01011112222", "nickZ", "950101", "홍길동", true, "SKT", "1234567");
        log.info("nick={}", memberService.getNickname(memberId));
        assertEquals("nickZ", memberService.getNickname(memberId));
    }

    @Test
    @DisplayName("updateFcmConsent/getFcmConsent: true/false 토글")
    void fcmConsent_toggle() {
        String email = "fcm_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String memberId = insertMember(email, "Pw1!", "01011112222", "nickF", "950101", "홍길동", false, "SKT", "1234567");

        memberService.updateFcmConsent(memberId, true);
        assertTrue(memberService.getFcmConsent(memberId));

        memberService.updateFcmConsent(memberId, false);
        assertFalse(memberService.getFcmConsent(memberId));
    }

    @Test
    @DisplayName("getBirth/getIdentity: 성공")
    void getBirth_identity_success() {
        String email = "bi_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String memberId = insertMember(email, "Pw1!", "01011112222", "nickB2", "950101", "홍길동", true, "SKT", "8888888");

        assertEquals("950101", memberService.getBirth(memberId));
        assertEquals("8888888", memberService.getIdentity(memberId));
    }

    /* -------------------- 북마크 -------------------- */

    @Test
    @DisplayName("getBookmarks: 데이터 없으면 400")
    void getBookmarks_empty_throws() {
        String fakeMemberId = UUID.randomUUID().toString();
        assertThrows(ResponseStatusException.class, () -> memberService.getBookmarks(fakeMemberId));
    }

    @Test
    @DisplayName("deleteBookmark: 대상 없으면 400")
    void deleteBookmark_notFound_throws() {
        String email = "bm_" + UUID.randomUUID().toString().substring(0,8) + "@t.com";
        String memberId = insertMember(email, "Pw1!", "01011112222", "nickBM", "950101", "홍길동", true, "SKT", "1234567");

        assertThrows(ResponseStatusException.class,
                () -> memberService.deleteBookmark(memberId, 999_999_999L));
    }


}

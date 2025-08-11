package bjs.zangbu.member.mapper;

import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.member.dto.join.BookmarkBuilding;
import bjs.zangbu.security.account.vo.Member;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static bjs.zangbu.global.formatter.PrettyJsonFormatter.toPrettyJson;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = RootConfig.class)
//@Transactional            // 테스트 후 롤백
@ActiveProfiles("test")
@Log4j2
class MemberMapperTest {

    @Autowired MemberMapper memberMapper;
    @Autowired DataSource dataSource;
    JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        assertNotNull(memberMapper, "MemberMapper 주입 실패");
        jdbc = new JdbcTemplate(dataSource);
    }

    // ----------------------
    // update 계열
    // ----------------------

    @Test
    @DisplayName("updatePassword: 비밀번호 변경")
    void updatePassword_success() {
        String memberId = insertMember("upd_pw1234@t.com", "nick_pw", false);

        int rows = memberMapper.updatePassword(memberId, "$2a$10$newhash");
        assertEquals(1, rows);

        String dbPw = jdbc.queryForObject(
                "SELECT password FROM member WHERE member_id = ?",
                String.class, memberId);
        assertEquals("$2a$10$newhash", dbPw);
    }

    @Test
    @DisplayName("updateNickname: 닉네임 변경")
    void updateNickname_success() {
        String memberId = insertMember("upd_nick12345@t.com", "oldNick", false);

        int rows = memberMapper.updateNickname(memberId, "newNick");
        assertEquals(1, rows);

        String nick = jdbc.queryForObject(
                "SELECT nickname FROM member WHERE member_id = ?",
                String.class, memberId);
        assertEquals("newNick", nick);
    }

    @Test
    @DisplayName("updateFcmConsent: 알림 수신 동의 변경")
    void updateFcmConsent_success() {
        String memberId = insertMember("consent@t.com", "nick", false);

        memberMapper.updateFcmConsent(memberId, true);

        Boolean consent = memberMapper.selectFcmConsentByMemberId(memberId);
        assertTrue(consent);
    }

    // ----------------------
    // delete 계열
    // ----------------------

    @Test
    @DisplayName("deleteBookMark: 북마크 삭제")
    void deleteBookMark_success() {
        String memberId = insertMember("bm_del@t.com", "nick", true);
        Long complexId = insertComplex();
        Long b1 = insertBuilding(memberId, complexId, "판매자", "TRADING",
                Timestamp.valueOf(LocalDateTime.of(2025,1,1,10,0)));
        insertBookmark(memberId, b1, complexId);

        int rows = memberMapper.deleteBookMark(memberId, b1);
        assertEquals(1, rows);

        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM bookmark WHERE member_id=? AND building_id=?",
                Integer.class, memberId, b1);
        assertEquals(0, cnt);
    }

    @Test
    @DisplayName("deleteMemberId: 회원 삭제")
    void deleteMemberId_success() {
        //String memberId = insertMember("del@t.com", "nick", true);
        String memberId = "0154b976-227f-4691-a2cd-57e86dedd775";

        int rows = memberMapper.deleteMemberId(memberId);
        assertEquals(1, rows);

        Integer cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM member WHERE member_id=?",
                Integer.class, memberId);
        assertEquals(0, cnt);
    }

    // ----------------------
    // select 단건/카운트
    // ----------------------

    @Test
    @DisplayName("get: 회원 정보(email, nickname) 조회")
    void get_success() {
        String memberId = insertMember("get@t.com", "nick_get", true);

        Member m = memberMapper.get(memberId);
        assertNotNull(m);
        assertEquals("get@t.com", m.getEmail());
        assertEquals("nick_get", m.getNickname());
    }

    @Test
    @DisplayName("findPasswordByMemberId: 비밀번호 조회")
    void findPasswordByMemberId_success() {
        String memberId = insertMember("pw_find@t.com", "nick", true);

        String pw = memberMapper.findPasswordByMemberId(memberId);
        assertNotNull(pw);
        assertEquals("$2a$10$fixture", pw); // 아래 insertMember에서 넣은 값과 동일
    }

    @Test
    @DisplayName("countByNickname: 닉네임 중복 카운트")
    void countByNickname_success() {
        insertMember("dup@t.com", "dupNick", false);

        Integer cnt = memberMapper.countByNickname("dupNick");
        assertEquals(1, cnt);
    }

    @Test
    @DisplayName("getNicknameByMemberId: 닉네임 조회")
    void getNicknameByMemberId_success() {
        String memberId = insertMember("nick@t.com", "seeNick", true);

        String nick = memberMapper.getNicknameByMemberId(memberId);
        log.info("\nnick=\n{}", toPrettyJson(nick));

        assertEquals("seeNick", nick);
    }

    @Test
    @DisplayName("selectFcmConsentByMemberId: 알림 수신 동의 조회")
    void selectFcmConsentByMemberId_success() {
        String memberId = insertMember("cons@t.com", "nick", true);

        Boolean c = memberMapper.selectFcmConsentByMemberId(memberId);
        log.info("\nc=\n{}", toPrettyJson(c));

        assertTrue(c);
    }

    @Test
    @DisplayName("findByEmail: * 전체 컬럼 매핑 대략 확인")
    void findByEmail_success() {
        String memberId = insertMember("find9999@t.com", "nick9999", true);

        Member m = memberMapper.findByEmail("find9999@t.com");
        assertNotNull(m);

        assertEquals(memberId, m.getMemberId());
        log.info("\nmemberId=\n{}", toPrettyJson(m.getMemberId()));

        assertEquals("find9999@t.com", m.getEmail());
        log.info("\nemail=\n{}", toPrettyJson(m.getEmail()));

        assertEquals("nick9999", m.getNickname());
        log.info("\nnick=\n{}", toPrettyJson(m.getNickname()));
    }

    // ----------------------
    // getBookmarksByMemberId (조인/최신 이미지/정렬)
    // ----------------------

    @Test
    @DisplayName("getBookmarksByMemberId: 최신 이미지 1장 + created_at DESC 정렬")
    void getBookmarksByMemberId_success_latestImageAndOrder() {
        String memberId = insertMember("bm@t.com", "nick", true);
        Long complexId = insertComplex();

        // building 두 개: b2가 더 최신(created_at)
        Long b1 = insertBuilding(memberId, complexId, "판매자A", "TRADING",
                Timestamp.valueOf(LocalDateTime.of(2025,1,1,10,0)));
        Long b2 = insertBuilding(memberId, complexId, "판매자B", "RENT",
                Timestamp.valueOf(LocalDateTime.of(2025,2,1,12,0)));

        // 각 빌딩 최신 이미지 설정
        insertImage(b1, "https://img/a_old.jpg", Timestamp.valueOf(LocalDateTime.of(2025,1,1,10,0)));
        insertImage(b1, "https://img/a_new.jpg", Timestamp.valueOf(LocalDateTime.of(2025,1,2,9,0)));
        insertImage(b2, "https://img/b_new.jpg", Timestamp.valueOf(LocalDateTime.of(2025,2,1,12,0)));

        // 북마크
        insertBookmark(memberId, b1, complexId);
        insertBookmark(memberId, b2, complexId);

        List<BookmarkBuilding> list = memberMapper.getBookmarksByMemberId(memberId);
        assertNotNull(list);
        assertEquals(2, list.size());

        // 최신 생성일의 b2가 먼저
        assertEquals(b2, list.get(0).getBuildingId());
        assertEquals("https://img/b_new.jpg", list.get(0).getImageUrl());

        // b1의 최신 이미지
        assertEquals(b1, list.get(1).getBuildingId());
        assertEquals("https://img/a_new.jpg", list.get(1).getImageUrl());
    }

    // =========================
    // 픽스처 INSERT helpers
    // =========================

    private String insertMember(String email, String nickname, boolean consent) {
        String memberId = UUID.randomUUID().toString();
        jdbc.update(
                "INSERT INTO member(member_id,email,password,phone,nickname,identity,`role`,birth,name,consent,telecom) " +
                        "VALUES (?,?,?,?,?,'1234567','ROLE_MEMBER','950101','테스터',?, 'SKT')",
                memberId, email, "$2a$10$fixture", "01011112222", nickname, consent
        );
        return memberId;
    }

    private Long insertComplex() {
        jdbc.update(
                "INSERT INTO complex_list(res_type,complex_name,complex_no,sido,sigungu,si_code,eupmyeondong,address,zonecode,building_name,dong,ho,roadName) " +
                        "VALUES ('APT','테스트단지',1,'서울','강남구','11110','역삼동','서울 강남구 역삼동 1-1','06234','래미안','101','1001','테헤란로')"
        );
        return jdbc.queryForObject("SELECT MAX(complex_id) FROM complex_list", Long.class);
    }

    private Long insertBuilding(String memberId, Long complexId, String sellerNickname, String saleType, Timestamp createdAt) {
        jdbc.update(
                "INSERT INTO building(member_id,complex_id,seller_nickname,sale_type,price,deposit,bookmark_count,created_at,building_name,seller_type,property_type,move_date,info_one_line,info_building,contact_name,contact_phone,facility) " +
                        "VALUES (?,?,?,?, ?,?,0, ?, '테스트빌딩','OWNER','APARTMENT', CURRENT_DATE, '한 줄 소개','건물 설명','담당자','01033334444','편의시설')",
                memberId, complexId, sellerNickname, saleType, 80000, 1000, createdAt
        );
        return jdbc.queryForObject("SELECT MAX(building_id) FROM building", Long.class);
    }

    private void insertImage(Long buildingId, String url, Timestamp createdAt) {
        jdbc.update(
                "INSERT INTO image_list(building_id,image_url,created_at) VALUES (?,?,?)",
                buildingId, url, createdAt
        );
    }

    private void insertBookmark(String memberId, Long buildingId, Long complexId) {
        // bookmark 테이블 스키마에 complex_id가 있다면 아래 VALUES 3번째에 complexId 넣기
        try {
            jdbc.update("INSERT INTO bookmark(member_id, building_id, complex_id) VALUES (?,?,?)",
                    memberId, buildingId, complexId);
        } catch (Exception e) {
            // complex_id가 없는 스키마일 수도 있으므로 fallback
            jdbc.update("INSERT INTO bookmark(member_id, building_id) VALUES (?,?)",
                    memberId, buildingId);
        }
    }
}

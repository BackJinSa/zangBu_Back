package bjs.zangbu.complexList.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bjs.zangbu.complexList.vo.ComplexList;
import bjs.zangbu.global.config.RootConfig;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(classes = RootConfig.class)
@Transactional
@ActiveProfiles("test")
@Log4j2
class ComplexListMapperTest {

  @Autowired
  private ComplexListMapper complexListMapper;

  @Autowired
  private DataSource dataSource;

  private JdbcTemplate jdbc;

  @BeforeEach
  void setUp() {
    assertNotNull(complexListMapper, "ComplexListMapper 주입 실패");
    jdbc = new JdbcTemplate(dataSource);
  }

  @Test
  @DisplayName("createComplexList: 단지 삽입 및 selectKey(complexId) 매핑")
  void createComplexList() {
    // given
    ComplexList complex = new ComplexList(
        null,                // complexId (selectKey로 세팅 기대)
        "APT",               // resType
        "테스트단지",          // complexName
        "11230103001448265",              // complexNo
        "서울",               // sido
        "강남구",             // sigungu
        "11110",             // siCode
        "역삼동",             // eupmyeondong
        "TX-001",            // transactionId
        "서울 강남구 역삼동 1-1", // address
        "06234",             // zonecode
        "래미안",             // buildingName
        "역삼동",             // bname
        "101동",             // dong
        "1001호",            // ho
        "테헤란로"             // roadName
    );

    // when
    int result = complexListMapper.createComplexList(complex);

    // then
    // MyBatis insert의 반환값은 보통 영향 행 수(1)입니다. (Mapper 시그니처가 Long이어도 1L일 가능성 높음)
    assertNotNull(result, "insert 영향 행 수(or key)가 null이면 안 됨");
    assertTrue(result >= 0, "insert 결과값은 0 이상이어야 함");

    // selectKey로 complex.complexId가 세팅되었는지 확인
    // ※ VO에 setter가 없으면 selectKey가 주입되지 않을 수 있음 → complexId에 @Setter 추가 권장
    log.info("selectKey로 세팅된 complexId = {}", complex.getComplexId());
    assertNotNull(complex.getComplexId(), "selectKey로 complexId가 세팅되어야 함 (VO에 @Setter 필요)");

    // 실제 DB에도 1건 들어갔는지 검증
    Integer cnt = jdbc.queryForObject(
        "SELECT COUNT(*) FROM complex_list WHERE complex_id = ?",
        Integer.class, complex.getComplexId()
    );
    assertEquals(1, cnt);
  }

  @Test
  @DisplayName("getComplexNoByBuildingId: building.complex_id 조인으로 complex_no 조회")
  void getComplexNoByBuildingId() {
    // given: 단지 1건 삽입 (JDBC로 직접)
    Long complexId = insertComplexByJdbc(
        "APT", "조회용단지", 123456, "서울", "송파구", "11710",
        "장지동", "TX-ABC", "서울 송파구 장지동 888", "05819",
        "센트럴파크", "장지동", "101동", "1202호", "문정로"
    );

    // building 1건 삽입 (complex_id FK 연결)
    String memberId = insertMemberByJdbc();
    Long buildingId = insertBuildingByJdbc(memberId, complexId, "판매자", "TRADING");

    // when
    String complexNo = complexListMapper.getComplexNoByBuildingId(buildingId);

    // then
    assertNotNull(complexNo);
    assertEquals(123456L, complexNo);
  }

  // ===================================
  // 테스트 픽스처 INSERT 헬퍼 (JDBC 직삽)
  // ===================================

  private Long insertComplexByJdbc(
      String resType, String complexName, int complexNo,
      String sido, String sigungu, String siCode, String eupmyeondong,
      String transactionId, String address, String zonecode,
      String buildingName, String bname, String dong, String ho, String roadName
  ) {
    jdbc.update(
        "INSERT INTO complex_list(" +
            "res_type, complex_name, complex_no, sido, sigungu, si_code, eupmyeondong, " +
            "transaction_id, address, zonecode, building_name, bname, dong, ho, roadName" +
            ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
        resType, complexName, complexNo, sido, sigungu, siCode, eupmyeondong,
        transactionId, address, zonecode, buildingName, bname, dong, ho, roadName
    );
    return jdbc.queryForObject("SELECT MAX(complex_id) FROM complex_list", Long.class);
  }

  private String insertMemberByJdbc() {
    String memberId = UUID.randomUUID().toString();
    String email = memberId.substring(0, 24) + "@t.com";
    jdbc.update(
        "INSERT INTO member(member_id,email,password,phone,nickname,`role`,birth,name,consent,telecom) "
            +
            "VALUES (?,?,?,?,?,'ROLE_MEMBER','950101','테스터',1,'SKT')",
        memberId, email, "pw", "010-1111-2222", "nick"
    );
    return memberId;
  }

  private Long insertBuildingByJdbc(String memberId, Long complexId, String sellerNickname,
      String saleType) {
    jdbc.update(
        "INSERT INTO building(member_id,complex_id,seller_nickname,sale_type,price,deposit,bookmark_count,"
            +
            "building_name,seller_type,property_type,move_date,info_oneline,contact_name,contact_phone,facility) "
            +
            "VALUES (?,?,?,?, ?,?,0,'테스트빌딩','OWNER','APARTMENT', NOW(),'한 줄 소개','담당자','010-3333-4444','편의점')",
        memberId, complexId, sellerNickname, saleType, 80000, 1000
    );
    return jdbc.queryForObject("SELECT MAX(building_id) FROM building", Long.class);
  }
}

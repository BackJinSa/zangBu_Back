package bjs.zangbu.building.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import bjs.zangbu.building.vo.Building;
import bjs.zangbu.building.vo.PropertyType;
import bjs.zangbu.building.vo.SellerType;
import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.notification.vo.SaleType;
import java.time.LocalDateTime;
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
class BuildingMapperTest {

  @Autowired
  private BuildingMapper buildingMapper;

  @Autowired
  private DataSource dataSource;

  private JdbcTemplate jdbc;

  @BeforeEach
  void setUp() {
    assertNotNull(buildingMapper, "BuildingMapper 주입 실패");
    jdbc = new JdbcTemplate(dataSource);
  }

  @Test
  @DisplayName("createBuilding: 매물 등록 후 buildingId selectKey 매핑 확인")
  void createBuilding_success() {
    // given
    String memberId = insertMemberByJdbc();
    Long complexId = insertComplexByJdbc();

    Building building = new Building(
        null, // buildingId (PK, selectKey로 세팅)
        "판매자닉",                 // sellerNickname
        SaleType.TRADING,          // saleType (ENUM)
        80000,                     // price
        1000L,                     // deposit
        0,                         // bookmarkCount
        LocalDateTime.now(),       // createdAt
        "테스트빌딩",               // buildingName
        SellerType.OWNER,          // sellerType (ENUM)
        PropertyType.APARTMENT,    // propertyType (ENUM)
        LocalDateTime.now(),       // moveDate
        "한 줄 소개",               // infoOneline
        "상세 설명",                // infoBuilding
        "담당자",                   // contactName
        "010-3333-4444",           // contactPhone
        "편의점, 주차장",           // facility
        14.f,
        memberId,                  // memberId (FK)
        complexId                   // complexId (FK)
    );

    // when
    buildingMapper.createBuilding(building);
    Long newId = buildingMapper.selectLastInsertId(); // 방법 A 기준

// then
    assertNotNull(newId);
    Integer cnt = jdbc.queryForObject(
        "SELECT COUNT(*) FROM building WHERE building_id = ?",
        Integer.class, newId
    );
    assertEquals(1, cnt);
  }

  // ==========================
  // 픽스처 INSERT 헬퍼
  // ==========================
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

  private Long insertComplexByJdbc() {
    jdbc.update(
        "INSERT INTO complex_list(" +
            "res_type, complex_name, complex_no, sido, sigungu, si_code, eupmyeondong, " +
            "transaction_id, address, zonecode, building_name, bname, dong, ho, roadName" +
            ") VALUES ('APT','테스트단지',1,'서울','강남구','11110','역삼동'," +
            "'TX-001','서울 강남구 역삼동 1-1','06234','래미안','역삼동','101동','1001호','테헤란로')"
    );
    return jdbc.queryForObject("SELECT MAX(complex_id) FROM complex_list", Long.class);
  }
}

//package bjs.zangbu.building.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import bjs.zangbu.building.dto.request.BuildingRequest;
//import bjs.zangbu.building.dto.request.BuildingRequest.BuildingDetails;
//import bjs.zangbu.building.dto.request.BuildingRequest.SaleRegistrationRequest;
//import bjs.zangbu.building.vo.PropertyType;
//import bjs.zangbu.building.vo.SellerType;
//import bjs.zangbu.global.config.RootConfig;
//import bjs.zangbu.notification.vo.SaleType;
//
//import java.io.UnsupportedEncodingException;
//import java.time.LocalDateTime;
//import java.util.UUID;
//import javax.sql.DataSource;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//
//@SpringJUnitConfig(classes = {RootConfig.class})
//@Transactional
//@ActiveProfiles("test")
//@Log4j2
//class BuildingServiceTest {
//
//  // 너희 실제 서비스 타입/패키지명으로 변경
//  @Autowired
//  private BuildingService buildingService;
//
//  @Autowired
//  private DataSource dataSource;
//
//  private JdbcTemplate jdbc() {
//    return new JdbcTemplate(dataSource);
//  }
//
//  @Test
//  @DisplayName("png로 업로더 우회: complex/building/image 모두 INSERT (image_url은 NULL)")
//  void saleRegistration_png_flow_ok() throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
//    // given
//    String memberId = insertMember("seller_nick");
//
//    String identity = "0110203018419";
//
//    // 요청 DTO (네 DTO 구조 그대로 사용)
//    BuildingRequest.BuildingDetails b =
//        new BuildingDetails(
//            "판매자닉", SaleType.TRADING, 80000, 1000L, 0,
//            "테스트빌딩", SellerType.OWNER, PropertyType.APARTMENT,
//            LocalDateTime.now(), "한 줄", "상세", "담당자", "010-3333-4444", "편의점"
//        );
//
//    BuildingRequest.ComplexDetails c =
//        new BuildingRequest.ComplexDetails(
//            "APT", "테스트단지", "11230103001448265", "서울", "강남구", "11110",
//            "역삼동", "TX-001", "서울 강남구 역삼동 1-1", "06234",
//            "래미안", "역삼동", "101동", "1001호", "테헤란로"
//        );
//
//    MultipartFile file = new MockMultipartFile(
//        "image", "room.jpeg", "image/jpeg", new byte[]{1, 2, 3}
//    );
//    BuildingRequest.ImageDetails img =
//        new BuildingRequest.ImageDetails(file);
//
//    SaleRegistrationRequest req =
//        new SaleRegistrationRequest(b, c, img, identity);
//
//    int c1 = count("complex_list");
//    int b1 = count("building");
//    int i1 = count("image_list");
//
//    // when
//    buildingService.SaleRegistration(req, memberId);
//
//    // then
//    int c2 = count("complex_list");
//    int b2 = count("building");
//    int i2 = count("image_list");
//
//    assertEquals(c1 + 1, c2);
//    assertEquals(b1 + 1, b2);
//    assertEquals(i1 + 1, i2);
//
//    Long lastComplexId = getLong("SELECT MAX(complex_id) FROM complex_list");
//    Long lastBuildingId = getLong("SELECT MAX(building_id) FROM building");
//
//    // image_list에 방금 건물 id로 레코드가 생겼는지
//    Integer cnt = jdbc().queryForObject(
//        "SELECT COUNT(*) FROM image_list WHERE building_id=? AND complex_id=?",
//        Integer.class, lastBuildingId, lastComplexId
//    );
//    assertNotNull(cnt);
//    assertTrue(cnt > 0);
//
//    String url = jdbc().queryForObject(
//        "SELECT image_url FROM image_list WHERE building_id=? ORDER BY image_id DESC LIMIT 1",
//        String.class, lastBuildingId
//    );
//    log.info("url", url);
//    assertNotNull(url);
//  }
//
//  // ---------- helpers ----------
//  private String insertMember(String nick) {
//    String id = UUID.randomUUID().toString();
//    String email = UUID.randomUUID().toString().substring(0, 8) + "@t.com";
//    jdbc().update("""
//        INSERT INTO member(member_id,email,password,phone,nickname,role,birth,name,consent,telecom)
//        VALUES (?,?,?,?,?,'ROLE_MEMBER','950101','테스터',1,'SKT')
//        """, id, email, "pw", "010-1111-2222", nick);
//    return id;
//  }
//
//  private int count(String table) {
//    return jdbc().queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
//  }
//
//  private Long getLong(String sql) {
//    return jdbc().queryForObject(sql, Long.class);
//  }
//}

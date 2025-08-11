package bjs.zangbu.deal.mapper;

import static bjs.zangbu.global.formatter.PrettyJsonFormatter.toPrettyJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bjs.zangbu.deal.dto.join.DealDocumentInfo;
import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import bjs.zangbu.deal.dto.join.DealWithSaleType;
import bjs.zangbu.deal.dto.request.EstateRegistrationRequest;
import bjs.zangbu.deal.dto.response.DealResponse.CreateResult;
import bjs.zangbu.global.config.RootConfig;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
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

/**
 * {@link DealMapper} MyBatis 매퍼의 CRUD 및 조회 메서드를 통합 테스트하는 클래스
 *
 * <p>Spring TestContext + JUnit5 환경에서 실행되며, 테스트 DB에 픽스처 데이터를 직접 INSERT하여
 * 각 Mapper 메서드의 동작을 검증한다.</p>
 *
 * <p>{@link Transactional}이 적용되어 각 테스트 실행 후 데이터가 롤백된다.</p>
 */
@SpringJUnitConfig(classes = RootConfig.class)
@Transactional
@Log4j2
@ActiveProfiles("test")
class DealMapperTest {

  @Autowired
  private DealMapper dealMapper;
  @Autowired
  private DataSource dataSource;
  private JdbcTemplate jdbc;

  @BeforeEach
  void setUp() {
    assertNotNull(dealMapper, "DealMapper 주입 실패");
    jdbc = new JdbcTemplate(dataSource);
  }

  /**
   * 회원 ID로 거래 대기 목록 조회
   *
   * <p>주어진 memberId에 해당하는 거래 대기 목록이 조회되는지 검증</p>
   */
  @Test
  @DisplayName("회원 ID로 거래 대기 목록 조회")
  void getAllWaitingList() {
    // --- given: 픽스처 생성 ---
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId = insertBuilding(memberId, complexId, "홍길동", "TRADING");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "홍길동", "김철수");
    var dealId = insertDeal(chatRoomId, buildingId, memberId, complexId, "BEFORE_CONSUMER");

    // --- when ---
    List<DealWithChatRoom> waitingList = dealMapper.getAllWaitingList(memberId);

    // --- then ---
    assertNotNull(waitingList);
    assertTrue(waitingList.size() > 0);
    log.info("\nwaitingList=\n{}", toPrettyJson(waitingList));
  }

  /**
   * dealId로 buildingId 조회
   *
   * <p>INSERT한 거래의 buildingId를 정확히 조회하는지 검증</p>
   */
  @Test
  @DisplayName("dealId로 buildingId 조회")
  void getBuildingIdByDealId() {
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId = insertBuilding(memberId, complexId, "철수", "CHARTER");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자", "구매자");
    var dealId = insertDeal(chatRoomId, buildingId, memberId, complexId, "MIDDLE_DEAL");

    Long foundBuildingId = dealMapper.getBuildingIdByDealId(dealId);
    assertEquals(buildingId, foundBuildingId);
  }

  /**
   * dealId로 complexId 조회
   *
   * <p>INSERT한 거래의 complexId를 정확히 조회하는지 검증</p>
   */
  @Test
  @DisplayName("dealId로 complexId 조회")
  void getComplexIdByDealId() {
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId = insertBuilding(memberId, complexId, "판매자", "MONTHLY");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자", "구매자");
    var dealId = insertDeal(chatRoomId, buildingId, memberId, complexId, "MIDDLE_DEAL");

    Long foundComplexId = dealMapper.getComplexIdByDealId(dealId);
    assertEquals(complexId, foundComplexId);
  }

  /**
   * Deal 삭제
   *
   * <p>deleteDealById 실행 시 해당 거래가 삭제되고, 삭제 후 SELECT COUNT 결과가 0인지 검증</p>
   */
  @Test
  @DisplayName("Deal 삭제")
  void deleteDealById() {
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId = insertBuilding(memberId, complexId, "판매자", "TRADING");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자", "구매자");
    var dealId = insertDeal(chatRoomId, buildingId, memberId, complexId, "BEFORE_CONSUMER");

    int affected = dealMapper.deleteDealById(dealId);
    assertEquals(1, affected);

    Integer count = jdbc.queryForObject(
        "SELECT COUNT(*) FROM deal WHERE deal_id = ?", Integer.class, dealId);
    assertEquals(0, count);
  }

  /**
   * Deal.status 가져오기
   *
   * <p>거래 생성 시 지정한 status가 getStatusByDealId로 조회되는지 검증</p>
   */
  @Test
  @DisplayName("Deal.status 가져오기")
  void getStatusByDealId() {
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId = insertBuilding(memberId, complexId, "판매자", "TRADING");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자", "구매자");
    var dealId = insertDeal(chatRoomId, buildingId, memberId, complexId, "BEFORE_OWNER");

    String status = dealMapper.getStatusByDealId(dealId);
    assertEquals("BEFORE_OWNER", status);
  }

  /**
   * createDeal 메서드 동작 검증
   *
   * <p>chatRoom 기반 신규 거래 생성 시 keyProperty로 dealId가 매핑되는지 확인</p>
   */
  @Test
  @DisplayName("createDeal: chatRoom 기반 신규 생성 및 keyProperty 매핑")
  void createDeal() {
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId = insertBuilding(memberId, complexId, "판매자", "TRADING");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자", "구매자");

    CreateResult result = new CreateResult(); // keyProperty="result.dealId"에 주입될 객체
    int affected = dealMapper.createDeal(chatRoomId, result);

    assertEquals(1, affected);
    assertNotNull(result.getDealId(), "생성된 dealId가 매핑돼야 함");

    Integer count = jdbc.queryForObject(
        "SELECT COUNT(*) FROM deal WHERE deal_id = ?", Integer.class, result.getDealId());
    assertEquals(1, count);
  }

  /**
   * 오늘 거래된 매물 building_id 조회
   *
   * <p>오늘 날짜로 생성된 거래들의 buildingId 목록이 반환되는지 검증</p>
   */
  @Test
  @DisplayName("오늘 거래된 매물 building_id 조회")
  void selectTodayTradedBuildingIds() {
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId1 = insertBuilding(memberId, complexId, "판매자1", "TRADING");
    var buildingId2 = insertBuilding(memberId, complexId, "판매자2", "TRADING");
    var chatRoomId1 = insertChatRoom(buildingId1, memberId, complexId, "판매자1", "구매자1");
    var chatRoomId2 = insertChatRoom(buildingId2, memberId, complexId, "판매자2", "구매자2");
    // 오늘 날짜로 2건 입력
    insertDeal(chatRoomId1, buildingId1, memberId, complexId, "MIDDLE_DEAL");
    insertDeal(chatRoomId2, buildingId2, memberId, complexId, "BEFORE_CONSUMER");

    List<Long> ids = dealMapper.selectTodayTradedBuildingIds();
    assertTrue(ids.contains(buildingId1));
    assertTrue(ids.contains(buildingId2));
  }

  /**
   * 판매중/구매중 목록 조회
   *
   * <p>판매자/구매자 닉네임을 기준으로 각각 대기 목록이 조회되는지 검증</p>
   */
  @Test
  @DisplayName("판매중/구매중 목록 조회 (nickname 기준)")
  void getOnSaleAndPurchaseWaitingList() {
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId = insertBuilding(memberId, complexId, "판매자닉", "TRADING");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자닉", "구매자닉");
    // 목록에서 제외되지 않는 상태로 deal 생성
    insertDeal(chatRoomId, buildingId, memberId, complexId, "MIDDLE_DEAL");

    // 판매자 닉네임 기준
    var onSale = dealMapper.getOnSaleWaitingList(memberId, "판매자닉");
    assertNotNull(onSale);
    assertTrue(onSale.size() > 0);

    // 구매자 닉네임 기준
    var purchase = dealMapper.getPurchaseWaitingList(memberId, "구매자닉");
    assertNotNull(purchase);
    assertTrue(purchase.size() > 0);
  }

  /**
   * findWithType: sale_type 조인 검증
   *
   * <p>거래 ID로 조회 시 sale_type 필드가 조인되어 올바르게 반환되는지 확인</p>
   */
  @Test
  @DisplayName("findWithType: sale_type 조인")
  void findWithType() {
    var memberId = insertMember();
    var complexId = insertComplex();
    var buildingId = insertBuilding(memberId, complexId, "판매자", "MONTHLY");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자", "구매자");
    var dealId = insertDeal(chatRoomId, buildingId, memberId, complexId, "MIDDLE_DEAL");

    DealWithSaleType dto = dealMapper.findWithType(dealId);
    assertNotNull(dto);
    assertEquals(buildingId, dto.getBuildingId());
    assertEquals("MONTHLY", String.valueOf(dto.getSaleType()));
  }

  /**
   * 등기부등본 요청 데이터 조회
   *
   * <p>getEstateRegistrationRequest 호출 시 필요한 필드들이 매핑되는지 검증</p>
   */
  @Test
  @DisplayName("등기부등본 요청 데이터 조회")
  void getEstateRegistrationRequest() {
    var memberId = insertMemberWithInfo("010-1234-5678", "950101");
    var complexId = insertComplexWithAddress();
    var buildingId = insertBuilding(memberId, complexId, "판매자", "TRADING");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자", "구매자");
    var dealId = insertDeal(chatRoomId, buildingId, memberId, complexId, "MIDDLE_DEAL");

    EstateRegistrationRequest req = dealMapper.getEstateRegistrationRequest(dealId);
    assertNotNull(req);
    log.info("\n{}", toPrettyJson(req));
  }

  /**
   * 건축물대장 요청 데이터 조회
   *
   * <p>getDocumentInfo 호출 시 필요한 필드들이 매핑되는지 검증</p>
   */
  @Test
  @DisplayName("건축물대장 요청 데이터 조회")
  void getDocumentInfo() {
    var memberId = insertMemberWithIdentity("900101-1234567", "900101", "010-0000-0000", "홍길동",
        "KT");
    var complexId = insertComplexWithAddress();
    var buildingId = insertBuilding(memberId, complexId, "판매자", "TRADING");
    var chatRoomId = insertChatRoom(buildingId, memberId, complexId, "판매자", "구매자");
    var dealId = insertDeal(chatRoomId, buildingId, memberId, complexId, "MIDDLE_DEAL");

    DealDocumentInfo info = dealMapper.getDocumentInfo(dealId);
    assertNotNull(info);
    log.info("\n{}", toPrettyJson(info));
  }

  // ============================
  // 헬퍼들 (픽스처 INSERT)
  // ============================

  /**
   * member 테이블에 테스트 회원 INSERT
   *
   * @return 생성된 member_id
   */
  private String insertMember() {
    String memberId = java.util.UUID.randomUUID().toString(); // 36자
    String email = memberId.substring(0, 24) + "@t.com"; // 총 29자
    jdbc.update(
        "INSERT INTO member(member_id,email,password,phone,nickname,`role`,birth,name,consent,telecom) "
            +
            "VALUES (?,?,?,?,?,'ROLE_MEMBER','950101','테스터',1,'SKT')",
        memberId, email, "pw", "010-1111-2222", "nick"
    );
    return memberId;
  }

  /**
   * phone, birth 정보를 지정하여 테스트 회원 INSERT
   */
  private String insertMemberWithInfo(String phone, String birth) {
    String memberId = java.util.UUID.randomUUID().toString();
    String email = memberId.substring(0, 24) + "@t.com";
    jdbc.update(
        "INSERT INTO member(member_id,email,password,phone,nickname,`role`,birth,name,consent,telecom) "
            +
            "VALUES (?,?,?,?,?,'ROLE_MEMBER',?,?,1,'SKT')",
        memberId, email, "pw", phone, "nick", birth, "홍길동"
    );
    return memberId;
  }

  /**
   * 주민등록번호, 이름, 통신사 포함 회원 INSERT
   */
  private String insertMemberWithIdentity(String identity, String birth, String phone, String name,
      String telecom) {
    String memberId = java.util.UUID.randomUUID().toString();
    String email = memberId.substring(0, 24) + "@t.com";
    jdbc.update(
        "INSERT INTO member(member_id,email,password,phone,nickname,identity,`role`,birth,name,consent,telecom) "
            +
            "VALUES (?,?,?,?,?,?, 'ROLE_MEMBER',?,?,1,?)",
        memberId, email, "pw", phone, "nick", identity, birth, name, telecom
    );
    return memberId;
  }

  /**
   * complex_list 테이블에 테스트 단지 INSERT
   *
   * @return 생성된 complex_id
   */
  private Long insertComplex() {
    jdbc.update(
        "INSERT INTO complex_list(res_type,complex_name,complex_no,sido,sigungu,si_code,eupmyeondong,address,zonecode,building_name,dong,ho,roadName) "
            +
            "VALUES ('APT','테스트단지',1,'서울','강남구','11110','역삼동','서울 강남구 역삼동 1-1','06234','래미안', '101','1001','테헤란로')");
    return jdbc.queryForObject("SELECT MAX(complex_id) FROM complex_list", Long.class);
  }

  /**
   * 주소 포함 단지 INSERT (현재는 insertComplex와 동일)
   */
  private Long insertComplexWithAddress() {
    return insertComplex();
  }

  /**
   * building 테이블에 테스트 매물 INSERT
   *
   * @return 생성된 building_id
   */
  private Long insertBuilding(String memberId, Long complexId, String sellerNickname,
      String saleType) {
    jdbc.update(
        "INSERT INTO building(member_id,complex_id,seller_nickname,sale_type,price,deposit,bookmark_count,building_name,seller_type,property_type,move_date,info_oneline,contact_name,contact_phone,facility) "
            +
            "VALUES (?,?,?,?, ?,?,0, '테스트빌딩','OWNER','APARTMENT', NOW(), '한 줄 소개', '담당자', '010-3333-4444', '편의점')",
        memberId, complexId, sellerNickname, saleType, 80000, 1000);
    return jdbc.queryForObject("SELECT MAX(building_id) FROM building", Long.class);
  }

  /**
   * chat_room 테이블에 테스트 채팅방 INSERT
   *
   * @return 생성된 chat_room_id
   */
  private String insertChatRoom(Long buildingId, String memberId, Long complexId,
      String sellerNickname, String consumerNickname) {
    String chatRoomId = "cr_" + UUID.randomUUID().toString().substring(0, 8);
    jdbc.update(
        "INSERT INTO chat_room(chat_room_id,building_id,member_id,complex_id,seller_nickname,consumer_nickname,seller_visible,consumer_visible) "
            +
            "VALUES (?,?,?,?,?,?,1,1)",
        chatRoomId, buildingId, memberId, complexId, sellerNickname, consumerNickname);
    return chatRoomId;
  }

  /**
   * deal 테이블에 테스트 거래 INSERT
   *
   * @return 생성된 deal_id
   */
  private Long insertDeal(String chatRoomId, Long buildingId, String memberId, Long complexId,
      String status) {
    jdbc.update(
        "INSERT INTO deal(chat_room_id,building_id,member_id,complex_id,status,created_at) " +
            "VALUES (?,?,?,?,?,?)",
        chatRoomId, buildingId, memberId, complexId, status,
        Timestamp.valueOf(LocalDateTime.now()));
    return jdbc.queryForObject("SELECT MAX(deal_id) FROM deal", Long.class);
  }


}

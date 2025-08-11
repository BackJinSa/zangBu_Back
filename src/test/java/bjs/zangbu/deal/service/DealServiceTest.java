package bjs.zangbu.deal.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bjs.zangbu.deal.dto.request.DealRequest.Status;
import bjs.zangbu.deal.dto.response.DealResponse.Notice;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingList;
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

/**
 * {@link DealService}의 기능을 통합 테스트하는 클래스
 *
 * <p>Spring TestContext + JUnit5 환경에서 실행되며, 테스트용 DB 픽스처 데이터를 삽입하여
 * 거래 안내, 대기 목록 조회, 거래 생성·삭제, 상태 전환 등의 시나리오를 검증한다.</p>
 *
 * <p>{@link Transactional}을 적용하여 각 테스트 실행 후 데이터가 롤백된다.</p>
 */
@SpringJUnitConfig(classes = RootConfig.class)
@Transactional
@ActiveProfiles("test")
@Log4j2
class DealServiceTest {

  @Autowired
  DealService dealService;
  @Autowired
  private DataSource dataSource;
  JdbcTemplate jdbc;

  // 공용 픽스처 식별자
  String sellerId;
  String buyerId;
  Long complexId;
  Long buildingId;
  String chatRoomId;
  Long dealId;

  @BeforeEach
  void setUp() {
    jdbc = new JdbcTemplate(dataSource);

    sellerId = insertMember("seller_nick"); // 판매자
    buyerId = insertMember("buyer_nick");  // 구매자

    complexId = insertComplex();
    buildingId = insertBuilding(sellerId, complexId, "seller_nick", "TRADING"); // 건물 소유=판매자
    chatRoomId = insertChatRoom(buildingId, buyerId, complexId, "seller_nick",
        "buyer_nick"); // 채팅 개설=구매자
    dealId = insertDeal(chatRoomId, buildingId, buyerId, complexId,
        "MIDDLE_DEAL"); // 보통 deal.member_id는 대화 개설자(구매자)
    insertImage(buildingId, sellerId, complexId, "https://example.com/cover.jpg");
  }

  /**
   * 테스트용 회원 데이터 삽입
   *
   * <p>UUID 기반 member_id와 랜덤 이메일, 지정 닉네임을 가진 회원을 생성한다.
   * ROLE_MEMBER, 기본 개인정보, 동의 상태를 설정한다.</p>
   *
   * @param nick 회원 닉네임
   * @return 생성된 member_id
   */
  private String insertMember(String nick) {
    String id = UUID.randomUUID().toString();
    String email = UUID.randomUUID().toString().substring(0, 8) + "@t.com";
    jdbc.update("""
          INSERT INTO member(member_id,email,password,phone,nickname,role,birth,name,consent,telecom)
          VALUES (?,?,?,?,?,'ROLE_MEMBER','950101','테스터',1,'SKT')
        """, id, email, "pw", "010-1111-2222", nick);
    return id;
  }

  /**
   * 테스트용 단지 데이터 삽입
   *
   * <p>단지명=테스트단지, 지역=서울 강남구, 임의 주소 데이터를 가진 complex_list 레코드를 생성한다.</p>
   *
   * @return 생성된 complex_id
   */
  private Long insertComplex() {
    jdbc.update("""
            INSERT INTO complex_list(res_type,complex_name,complex_no,sido,sigungu,si_code,eupmyeondong,address,zonecode,building_name,dong,ho,roadName)
            VALUES ('APT','테스트단지',1,'서울','강남구','11110','역삼동','서울 강남구 역삼동 1-1','06234','래미안','101','1001','테헤란로')
        """);
    return jdbc.queryForObject("SELECT MAX(complex_id) FROM complex_list", Long.class);
  }

  /**
   * 테스트용 매물 데이터 삽입
   *
   * <p>지정된 소유자(memberId)와 complexId, 판매자 닉네임, 매물 종류를 기반으로
   * building 테이블에 레코드를 생성한다. property_type은 APARTMENT로 고정.</p>
   *
   * @param memberId       소유자 ID
   * @param complexId      단지 ID
   * @param sellerNickname 판매자 닉네임
   * @param saleType       거래 유형 (TRADING, MONTHLY, CHARTER 등)
   * @return 생성된 building_id
   */
  private Long insertBuilding(String memberId, Long complexId, String sellerNickname,
      String saleType) {
    jdbc.update("""
            INSERT INTO building(member_id,complex_id,seller_nickname,sale_type,price,deposit,bookmark_count,
                                 building_name,seller_type,property_type,move_date,info_oneline,contact_name,contact_phone,facility)
            VALUES (?,?,?,?, ?,?,0,'테스트빌딩','OWNER','APARTMENT',NOW(),'한 줄 소개','담당자','010-3333-4444','편의점')
        """, memberId, complexId, sellerNickname, saleType, 80000, 1000);
    return jdbc.queryForObject("SELECT MAX(building_id) FROM building", Long.class);
  }

  /**
   * 테스트용 채팅방 데이터 삽입
   *
   * <p>buildingId, complexId, 판매자·구매자 닉네임을 기반으로 chat_room 테이블에 레코드를 생성한다.
   * seller_visible, consumer_visible은 1(true)로 설정된다.</p>
   *
   * @param buildingId 매물 ID
   * @param memberId   채팅 개설자 ID (일반적으로 구매자)
   * @param complexId  단지 ID
   * @param seller     판매자 닉네임
   * @param consumer   구매자 닉네임
   * @return 생성된 chat_room_id
   */
  private String insertChatRoom(Long buildingId, String memberId, Long complexId, String seller,
      String consumer) {
    String id = "cr_" + UUID.randomUUID().toString().substring(0, 8);
    jdbc.update("""
            INSERT INTO chat_room(chat_room_id,building_id,member_id,complex_id,seller_nickname,consumer_nickname,seller_visible,consumer_visible)
            VALUES (?,?,?,?,?,?,1,1)
        """, id, buildingId, memberId, complexId, seller, consumer);
    return id;
  }

  /**
   * 테스트용 거래 데이터 삽입
   *
   * <p>chatRoomId, buildingId, memberId, complexId, 상태값을 기반으로 deal 테이블에 레코드를 생성한다.
   * created_at은 현재 시각으로 설정된다.</p>
   *
   * @param chatRoomId 채팅방 ID
   * @param buildingId 매물 ID
   * @param memberId   거래 생성자 ID
   * @param complexId  단지 ID
   * @param status     거래 상태 문자열 ({@link bjs.zangbu.deal.vo.DealEnum})
   * @return 생성된 deal_id
   */
  private Long insertDeal(String chatRoomId, Long buildingId, String memberId, Long complexId,
      String status) {
    jdbc.update("""
            INSERT INTO deal(chat_room_id,building_id,member_id,complex_id,status,created_at)
            VALUES (?,?,?,?,?, NOW())
        """, chatRoomId, buildingId, memberId, complexId, status);
    return jdbc.queryForObject("SELECT MAX(deal_id) FROM deal", Long.class);
  }

  /**
   * 테스트용 이미지 데이터 삽입
   *
   * <p>buildingId, memberId, complexId, 이미지 URL을 기반으로 image_list 테이블에 레코드를 생성한다.</p>
   *
   * @param buildingId 매물 ID
   * @param memberId   이미지 등록자 ID
   * @param complexId  단지 ID
   * @param url        이미지 URL
   */
  private void insertImage(Long buildingId, String memberId, Long complexId, String url) {
    jdbc.update("""
            INSERT INTO image_list(member_id, complex_id, building_id, image_url)
            VALUES (?,?,?,?)
        """, memberId, complexId, buildingId, url);
  }

  /**
   * 거래 전 안내 조회 - 정상 케이스
   *
   * <p>사전에 삽입한 건물 ID를 기반으로 {@link DealService#getNotice(Long)}를 호출하고,
   * 결과가 null이 아님을 검증한다.</p>
   */
  @Test
  @DisplayName("거래 전 안내 - 정상")
  void getNotice_ok() {
    Notice notice = dealService.getNotice(buildingId);
    assertNotNull(notice);
    log.info("notice: {}", notice);
  }

  /**
   * 전체 거래 대기 목록 조회 - 정상 케이스
   *
   * <p>구매자 ID/닉네임으로 {@link DealService#getAllWaitingList(String, String)}를 호출하여
   * 목록이 비어있지 않음을 검증한다.</p>
   */
  @Test
  @DisplayName("전체 거래 대기 목록 조회 - 기본 픽스처로 1건 이상")
  void getAllWaitingList_ok() {
    WaitingList list = dealService.getAllWaitingList(buyerId,
        "buyer_nick"); // setUp에서 만든 consumer 닉네임
    assertNotNull(list);
    assertNotNull(list.getDeals());
    assertFalse(list.getDeals().isEmpty());
  }

  /**
   * 구매중인 매물 목록 조회 - 정상 케이스
   *
   * <p>구매자 ID/닉네임으로 {@link DealService#getPurchaseWaitingList(String, String)} 호출 후,
   * 반환된 목록이 비어있지 않음을 검증한다.</p>
   */
  @Test
  @DisplayName("구매중인 매물 목록 조회 - consumer 닉네임 기준")
  void getPurchaseWaitingList_ok() {
    WaitingList list = dealService.getPurchaseWaitingList(buyerId, "buyer_nick");
    assertNotNull(list);
    assertNotNull(list.getDeals());
    assertFalse(list.getDeals().isEmpty());
  }

  /**
   * 판매중인 매물 목록 조회 - 정상 케이스
   *
   * <p>판매자 ID/닉네임으로 {@link DealService#getOnSaleWaitingList(String, String)} 호출 후,
   * 반환된 목록이 비어있지 않음을 검증한다.</p>
   */
  @Test
  @DisplayName("판매중인 매물 목록 조회 - seller 닉네임 기준")
  void getOnSaleWaitingList_ok() {
    WaitingList list = dealService.getOnSaleWaitingList(sellerId, "seller_nick");
    assertNotNull(list);
    assertNotNull(list.getDeals());
    assertFalse(list.getDeals().isEmpty());
  }

  /**
   * 거래 생성 - 정상 케이스
   *
   * <p>새 채팅방을 생성하여 {@link DealService#createDeal(String)} 호출 시
   * 유효한 dealId가 반환되는지 검증한다.</p>
   */
  @Test
  @DisplayName("거래 생성 - 새 채팅방으로 생성")
  void createDeal_ok() {
    // 별도의 채팅방/딜 없이 새로 생성
    String cr = insertChatRoom(buildingId, buyerId, complexId, "판매자", "구매자2");
    Long newDealId = dealService.createDeal(cr);
    assertNotNull(newDealId);
    assertTrue(newDealId > 0);
  }

  /**
   * 거래 삭제 - 존재/미존재 구분
   *
   * <p>기존 거래 ID 삭제 시 true, 존재하지 않는 ID 삭제 시 false가 반환되는지 검증한다.</p>
   */
  @Test
  @DisplayName("거래 삭제 - 존재/미존재 구분")
  void deleteDealById_ok() {
    assertTrue(dealService.deleteDealById(dealId));
    assertFalse(dealService.deleteDealById(-1L));
  }

  /**
   * 거래 상태 전이 - 정상 플로우
   *
   * <p>{@link bjs.zangbu.deal.vo.DealEnum} 순서대로
   * BEFORE_TRANSACTION → BEFORE_OWNER → BEFORE_CONSUMER → MIDDLE_DEAL → CLOSE_DEAL 전환이 가능함을
   * 검증한다.</p>
   */
  @Test
  @DisplayName("상태 전이 - 정상 플로우")
  void patchStatus_flow_ok() {
    // 픽스처 시작 상태가 BEFORE_TRANSACTION이라 가정
    jdbc.update("UPDATE deal SET status='BEFORE_TRANSACTION' WHERE deal_id=?", dealId);

    assertTrue(dealService.patchStatus(new Status(dealId, "BEFORE_OWNER")));
    assertTrue(dealService.patchStatus(new Status(dealId, "BEFORE_CONSUMER")));
    assertTrue(dealService.patchStatus(new Status(dealId, "MIDDLE_DEAL")));
    assertTrue(dealService.patchStatus(new Status(dealId, "CLOSE_DEAL")));

  }

  /**
   * 거래 상태 전이 - 역행/건너뛰기 금지
   *
   * <p>이전 상태로 돌아가거나, 순서를 건너뛰는 전환이 거부됨을 검증한다.</p>
   */
  @Test
  @DisplayName("상태 전이 - 역행/건너뛰기 금지")
  void patchStatus_invalid_fail() {
    jdbc.update("UPDATE deal SET status='BEFORE_OWNER' WHERE deal_id=?", dealId);

    assertFalse(dealService.patchStatus(new Status(dealId, "BEFORE_TRANSACTION"))); // 역행
    assertFalse(dealService.patchStatus(new Status(dealId, "MIDDLE_DEAL")));         // 건너뛰기
  }
}

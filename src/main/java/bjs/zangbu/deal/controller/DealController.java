package bjs.zangbu.deal.controller;


import bjs.zangbu.deal.dto.request.DealRequest.*;
import bjs.zangbu.deal.dto.response.DealResponse.*;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.DocumentType;


import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/deal")
public class DealController {

  // private final DealService dealService;  // TODO: 구현 후 주입

  /* -------------------------------------------------
   * 1. 거래 안내/대기 목록
   * ------------------------------------------------- */

  /**
   * 1) 거래 전 안내 페이지 이동
   */
  @GetMapping("/notice/{buildingId}")
  public ResponseEntity<Void> moveNoticePage(@PathVariable Long buildingId) {
    // TODO: view 이름 반환 or redirect 처리
    return ResponseEntity.ok().build();
  }

  /**
   * 2) 거래 중인 매물(전체)
   */
  @GetMapping("/waitinglist")
  public ResponseEntity<List<WaitingList>> getAllWaitingList() {
    // TODO: DealSummaryDTO 생성 및 로직 처리
    return ResponseEntity.ok(List.of());
  }

  /**
   * 3) 거래 중인 매물 - 구매중
   */
  @GetMapping("/waitinglist/purchase")
  public ResponseEntity<List<WaitingListPurchase>> getPurchaseWaitingList() {
    return ResponseEntity.ok(List.of());
  }

  /**
   * 4) 거래 중인 매물 - 판매 중
   */
  @GetMapping("/waitinglist/onsale")
  public ResponseEntity<List<WaitingListOnSale>> getOnSaleWaitingList() {
    return ResponseEntity.ok(List.of());
  }

  /**
   * 5) 거래 취소
   */
  @DeleteMapping("/remove/{dealId}")
  public ResponseEntity<Void> removeDeal(@PathVariable Long dealId) {
    return ResponseEntity.noContent().build();
  }

  /* -------------------------------------------------
   * 2. 구매자 전용 (documents / membership / report …)
   * ------------------------------------------------- */

  /**
   * 6) 거래 중인 매물(전체)
   */
  @GetMapping("/consumer/documents/{dealId}/{type}/download")
  public ResponseEntity<?> downloadDocument(@PathVariable Long dealId,
      @PathVariable DocumentType type) {
    //Todo: DocumentType -> enum 처리 및 타입 지정, 로직 처리 해야함
    return ResponseEntity.ok().build();
  }

  /**
   * 7) 분석 리포트 주문-> 결제 페이지 이동
   */
  @PostMapping("/consumer/membership")
  public ResponseEntity<?> orderAnalysisReport(@RequestBody IntentRequest dto) {
    return ResponseEntity.ok().build();
  }

  /**
   * 8) 분석 리포트 다운로드
   */
  @GetMapping("/consumer/report/{reportId}/download")
  public ResponseEntity<?> downloadAnalysisReport(@PathVariable Long reportId) {
    return ResponseEntity.ok().build();
  }

  /**
   * 9) 표준 계약서 다운로드
   */
  @GetMapping("/consumer/contract/download")
  public ResponseEntity<?> donwloadContractReport() {
    return ResponseEntity.ok().build();
  }

  /**
   * 10) 거래 시작(의향서 제출)
   */
  @PostMapping("/consumer/intent")
  public ResponseEntity<Void> startDeal(@RequestBody IntentRequest dto) {
    return ResponseEntity.ok().build();
  }

  /**
   * 11) 결제 완료 후 리포트 페이지 이동
   */
  @PostMapping("/consumer/report/{reportId}")
  public ResponseEntity<Void> moveReportPage(@PathVariable Long reportId) {
    return ResponseEntity.ok().build();
  }

  /* -------------------------------------------------
   * 3. 상태 변경
   * ------------------------------------------------- */

  /**
   * 12) 거래 상태 변경
   */
  @PatchMapping("/status")
  public ResponseEntity<Void> changeDealStatus(@RequestBody Status dto) {
    return ResponseEntity.noContent().build();
  }


}

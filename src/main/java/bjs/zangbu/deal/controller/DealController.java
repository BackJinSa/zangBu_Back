package bjs.zangbu.deal.controller;


import bjs.zangbu.deal.dto.request.DealRequest.IntentRequest;
import bjs.zangbu.deal.dto.request.DealRequest.Status;
import bjs.zangbu.deal.dto.response.DealResponse.Notice;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingList;
import bjs.zangbu.deal.service.DealService;
import bjs.zangbu.member.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.DocumentType;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/deal")
public class DealController {

  private final DealService dealService;
  private final UserService userService;

  /* -------------------------------------------------
   * 1. 거래 안내/대기 목록
   * ------------------------------------------------- */

  /**
   * 1) 거래 전 안내 페이지 이동
   */
  @GetMapping("/notice/{buildingId}")
  public ResponseEntity<?> moveNoticePage(@PathVariable Long buildingId) {
    try {
      Notice response = dealService.getNotice(buildingId);
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("안내 페이지 요청에 실패했습니다.");
    }
  }

  /**
   * 2) 거래 중인 매물(전체)
   */
  @GetMapping("/waitinglist")
  public ResponseEntity<?> getAllWaitingList(
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    try {
      String userId = userDetails.getUsername();
      String nickname = userService.getNickname(userId); // 닉네임 추출

      // Response 생성
      WaitingList response = dealService.getAllWaitingList(userId, nickname);

      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전체 매물 리스트 불러오는데 실패했습니다.");

    }
  }

  /**
   * 3) 거래 중인 매물 - 구매중
   */
  @GetMapping("/waitinglist/purchase")
  public ResponseEntity<?> getPurchaseWaitingList(
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    try {
      String userId = userDetails.getUsername();
      String nickname = userService.getNickname(userId); // 닉네임 추출

      WaitingList response = dealService.getPurchaseWaitingList(userId, nickname);
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("구매 중인 리스트 불러오는데 실패했습니다.");

    }
  }

  /**
   * 4) 거래 중인 매물 - 판매 중
   */
  @GetMapping("/waitinglist/onsale")
  public ResponseEntity<?> getOnSaleWaitingList(
      @AuthenticationPrincipal UserDetails userDetails
  ) {
    try {
      String userId = userDetails.getUsername();
      String nickname = userService.getNickname(userId); // 닉네임 추출

      WaitingList response = dealService.getOnSaleWaitingList(userId, nickname);
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("판매 중인 리스트 불러오는데 실패했습니다.");

    }
  }

  /**
   * 5) 거래 취소
   */
  @DeleteMapping("/remove/{dealId}")
  public ResponseEntity<?> removeDeal(@PathVariable Long dealId) {
    try {

      if (dealService.deleteDealById(dealId)) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("거래 취소에 성공했습니다.");
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("거래 취소에 실패했습니다.");
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("예기치 못한 문제가 발생했습니다.");
    }
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
  public ResponseEntity<?> startDeal(@RequestBody IntentRequest dto) {
    return ResponseEntity.ok().build();
  }

  /**
   * 11) 결제 완료 후 리포트 페이지 이동
   */
  @PostMapping("/consumer/report/{reportId}")
  public ResponseEntity<?> moveReportPage(@PathVariable Long reportId) {
    return ResponseEntity.ok().build();
  }

  /* -------------------------------------------------
   * 3. 상태 변경
   * ------------------------------------------------- */

  /**
   * 12) 거래 상태 변경
   */
  @PatchMapping("/status")
  public ResponseEntity<?> changeDealStatus(@RequestBody Status dto) {
    try {
      // 상태 patch
      if (dealService.patchStatus(dto)) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("상태변경에 성공했습니다.");
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상태변경에 실패했습니다.");
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("예기치 못한 문제가 발생했습니다.");
    }

  }


}

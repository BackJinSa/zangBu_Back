package bjs.zangbu.deal.controller;


import bjs.zangbu.deal.dto.request.DealRequest.IntentRequest;
import bjs.zangbu.deal.dto.request.DealRequest.Status;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.dto.response.DealResponse;
import bjs.zangbu.deal.dto.response.DealResponse.Notice;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingList;
import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import bjs.zangbu.deal.service.ContractService;
import bjs.zangbu.deal.service.DealService;
import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.documentReport.dto.response.DocumentReportResponse.DocumentReportElement;
import bjs.zangbu.documentReport.service.DocumentReportService;
import bjs.zangbu.member.service.MemberService;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/deal")
@Tag(name = "Deal API", description = "거래 관련 기능 API")
public class DealController {

  private final DealService dealService;
  private final MemberService memberService;
  private final ContractService contractService;
  private final DocumentReportService documentReportService;

  /* -------------------------------------------------
   * 1. 거래 안내/대기 목록
   * ------------------------------------------------- */

  /**
   * 거래 전 안내 페이지 이동
   *
   * @param buildingId 건물 ID
   * @return 거래 안내 정보
   */
  @Operation(
      summary = "거래 안내 페이지 이동",
      description = "거래 전 건물 ID를 기반으로 안내 정보를 반환합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "안내 정보 반환 성공", content = @Content(schema = @Schema(implementation = DealResponse.Notice.class))),
      @ApiResponse(responseCode = "400", description = "안내 정보 요청 실패")
  })
  @GetMapping("/notice/{buildingId}")
  public ResponseEntity<?> moveNoticePage(
      @Parameter(description = "건물 ID", example = "123")
      @PathVariable Long buildingId) {
    try {
      Notice response = dealService.getNotice(buildingId);
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("안내 페이지 요청에 실패했습니다.");
    }
  }

  /**
   * 사용자의 전체 거래 대기 매물 조회
   *
   * @param userDetails 로그인 사용자 정보
   * @param page        요청 페이지 (1부터 시작)
   * @param size        페이지당 항목 수
   * @return 전체 거래 대기 매물 목록
   */
  @Operation(summary = "전체 거래 대기 매물 조회", description = "사용자의 전체 거래 대기 매물을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "전체 대기 매물 조회 성공", content = @Content(schema = @Schema(implementation = WaitingList.class))),
      @ApiResponse(responseCode = "400", description = "전체 대기 매물 조회 실패")
  })
  @GetMapping("/waitinglist")
  public ResponseEntity<?> getAllWaitingList(
      @Parameter(hidden = true)
      @AuthenticationPrincipal UserDetails userDetails,
      @Parameter(description = "페이지 번호", example = "1")
      @RequestParam(defaultValue = "1") int page,         // 요청 페이지 (1부터 시작)
      @Parameter(description = "페이지당 항목 수", example = "10")
      @RequestParam(defaultValue = "10") int size         // 페이지당 항목 수
  ) {
    try {
      String memberId = userDetails.getUsername();
      String nickname = memberService.getNickname(memberId); // 닉네임 추출

      // PageHelper 페이지네이션 시작
      PageHelper.startPage(page, size);
      // Response 생성
      // 내부적으로 LIMIT OFFSET 쿼리로 변환되어서 페이지네이션 됨
      WaitingList response = dealService.getAllWaitingList(memberId, nickname);

      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전체 매물 리스트 불러오는데 실패했습니다.");

    }
  }

  /**
   * 사용자의 구매 진행 중인 매물 목록 조회
   *
   * @param userDetails 로그인 사용자 정보
   * @param page        요청 페이지 (1부터 시작)
   * @param size        페이지당 항목 수
   * @return 구매 진행 중인 거래 대기 매물 목록
   */
  @Operation(
      summary = "구매 중인 대기 매물 조회",
      description = "사용자의 구매 진행 중인 거래 대기 매물 목록을 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "구매 대기 매물 조회 성공", content = @Content(schema = @Schema(implementation = WaitingList.class))),
      @ApiResponse(responseCode = "400", description = "구매 대기 매물 조회 실패")
  })
  @GetMapping("/waitinglist/purchase")
  public ResponseEntity<?> getPurchaseWaitingList(
      @Parameter(hidden = true)
      @AuthenticationPrincipal UserDetails userDetails,
      @Parameter(description = "페이지 번호", example = "1")
      @RequestParam(defaultValue = "1") int page,         // 요청 페이지 (1부터 시작)
      @Parameter(description = "페이지당 항목 수", example = "10")
      @RequestParam(defaultValue = "10") int size         // 페이지당 항목 수
  ) {
    try {

      String userId = userDetails.getUsername();
      String nickname = memberService.getNickname(userId); // 닉네임 추출

      // PageHelper 페이지네이션 시작
      PageHelper.startPage(page, size);
      WaitingList response = dealService.getPurchaseWaitingList(userId, nickname);
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("구매 중인 리스트 불러오는데 실패했습니다.");

    }
  }

  /**
   * 사용자의 판매 중인 거래 대기 매물 목록 조회
   *
   * @param userDetails 로그인 사용자 정보
   * @param page        요청 페이지 번호 (1부터 시작)
   * @param size        페이지당 항목 수
   * @return 판매 중인 거래 대기 매물 목록
   */
  @Operation(
      summary = "판매 중인 대기 매물 조회",
      description = "사용자의 판매 진행 중인 거래 대기 매물 목록을 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "판매 대기 매물 조회 성공", content = @Content(schema = @Schema(implementation = WaitingList.class))),
      @ApiResponse(responseCode = "400", description = "판매 대기 매물 조회 실패")
  })
  @GetMapping("/waitinglist/onsale")
  public ResponseEntity<?> getOnSaleWaitingList(
      @Parameter(hidden = true)
      @AuthenticationPrincipal UserDetails userDetails,
      @Parameter(description = "페이지 번호", example = "1")
      @RequestParam(defaultValue = "1") int page,         // 요청 페이지 (1부터 시작)
      @Parameter(description = "페이지당 항목 수", example = "10")
      @RequestParam(defaultValue = "10") int size         // 페이지당 항목 수
  ) {
    try {
      String userId = userDetails.getUsername();
      String nickname = memberService.getNickname(userId); // 닉네임 추출

      // PageHelper 페이지네이션 시작
      PageHelper.startPage(page, size);
      WaitingList response = dealService.getOnSaleWaitingList(userId, nickname);
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("판매 중인 리스트 불러오는데 실패했습니다.");

    }
  }

  /**
   * 거래 취소 (삭제)
   *
   * @param dealId 거래 ID
   * @return 삭제 처리 결과 메시지
   */
  @Operation(
      summary = "거래 취소",
      description = "거래 ID를 기반으로 해당 거래를 삭제(취소)합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "거래 취소 성공"),
      @ApiResponse(responseCode = "400", description = "거래 취소 실패"),
      @ApiResponse(responseCode = "404", description = "예기치 못한 서버 오류")
  })
  @Parameter(description = "거래 ID", example = "123")
  @DeleteMapping("/remove/{dealId}")
  public ResponseEntity<?> removeDeal(
      @Parameter(description = "거래 ID", example = "123")
      @PathVariable Long dealId) {
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
   * 등기/건축 서류 다운로드
   *
   * @param dealId 거래 ID
   * @param type   문서 종류 (예: BUILDING_REGISTER, ARCHITECTURE 등)
   * @return PDF 다운로드 링크
   */
  @Operation(
      summary = "등기/건축 서류 다운로드",
      description = "거래 ID와 문서 유형을 기반으로 등기 또는 건축 서류 PDF 파일을 다운로드합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "문서 다운로드 성공"),
      @ApiResponse(responseCode = "", description = "문서 다운로드 실패")
  })
  @GetMapping("/consumer/documents/{dealId}/{type}/download")
  public ResponseEntity<?> downloadDocument(@PathVariable Long dealId,
      @PathVariable DocumentType type) throws Exception {
    //todo: 중복 로직 정리 해야 함 , 그 후 스웨거 적용
    if (type == DocumentType.ESTATE) {
      EstateRegistrationResponse rsp = contractService.getEstateRegistrationPdf(dealId);
      return ResponseEntity.ok(new DealResponse.Download(rsp.getResOriginalData()));
    } else if (type == DocumentType.BUILDING_REGISTER) {
      BuildingRegisterResponse rsp = contractService.generateRegisterPdf(dealId);
      return ResponseEntity.ok(new DealResponse.Download(rsp.getResOriginalData()));
    }
    return null;
  }

  /**
   * 분석 리포트 주문 → 결제 페이지 이동
   *
   * @param dto 결제 의도 요청 DTO
   * @return 상태 200 OK
   */
  @Operation(
      summary = "분석 리포트 결제 요청",
      description = "분석 리포트 주문 후 결제 페이지로 이동합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "결제 페이지 이동 성공")
  })
  @PostMapping("/consumer/membership")
  public ResponseEntity<?> orderAnalysisReport(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "결제 의도 정보",
          required = true,
          content = @Content(schema = @Schema(implementation = IntentRequest.class))
      )
      @RequestBody IntentRequest dto
  ) {
    return ResponseEntity.ok().build();
  }

  /**
   * 분석 리포트 다운로드
   *
   * @param reportId 리포트 ID
   * @return PDF 파일 또는 다운로드 링크
   */
  @Operation(
      summary = "분석 리포트 다운로드",
      description = "리포트 ID를 통해 분석 리포트 PDF를 다운로드합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "리포트 다운로드 성공")
  })
  @GetMapping("/consumer/report/{reportId}/download")
  public ResponseEntity<?> downloadAnalysisReport(
      @Parameter(description = "거래 ID", example = "1001")
      @PathVariable Long reportId
  ) {
    return ResponseEntity.ok().build();
  }

  /**
   * 표준 계약서 다운로드
   *
   * @param dealId 거래 ID
   * @return 계약서 다운로드 링크
   */
  @Operation(
      summary = "표준 계약서 다운로드",
      description = "거래 ID를 기반으로 표준 계약서 PDF를 다운로드합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "계약서 다운로드 성공")
  })
  @GetMapping("/consumer/contract/{dealId}/download")
  public ResponseEntity<?> downloadContractReport(
      @Parameter(description = "거래 ID", example = "1001")
      @PathVariable Long dealId
  ) {
    // 1. 상대 경로
    String relativePath = contractService.getContractPdf(dealId);

    //2. 절대 URL(Host·Port 포함) 생성
    String absolutePath = ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path(relativePath)
        .toUriString();

    return ResponseEntity.ok(new DealResponse.Download(absolutePath));
  }

  /**
   * 결제 완료 후 리포트 페이지 이동
   *
   * @param reportId 리포트 ID
   * @return 리포트 조회 페이지로 이동
   */
  @Operation(
      summary = "결제 완료 후 리포트 페이지 이동",
      description = "리포트 결제가 완료된 후 해당 리포트 페이지로 이동합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "리포트 페이지 이동 성공")
  })
  @PostMapping("/consumer/report/{reportId}")
  public ResponseEntity<?> moveReportPage(
      @Parameter(description = "리포트 ID", example = "600")
      @PathVariable Long reportId
  ) {
    return ResponseEntity.ok().build();
  }

  /* -------------------------------------------------
   * 3. 상태 변경
   * ------------------------------------------------- */

  /**
   * 거래 상태 변경
   *
   * @param dto 거래 상태 변경 요청 DTO
   * @return 변경 결과 메시지
   */
  @Operation(
      summary = "거래 상태 변경",
      description = "거래 상태(예: WAITING → COMPLETED 등)를 변경합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
      @ApiResponse(responseCode = "400", description = "상태 변경 실패"),
      @ApiResponse(responseCode = "404", description = "예기치 못한 오류 발생")
  })
  @PatchMapping("/status")
  public ResponseEntity<?> changeDealStatus(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "거래 상태 변경 요청 DTO",
          required = true,
          content = @Content(schema = @Schema(implementation = Status.class))
      )
      @RequestBody Status dto
  ) {
    try {
      // 상태 patch
      if (dealService.patchStatus(dto)) {
        return ResponseEntity.status(HttpStatus.OK).body("상태변경에 성공했습니다.");
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상태변경에 실패했습니다.");
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("예기치 못한 문제가 발생했습니다.");
    }

  }

  /**
   * 분석 리포트 상세 조회
   *
   * @param reportId 리포트 ID
   * @return 리포트 상세 정보
   */
  @Operation(
      summary = "분석 리포트 상세 조회",
      description = "리포트 ID를 기반으로 리포트 상세 정보를 반환합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "리포트 조회 성공"),
      @ApiResponse(responseCode = "400", description = "리포트 요청 실패"),
      @ApiResponse(responseCode = "404", description = "리포트를 찾을 수 없음")
  })
  @GetMapping("/consumer/report/{reportId}")
  public ResponseEntity<?> getReport(
      @Parameter(description = "리포트 ID", example = "600")
      @PathVariable Long reportId
  ) {
    try {
      DocumentReportElement response = documentReportService.getDocumentReportByReportId(reportId);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (NullPointerException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리포트 요청에 실패했습니다.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("리포트를 찾을 수 없습니다.");
    }
  }

}

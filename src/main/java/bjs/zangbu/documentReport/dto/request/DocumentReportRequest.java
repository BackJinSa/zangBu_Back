package bjs.zangbu.documentReport.dto.request;

import bjs.zangbu.documentReport.vo.DocumentReport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 분석 리포트 저장 요청 DTO
 */
public class DocumentReportRequest {


  /**
   * 분석 리포트 저장 요청 단건 DTO
   */
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(name = "DocumentReportRequestElement", description = "분석 리포트 단건 저장 요청 DTO")
  public static class DocumentReportRequestElement {

    /**
     * 분석 리포트 ID (수정 시 사용)
     */
    @Schema(description = "분석 리포트 ID", example = "1")
    Long reportId;

    /**
     * 매물 ID
     */
    @Schema(description = "매물 ID", example = "101")
    Long buildingId;

    /**
     * 단지 ID
     */
    @Schema(description = "단지 ID", example = "201")
    Long complexId;

    /**
     * 부동산 고유 식별 번호
     */
    @Schema(description = "부동단 고유 식별 번호", example = "A123456789")
    String commUniqueNo;

    /**
     * 거래 금액 (단위: 만원)
     */
    @Schema(description = "거래 금액(만원)", example = "50000")
    int dealAmount;

    /**
     * 보증금 (단위: 만원)
     */
    @Schema(description = "보증 금액(만원)", example = "10000")
    int deposit;

    /**
     * 월세 (단위: 만원)
     */
    @Schema(description = "월세 금액(만원)", example = "50")
    int monthlyRent;

    /**
     * 선순위 채권액 (단위: 만원)
     */
    @Schema(description = "선순위 채권액", example = "20000")
    int priorityDept;

    /**
     * 내 보증금 (단위: 만원)
     */
    @Schema(description = "내 보증금", example = "10000")
    int depositPrice;

    /**
     * 최종 낙찰가 (단위: 만원)
     */
    @Schema(description = "최종 낙찰가", example = "60000")
    int finalAuctionPrice;

    /**
     * 여유 보증금 (단위: 만원)
     */
    @Schema(description = "여유 보증금", example = "8000")
    int remainingDeposit;

    /**
     * 소유자 주민번호 (RSA 암호화)
     */
    @Schema(description = "소유자 주민번호", example = "RSA(1234567)")
    String resUserNum;

    /**
     * 신탁 여부
     */
    @Schema(description = "신탁 여부", example = "false")
    boolean isTrustee;

    /**
     * 신탁 내용 설명
     */
    @Schema(description = "신탁 내용 텍스트", example = "신탁 미설정")
    String trustType;

    /**
     * 리포트 생성 일시 (yyyy-MM-dd'T'HH:mm:ss)
     */
    @Schema(description = "보고서 생성 시간", example = "2025-07-29T14:30:00")
    LocalDateTime createdAt;

    /**
     * DocumentReport VO → Request DTO 변환
     */
    public static DocumentReportRequestElement toDto(DocumentReport vo) {
      return new DocumentReportRequestElement(
          vo.getReportId(),
          vo.getBuildingId(),
          vo.getComplexId(),
          vo.getCommUniqueNo(),
          vo.getDealAmount(),
          vo.getDeposit(),
          vo.getMonthlyRent(),
          vo.getPriorityDept(),
          vo.getDepositPrice(),
          vo.getFinalAuctionPrice(),
          vo.getRemainingDeposit(),
          vo.getResUserNum(),
          vo.isTrustee(),
          vo.getTrustType(),
          vo.getCreatedAt()
      );
    }

    /**
     * Request DTO → DocumentReport VO 변환
     *
     * @param memberId 사용자 ID
     * @param request  요청 DTO
     * @return DocumentReport VO
     */
    public static DocumentReport toVo(String memberId, DocumentReportRequestElement request) {
      return new DocumentReport(
          null,
          request.getCommUniqueNo(),
          request.getDealAmount(),
          request.getDeposit(),
          request.getMonthlyRent(),
          request.getPriorityDept(),
          request.getDepositPrice(),
          request.getFinalAuctionPrice(),
          request.getRemainingDeposit(),
          request.getResUserNum(),
          request.isTrustee(),
          request.getTrustType(),
          request.getCreatedAt(),

          request.getBuildingId(),
          memberId,
          request.getComplexId()
      );
    }
  }

}

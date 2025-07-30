package bjs.zangbu.documentReport.dto.response;

import bjs.zangbu.documentReport.vo.DocumentReport;
import bjs.zangbu.global.formatter.LocalDateFormatter.CreatedAt;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 분석 리포트 관련 응답 DTO
 */
public class DocumentReportResponse {

  //  /deal/consumer/report/{reportId} Response

  /**
   * 여러 개의 분석 리포트를 리스트로 반환하는 DTO
   */
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(name = "DocumentReportList", description = "분석 리포트 목록 응답 DTO")
  public static class DocumentReportList {

    @Schema(description = "분석 리포트 목록")
    List<DocumentReport> documentReport;
  }

  //  /deal/consumer/report/{reportId} Response Element

  /**
   * 단일 분석 리포트의 상세 정보를 담는 DTO
   */
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(name = "DocumentReportElement", description = "분석 리포트 단건 상세 응답 DTO")
  public static class DocumentReportElement {

    /**
     * 분석 리포트 ID
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
     * 부동산 고유 식별 번호 (commUniqueNo)
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
     * 월세 금액 (단위: 만원)
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
     * 소유자 주민번호 (RSA 암호화된 값)
     */
    @Schema(description = "소유자 주민번호", example = "RSA(1234567)")
    String resUserNum;

    /**
     * 신탁 여부
     */
    @Schema(description = "신탁 여부", example = "false")
    boolean isTrustee;

    /**
     * 신탁 유형 설명
     */
    @Schema(description = "신탁 유형 설명", example = "신탁 미설정")
    String trustType;

    /**
     * 보고서 생성일 (형식: yyyy/MM/dd)
     */
    @Schema(description = "보고서 생성일", example = "2025/07/29")
    String createdAt;


    /**
     * DocumentReport VO 객체를 DocumentReportElement DTO로 변환
     *
     * @param vo DocumentReport VO 객체
     * @return DocumentReportElement DTO 객체
     */
    public static DocumentReportElement toDto(DocumentReport vo) {
      return new DocumentReportElement(
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
          CreatedAt.formattingCreatedAt(vo.getCreatedAt())
      );
    }
  }
}

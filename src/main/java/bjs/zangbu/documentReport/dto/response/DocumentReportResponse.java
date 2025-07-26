package bjs.zangbu.documentReport.dto.response;

import bjs.zangbu.documentReport.vo.DocumentReport;
import bjs.zangbu.global.formatter.LocalDateFormatter.CreatedAt;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DocumentReportResponse {

  //  /deal/consumer/report/{reportId} Response
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DocumentReportList {

    List<DocumentReport> documentReport; // DocumentReportElement 를 담고 있는 list
  }

  //  /deal/consumer/report/{reportId} Response Element
  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DocumentReportElement {

    Long reportId;         // 분석 리포트 식별 id
    Long buildingId;       // 매물 식별 id
    Long complexId;        // 단지 식별 id
    String commUniqueNo;   // 부동단 고유 식별 번호
    int dealAmount;        // 거래 금액(만원)
    int deposit;           // 보증 금액(만원)
    int monthlyRent;       // 월세 금액(만원)
    int priorityDept;      // 선순위 채권액
    int depositPrice;      // 내 보증금
    int finalAuctionPrice; // 최종 낙찰가
    int remainingDeposit;  // 여유 보증금
    String resUserNum;     // 소유자 정보
    boolean isTrustee;     // 신탁 여부
    String trustType;      // 신탁 내용 텍스트
    String createdAt;      // 보고서 생성 시간

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

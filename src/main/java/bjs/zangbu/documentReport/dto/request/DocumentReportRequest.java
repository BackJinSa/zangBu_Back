package bjs.zangbu.documentReport.dto.request;

import bjs.zangbu.documentReport.vo.DocumentReport;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DocumentReportRequest {


  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DocumentReportRequestElement {

    Long reportId;           // 분석 리포트 식별 id
    Long buildingId;         // 매물 식별 id
    Long complexId;          // 단지 식별 id
    String commUniqueNo;     // 부동단 고유 식별 번호
    int dealAmount;          // 거래 금액(만원)
    int deposit;             // 보증 금액(만원)
    int monthlyRent;         // 월세 금액(만원)
    int priorityDept;        // 선순위 채권액
    int depositPrice;        // 내 보증금
    int finalAuctionPrice;   // 최종 낙찰가
    int remainingDeposit;    // 여유 보증금
    String resUserNum;       // 소유자 정보
    boolean isTrustee;       // 신탁 여부
    String trustType;        // 신탁 내용 텍스트
    LocalDateTime createdAt; // 보고서 생성 시간

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

package bjs.zangbu.documentReport.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentReport {

  Long reportId;           // 분석 리포트 식별 id
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

  // foreign key
  Long buildingId;         // 매물 식별 id
  String memberId;         // 유저 식별 id
  Long complexId;            // 단지 식별 id
}

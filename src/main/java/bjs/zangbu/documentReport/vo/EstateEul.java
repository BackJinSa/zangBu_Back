// EstateEncumbrance.java
package bjs.zangbu.documentReport.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EstateEul {
    private Long id;
    private Long buildingId;
    private String rankNo;        // 순위번호(원문)
    private Integer rankOrder;    // 정렬용(숫자)
    private String purpose;       // 근저당권설정/전세권 등
    private String receipt;       // 접수
    private String cause;         // 원인
    private String creditor;      // 채권자
    private String debtor;        // 채무자
    private Long maxClaimAmount;  // 채권최고액(원)
    private String remarksRaw;    // 비고(요약)
    private Boolean buildingOnly; // 건물만 여부(개별)
    private LocalDateTime createdAt;
}
package bjs.zangbu.documentReport.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 갑구(소유권 변동) 1:N
 * - 프롬프트 source.registry.ownership_history[]로 매핑
 */
@Data
public class EstateGap {
    private Long id;
    private Long buildingId;
    private String rankNo;         // 순위번호(원문)
    private Integer rankOrder;     // 정렬용(숫자), null이면 후순위
    private String purpose;        // 소유권보존/소유권이전 등
    private String receipt;        // 접수(날짜/번호)
    private String cause;          // 원인(매매 등)
    private String holderName;     // 등기명의인
    private String holderRegnoMasked;
    private String holderAddress;
    private Long priceIfAny;       // 거래가액(원, 없으면 null)
    private String notesRaw;       // 비고(요약)
    private LocalDateTime createdAt;
}
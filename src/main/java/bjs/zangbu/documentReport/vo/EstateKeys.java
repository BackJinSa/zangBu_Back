package bjs.zangbu.documentReport.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * 등기부 핵심 요약(빌딩 기준 1행)
 * - 프롬프트 source.registry.property/summary 및 선순위 계산용 힌트로 사용
 */
@Data
public class EstateKeys {
    private Long buildingId;
    private LocalDate publishDate;        // 등기부 발급일
    private String office;               // 관할 등기소
    private String formattedAddress;     // 주소 요약
    private String unit;                 // 전유부분 호수
    private BigDecimal exclusiveAreaM2;  // 전유면적(㎡)
    private String landShareRatio;       // 대지권비율
    private String landRightType;        // 대지권종류
    private String ownerNameCurrent;     // 현재 소유자
    private String commUniqueNo;         // 고유번호(선택)
    private Long firstMaxClaim;          // 선순위 채권최고액(원, 없으면 null)
    private Boolean buildingOnlyOverall; // 건물만 여부(전체 판단)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
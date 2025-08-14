package bjs.zangbu.documentReport.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 건축물대장 핵심 요약(빌딩 기준 1행)
 * - 프롬프트 source.building_register.*로 매핑
 */
@Data
public class BuildingRegisterKeys {
    private Long buildingId;
    private LocalDate issueDate;             // 대장 발급일
    private String issuer;                   // 발급기관
    private String addressLot;               // 지번 주소
    private String addressRoad;              // 도로명 주소
    private String complexName;              // 단지명
    private String useMain;                  // 주용도
    private LocalDate useApprovalDate;       // 사용승인일
    private BigDecimal siteAreaM2;           // 대지면적(㎡)
    private BigDecimal totalFloorAreaM2;     // 연면적(㎡)
    private BigDecimal buildingCoverageRatio;// 건폐율(%)
    private BigDecimal floorAreaRatio;       // 용적률(%)
    private String householdSummary;         // 세대요약
    private String seismicApplied;           // 내진설계 적용여부
    private String seismicCapacity;          // 내진능력
    private String violationStatus;          // 위반건축물 상태(빈문자면 없음)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

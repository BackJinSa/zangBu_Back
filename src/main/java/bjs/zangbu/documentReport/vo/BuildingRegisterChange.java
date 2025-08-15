package bjs.zangbu.documentReport.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 건축물대장 변경 이력 1:N
 * - 프롬프트 source.building_register.changes[]로 매핑
 */
@Data
public class BuildingRegisterChange {
    private Long id;
    private Long buildingId;
    private LocalDate changeDate;
    private String changeReason;
    private LocalDateTime createdAt;
}

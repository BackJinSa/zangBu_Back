package bjs.zangbu.deal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class BuildingRegisterResponse {
    private String resOriginalData;
    private String resViolationStatus;
}

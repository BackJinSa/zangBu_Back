package bjs.zangbu.publicdata.dto.managecost.publicuse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EtcCost {
    private String kaptCode;
    private String kaptName;
    private Long careItemCost;
    private Long accountingCost;
    private Long hiddenCost;
}

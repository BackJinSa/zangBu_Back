package bjs.zangbu.publicdata.dto.managecost.publicuse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleCost {
    private String kaptCode;
    private String kaptName;
    private Long fuelCost;
    private Long refairCost;
    private Long carInsurance;
    private Long carEtc;
}

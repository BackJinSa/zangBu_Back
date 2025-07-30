package bjs.zangbu.publicdata.dto.managecost.privateuse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeatCost {
    private String kaptCode;
    private String kaptName;
    private Long heatC;   // 개별세대 난방비 총합
    private Long heatP;   // 지역난방 총 구매비용
}

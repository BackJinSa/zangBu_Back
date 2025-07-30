package bjs.zangbu.publicdata.dto.managecost.privateuse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GasCost {
    private String kaptCode;
    private String kaptName;
    private Long gasC;  // 세대별 가스 사용요금 총합
    private Long gasP;  // 전체 가스 공급사 납부 총비용
}

package bjs.zangbu.publicdata.dto.trade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HousingTrade {
    private int buildYear;
    private int dealYear;
    private int dealMonth;
    private int dealDay;
    private String dealAmount;
    private String houseType;
    private double plottageAr;
    private double totalFloorAr;
    private String jibun;
    private String umdNm;
    private int sggCd;
}

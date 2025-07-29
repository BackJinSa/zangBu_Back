package bjs.zangbu.publicdata.dto.trade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HousingRent {
    private Integer buildYear;     // 준공년도(누락 시 null)
    private Integer dealYear;      // 계약 연도
    private Integer dealMonth;     // 계약 월
    private Integer dealDay;       // 계약 일
    private String deposit;        // 보증금
    private Integer monthlyRent;   // 월세
    private String houseType;      // 주택 유형
    private Double totalFloorAr;   // 건물 연면적(㎡)
    private String umdNm;          // 법정동명
    private Integer sggCd;         // 시군구 코드
}

package bjs.zangbu.publicdata.dto.trade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfficeRent {
    private Integer buildYear;     // 준공년도
    private Integer dealYear;      // 계약 연도
    private Integer dealMonth;     // 계약 월
    private Integer dealDay;       // 계약 일
    private String deposit;        // 보증금 (만원, 문자열)
    private Integer monthlyRent;   // 월세 (0이면 전세)
    private Double excluUseAr;     // 전용면적 (㎡)
    private Integer floor;         // 층수
    private String offiNm;         // 오피스텔 이름
    private String jibun;          // 지번 (문자열)
    private String umdNm;          // 법정동명
    private Integer sggCd;         // 시군구 코드
    private String sggNm;          // 시군구 이름
}

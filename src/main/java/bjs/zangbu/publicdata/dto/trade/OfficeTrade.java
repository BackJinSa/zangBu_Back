package bjs.zangbu.publicdata.dto.trade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfficeTrade {
    private Integer buildYear;     // 준공년도
    private Integer dealYear;      // 거래 연도
    private Integer dealMonth;     // 거래 월
    private Integer dealDay;       // 거래 일
    private String dealAmount;     // 거래금액 (만원, 문자열)
    private Double excluUseAr;     // 전용면적 (㎡)
    private Integer floor;         // 층수
    private String jibun;          // 지번 주소
    private String offiNm;         // 오피스텔 이름
    private String umdNm;          // 법정동명
    private Integer sggCd;         // 시군구 코드
    private String sggNm;          // 시군구 이름
}

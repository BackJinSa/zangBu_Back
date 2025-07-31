package bjs.zangbu.publicdata.dto.aptinfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 기본정보 조회(getAptInfo) API 로부터 받아올
 * 공동주택 단지 식별 기본정보 DTO
 */

@Getter
@Setter
public class AptInfo {
    /** 단지 주소 */
    private String adres;
    /** 단지 종류 코드 (1: 아파트 등) */
    private String complexGbCd;
    /** 단지명 (표준) */
    private String complexNm1;
    /** 단지명 (건축물대장용) */
    private String complexNm2;
    /** 단지명 (도로명주소용) */
    private String complexNm3;
    /** 단지고유번호 (이 값을 이용해 동정보 조회) */
    private String complexPk;
    /** 동 수 */
    private int dongCnt;
    /** 필지고유번호 */
    private String pnu;
    /** 세대 수 */
    private int unitCnt;
    /** 사용승인일 (yyyyMMdd) */
    private String useaprDt;
}

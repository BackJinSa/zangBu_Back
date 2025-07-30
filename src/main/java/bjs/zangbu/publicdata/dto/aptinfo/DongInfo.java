package bjs.zangbu.publicdata.dto.aptinfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 동정보 조회(getDongInfo) API 로부터 받아올
 * 동별 상세정보 DTO
 */

@Getter
@Setter
public class DongInfo {
    /** 단지고유번호 (조회 키) */
    private String complexPk;
    /** 동명 (공시지가 기준) */
    private String dongNm1;
    /** 동명 (건축물대장용) */
    private String dongNm2;
    /** 동명 (도로명주소용) */
    private String dongNm3;
    /** 지상 층 수 */
    private int grndFlrCnt;
}

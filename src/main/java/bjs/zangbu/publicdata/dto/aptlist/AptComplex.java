package bjs.zangbu.publicdata.dto.aptlist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AptComplex {
    /** 단지 고유 코드 */
    private String kaptCode;
    /** 단지 이름 */
    private String kaptName;
    /** 법정동 코드 (10자리) */
    private String bjdCode;
    /** 시도명 */
    private String as1;
    /** 시군구명 */
    private String as2;
    /** 읍면동명 */
    private String as3;
    /** 리명 (존재 시) */
    private String as4;
}

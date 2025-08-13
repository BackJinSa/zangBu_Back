package bjs.zangbu.complexList.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ComplexList {

    /** 단지 식별자 (PK로 사용 가능) */
    private Long complexId;

    /** 부동산 유형 (예: 아파트, 오피스텔 등) */
    private String resType;

    /** 단지 이름 (예: 송파더센트레 등) */
    private String complexName;

  /**
   * 단지 입력 번호 (외부 시스템에서 부여하는 고유 번호)
   */
    private String complexNo;

    /** 시/도 명칭 (예: 서울특별시) */
    private String sido;

    /** 시/군/구 명칭 (예: 송파구) */
    private String sigungu;

    /** 시군구 코드 (행정구역 코드, 예: 11710) */
    private String siCode;

    /** 읍/면/동 명칭 (예: 장지동) */
    private String eupmyeondong;

    /** Codef 트랜잭션 ID (API 요청 식별을 위한 고유 값) */
    private String transactionId;

    /** 기본 주소 (전체 주소 문자열, 예: 서울 송파구 장지동 888) */
    private String address;

    /** 우편번호 (예: 05819) */
    private String zonecode;

    /** 건물 이름 (예: 송파센트럴파크 A동) */
    private String buildingName;

    /** 법정동/법정리 명칭 (예: 장지동) */
    private String bname;

    /** 동 정보 (예: 101동) */
    private String dong;

    /** 호수 정보 (예: 1202호) */
    private String ho;

    /** 도로명 */
    private String roadName;
}

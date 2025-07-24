package bjs.zangbu.complexList.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ComplexList {
    // 단지 식별 ID
    private Long complexId;
    // 부동산 유형
    private String resType;
    // 단지 이름
    private String complexName;
    // 단지 입력번호
    private Integer complexNo;
    // 시/도
    private String sido;
    // 시/군/구
    private String sigungu;
    // 시군구 코드
    private String siCode;
    // 읍/면/동
    private String eupmyeondong;
    // Codef 트랜잭션 ID
    private String transactionId;
    // 기본 주소
    private String address;
    // 우편번호
    private String zonecode;
    // 건물명
    private String buildingName;
    // 법정동/법정리 이름
    private String bname;
}

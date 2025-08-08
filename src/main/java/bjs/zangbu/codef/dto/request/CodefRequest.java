package bjs.zangbu.codef.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CODEF API 호출 시 사용되는 요청 DTO들을 모아놓은 클래스.
 */
// @Schema(description = "CODEF API 요청 DTO")
public class CodefRequest {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(description = "2-Way 인증(보안문자) 완료용 요청 DTO")
  public class secureNoRequest {

    //         @Schema(description = "Redis에 저장한 CodefAuthSession의 Key", example = "session_key_123")
    private String sessionKey;
    //         @Schema(description = "사용자가 입력한 보안문자 또는 캡차 숫자", example = "1234")
    private String secureNo;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(description = "건물 주소 정보를 사용하여 단지 정보를 조회하기 위한 요청 DTO")
  public class AddressRequest {

    //         @Schema(description = "주소의 시/도", example = "서울특별시")
    private String addrSido;
    //         @Schema(description = "주소의 시/군/구", example = "송파구")
    private String addrSigun;
    //         @Schema(description = "주소의 동/읍/면", example = "방이동")
    private String addrDong;
    //         @Schema(description = "조회하고자 하는 건물명 또는 단지명", example = "송파더센트레")
    private String buildingName;
  }
}
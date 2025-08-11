package bjs.zangbu.fcm.dto.request;

import bjs.zangbu.fcm.vo.Fcm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public class FcmRequest {

  /**
   * 토큰 등록 Request /fcm/register
   */
  @Getter
  @NoArgsConstructor
  public class FcmRegisterRequest {
    @NotBlank
    private String token;        // FCM 디바이스 토큰 (필수)
    private String deviceType;   // 예: WEB_CHROME, WEB_FIREFOX, ANDROID, IOS (선택)
    private String deviceName;   // 예: "Chrome on Windows 11" (선택)
  }

  /**
   * DTO → VO 변환
   */
  public static Fcm tovo(FcmRegisterRequest request, String memberId) {
    return new Fcm(
            null,                   // id (Auto Increment)
            request.getToken(),     // token
            request.getDeviceType(),// 디바이스 유형
            request.getDeviceName(),
            request.get// 디바이스 이름
            memberId               // memberId
    );
  }
}

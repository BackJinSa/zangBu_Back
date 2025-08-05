package bjs.zangbu.fcm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FcmRequest {

    /**
     * 토큰 등록 Request
     * /fcm/register
     */
    @Getter
    @NoArgsConstructor
    @Schema(description = "FCM 디바이스 토큰 요청")
    public static class FcmTokenRequest {

        @Schema(description = "FCM 디바이스 토큰 값", example = "d7q3Lk82xH...Xyz")
        private String token; // 삭제 시 null 가능
    }
}

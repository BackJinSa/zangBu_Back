package bjs.zangbu.fcm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FcmRequest {

    /**
     * 토큰 등록 Request
     * /fcm/register
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FcmTokenRequest {
        private String token; // 삭제 시 null 가능
    }
}

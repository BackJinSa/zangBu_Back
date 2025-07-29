package bjs.zangbu.codef.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * 2‑Way 인증(보안문자) 완료용 요청 DTO.
 *
 *  • sessionKey : Redis 에 저장한 CodefAuthSession 의 Key
 *  • secureNo   : 사용자가 입력한 보안문자 / 캡차 숫자
 */
public class CodefRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class secureNoRequest {
        private String sessionKey;
        private String secureNo;
    }
}

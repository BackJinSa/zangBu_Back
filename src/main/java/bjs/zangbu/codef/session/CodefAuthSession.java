package bjs.zangbu.codef.session;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
/**
 * 2‑Way 인증 도중(1차⇄2차 사이) 필요한 세션 정보를
 * Redis 등에 저장하기 위한 직렬화 객체.
 *
 *  • parameterMap   : 최초 요청 파라미터 전체
 *  • jobIndex …     : CODEF 가 두 번째 호출 시 필요로 하는 키들
 *  • productUrl     : 해당 세션이 요청 중인 상품 URL
 */
@Getter
@Setter
public class CodefAuthSession implements Serializable {
    private HashMap<String, Object> parameterMap;
    private Integer jobIndex;
    private Integer threadIndex;
    private String jti;
    private Long twoWayTimestamp;
    private String productUrl;
}


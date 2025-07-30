package bjs.zangbu.codef.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import java.util.HashMap;
import java.util.UUID;

/**
 * CODEF API 요청 및 2차 인증(2-Way Authentication) 처리를 담당하는 스레드 클래스.
 */
@Getter
@RequiredArgsConstructor
public class CodefSessionThread extends Thread {
    /**
     * CODEF API 호출을 위한 EasyCodef 인스턴스 (final)
     */
    private final EasyCodef codef;

    /**
     * API 호출 시 전달할 파라미터 맵 (final)
     */
    private final HashMap<String, Object> parameterMap;

    /**
     * 스레드 번호 (final)
     */
    private final int threadNo;

    /**
     * 호출할 CODEF 상품 URL 경로 (final)
     */
    private final String productUrl;

    /**
     * 2차 인증 관련 정보를 Redis에 저장할 때 사용하는 세션 키(UUID)
     */
    private String sessionKey;

    /**
     * 1차 인증 API 호출 응답(JSON 문자열)
     */
    private String firstResponse;

    /**
     * 2차 인증 API 호출 응답(JSON 문자열, 2차 인증이 없으면 null)
     */
    private String secondResponse;

    @Override
    public void run() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            // 이하 기존 코드 유지
            firstResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);

            HashMap<String, Object> responseMap = new ObjectMapper().readValue(firstResponse, HashMap.class);
            HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
            String code = (String) resultMap.get("code");
            HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

            boolean continue2Way = dataMap != null && Boolean.TRUE.equals(dataMap.get("continue2Way"));

            if ("CF-03002".equals(code) && continue2Way) {
                sessionKey = UUID.randomUUID().toString();

                jedis.hset(sessionKey, "jobIndex", dataMap.get("jobIndex").toString());
                jedis.hset(sessionKey, "threadIndex", dataMap.get("threadIndex").toString());
                jedis.hset(sessionKey, "jti", dataMap.get("jti").toString());
                jedis.hset(sessionKey, "twoWayTimestamp", dataMap.get("twoWayTimestamp").toString());

                HashMap<String, Object> twoWayParams = new HashMap<>();
                twoWayParams.put("jobIndex", dataMap.get("jobIndex"));
                twoWayParams.put("threadIndex", dataMap.get("threadIndex"));
                twoWayParams.put("jti", dataMap.get("jti"));
                twoWayParams.put("twoWayTimestamp", dataMap.get("twoWayTimestamp"));

                secondResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, twoWayParams);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

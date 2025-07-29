package bjs.zangbu.codef.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import lombok.Getter;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class CodefSessionThread extends Thread {
    private EasyCodef codef;
    private HashMap<String, Object> parameterMap;
    private int threadNo;
    private String productUrl;

    private String sessionKey;
    private String firstResponse;
    private String secondResponse;

    public CodefSessionThread(EasyCodef codef, HashMap<String, Object> parameterMap, int threadNo, String productUrl) {
        this.codef = codef;
        this.parameterMap = parameterMap;
        this.threadNo = threadNo;
        this.productUrl = productUrl;
    }

    @Override
    public void run() {
        try (Jedis jedis = new Jedis("localhost", 6379)) { // 실제 환경에 맞게 설정
            // 1차 인증 요청
            firstResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);

            HashMap<String, Object> responseMap = new ObjectMapper().readValue(firstResponse, HashMap.class);
            HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
            String code = (String) resultMap.get("code");
            HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

            boolean continue2Way = dataMap != null && Boolean.TRUE.equals(dataMap.get("continue2Way"));

            // 2차 인증이 필요한 경우
            if ("CF-03002".equals(code) && continue2Way) {
                sessionKey = UUID.randomUUID().toString();
                // Redis에 2차 인증 정보 저장 (실제 Redis 필요 없으면 이부분 생략 가능)
                jedis.hset(sessionKey, "jobIndex", dataMap.get("jobIndex").toString());
                jedis.hset(sessionKey, "threadIndex", dataMap.get("threadIndex").toString());
                jedis.hset(sessionKey, "jti", dataMap.get("jti").toString());
                jedis.hset(sessionKey, "twoWayTimestamp", dataMap.get("twoWayTimestamp").toString());

                // 2차 인증 파라미터 생성
                HashMap<String, Object> twoWayParams = new HashMap<>();
                twoWayParams.put("jobIndex", dataMap.get("jobIndex"));
                twoWayParams.put("threadIndex", dataMap.get("threadIndex"));
                twoWayParams.put("jti", dataMap.get("jti"));
                twoWayParams.put("twoWayTimestamp", dataMap.get("twoWayTimestamp"));

                // 2차 인증 요청 (즉시)
                secondResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, twoWayParams);

                // 원하면 여기서 secondResponse에 sessionKey 포함해서 가공도 가능
                // 필요 없으면 생략
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

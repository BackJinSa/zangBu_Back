package bjs.zangbu.codef.thread;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import lombok.Getter;

import java.util.HashMap;


@Getter
public class CodefThread extends Thread {
    private EasyCodef codef;
    private HashMap<String, Object> parameterMap;

    public int getThreadNo() {
        return threadNo;
    }

    private int threadNo;
    private String productUrl;

    // 2-Way 인증 정보
    private Integer jobIndex;
    private Integer threadIndex;
    private String jti;
    private Long twoWayTimestamp;
    private String firstResponse;
    private String secondResponse;

    public CodefThread(EasyCodef codef, HashMap<String, Object> parameterMap, int threadNo, String productUrl) {
        this.codef = codef;
        this.parameterMap = parameterMap;
        this.threadNo = threadNo;
        this.productUrl = productUrl;
    }

    public String getFirstResponse() { return firstResponse; }
    public String getSecondResponse() { return secondResponse; }

    @Override
    public void run() {
        try {
            // 1차 인증 요청
            firstResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);

            HashMap<String, Object> responseMap = new ObjectMapper().readValue(firstResponse, HashMap.class);
            HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
            String code = (String) resultMap.get("code");
            HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

            boolean continue2Way = false;
            if (dataMap != null && dataMap.containsKey("continue2Way")) {
                continue2Way = Boolean.TRUE.equals(dataMap.get("continue2Way"));
            }

            // 2-Way 인증이 필요하면 값 저장 → 즉시 2차 인증 요청
            if ("CF-03002".equals(code) && continue2Way) {
                this.jobIndex = (Integer) dataMap.get("jobIndex");
                this.threadIndex = (Integer) dataMap.get("threadIndex");
                this.jti = (String) dataMap.get("jti");
                this.twoWayTimestamp = ((Number) dataMap.get("twoWayTimestamp")).longValue();

                // 2차 인증 호출
                HashMap<String, Object> twoWayParams = new HashMap<>();
                twoWayParams.put("jobIndex", jobIndex);
                twoWayParams.put("threadIndex", threadIndex);
                twoWayParams.put("jti", jti);
                twoWayParams.put("twoWayTimestamp", twoWayTimestamp);
                secondResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, twoWayParams);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

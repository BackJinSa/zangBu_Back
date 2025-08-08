package bjs.zangbu.codef.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import lombok.Getter;

import java.util.HashMap;

/**
 * CODEF API 2차 인증 처리 스레드.
 */
@Getter
public class CodefThread extends Thread {
    private EasyCodef codef;
    private HashMap<String, Object> twoWayParams;
    private String productUrl;

    private String secondResponse;

    public CodefThread(EasyCodef codef, HashMap<String, Object> twoWayParams, String productUrl) {
        this.codef = codef;
        this.twoWayParams = twoWayParams;
        this.productUrl = productUrl;
    }

    public String getSecondResponse() {
        return secondResponse;
    }

    @Override
    public void run() {
        try {
            // ⭐ 2차 인증 요청을 바로 보냄
            secondResponse = codef.requestCertification(productUrl, EasyCodefServiceType.DEMO, twoWayParams);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
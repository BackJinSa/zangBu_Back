package bjs.zangbu.codef.service;

import bjs.zangbu.codef.encryption.CodefEncryption;
import bjs.zangbu.codef.thread.CodefThread;
import io.codef.api.EasyCodef;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CodefTwoFactorServiceImpl implements CodefTwoFactorService {

    // CODEF 암호화 및 인증을 위한 유틸 클래스
    private final CodefEncryption codefEncryption;

    // CODEF SDK 객체 초기화에 사용
    private EasyCodef codef;

    // 애플리케이션이 시작되면 CODEF 인스턴스를 초기화
    @PostConstruct
    public void init() {
        codef = codefEncryption.getCodefInstance();
    }

    @Override
    public String residentRegistrationCertificate(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        String productUrl = "/v1/kr/public/mw/resident-registration-abstract/issuance";
        List<CodefThread> threadList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            HashMap<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("organization", "0001");
            parameterMap.put("loginType", "5");
            parameterMap.put("userName", );
            parameterMap.put("identity", );
            parameterMap.put("birthDate", );
            parameterMap.put("identityEncYn", "Y");
            parameterMap.put("loginTypeLevel", );
            parameterMap.put("phoneNo", );
            parameterMap.put("addrSido", );
            parameterMap.put("addrSiGunGu",);
            parameterMap.put("personalInfoChangeYN", "0");
            parameterMap.put("pastAddrChangeYN", "1");
            parameterMap.put("nameRelationYN", "0");
            parameterMap.put("militaryServiceYN", "0");
            parameterMap.put("overseasKoreansIDYN", "0");
            parameterMap.put("isIdentityViewYn", "0");
            parameterMap.put("originDataYN", "0");

            CodefThread t = new CodefThread(codef, parameterMap, i, productUrl);
            t.start();
            threadList.add(t);
            Thread.sleep(10000); // throws InterruptedException
        }

        StringBuilder sb = new StringBuilder();
        for (CodefThread t : threadList) {
            t.join(); // throws InterruptedException
            if (t.getSecondResponse() != null) {
                sb.append(t.getSecondResponse());
            } else if (t.getFirstResponse() != null) {
                sb.append(t.getFirstResponse());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    @Override
    public String generalBuildingLeader(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        String productUrl = "/v1/kr/public/ck/real-estate-register/identity-matching";
        List<CodefThread> threadList = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            HashMap<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("organization", "0001");
            parameterMap.put("loginType", "5");
            parameterMap.put("userName", );
            parameterMap.put("identity", );
            parameterMap.put("identityEncYn", "Y");
            parameterMap.put("birthDate", );
            parameterMap.put("loginTypeLevel", "1");
            parameterMap.put("phoneNo", );
            parameterMap.put("telecom", );
            parameterMap.put("address", );
            parameterMap.put("dong", );
            parameterMap.put("ho", );
            parameterMap.put("type", "0");
            parameterMap.put("zipCode", );
            parameterMap.put("originDateYN", "1");

            CodefThread t = new CodefThread(codef, parameterMap, i, productUrl);
            t.start();
            threadList.add(t);
            Thread.sleep(10000);
        }

        StringBuilder sb = new StringBuilder();
        for (CodefThread t : threadList) {
            t.join();
            if (t.getSecondResponse() != null) {
                sb.append(t.getSecondResponse());
            } else if (t.getFirstResponse() != null) {
                sb.append(t.getFirstResponse());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

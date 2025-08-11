package bjs.zangbu.security.account.client;

import bjs.zangbu.security.account.dto.response.AuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.VerifyCodefResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class PassApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    //PASS 본인인증 서비스에서 제공한 실제 API 주소로 수정
    private final String PASS_API_URL = "https://pass.example.com/api/verify";

    //외부 pass api에 요청
    public ResponseEntity<VerifyCodefResponse> sendVerification(Map<String, String> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        return restTemplate.postForEntity(PASS_API_URL, request, AuthResponse.VerifyCodefResponse.class);
    }
}
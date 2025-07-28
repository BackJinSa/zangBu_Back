package bjs.zangbu.security.account.client;

import bjs.zangbu.security.account.dto.response.AuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.AuthVerify;
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
    private final String PASS_API_URL = "https://pass.example.com/api/verify";

    public ResponseEntity<AuthVerify> sendVerification(Map<String, String> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        return restTemplate.postForEntity(PASS_API_URL, request, AuthResponse.AuthVerify.class);
    }
}
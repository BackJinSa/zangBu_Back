package bjs.zangbu.payment.service;

import bjs.zangbu.payment.dto.request.PaymentRequest;
import bjs.zangbu.payment.dto.response.PaymentConfirmResponse;
import bjs.zangbu.payment.dto.request.PaymentConfirmRequest;
import bjs.zangbu.payment.dto.response.PaymentResponse;
import bjs.zangbu.payment.mapper.PaymentInsertParam;
import bjs.zangbu.payment.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentMapper paymentMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    // Toss 샌드박스 키
    @Value("${toss.secretKey}")
    private String secretKey;

    @Value("${toss.apiBaseUrl}")
    private String apiBaseUrl;


    @Override
    public PaymentResponse createPayment(PaymentRequest req, String memberId) {
        if (req.getOrderId() == null || req.getAmount() == null || req.getAmount() <= 0) {
            throw new IllegalArgumentException("결제 요청에 실패했습니다.");
        }

        // TODO: 실제 간편결제 API 호출 로직을 여기 넣을 수도 있습니다.

        // DB에 결제 레코드 저장 (token 필드에 amount 저장)
        PaymentInsertParam param = new PaymentInsertParam();
        param.setMemberId(memberId);
        param.setToken(req.getAmount());
        paymentMapper.insertPayment(param);

        // 응답에는 orderId 만 반환
        return new PaymentResponse(req.getOrderId());
    }

    // 결제 승인 요청 기능
    @Override
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest req) {
        // 1) 파라미터 검증
        if (req.getPaymentKey() == null
                || req.getOrderId()   == null
                || req.getAmount()    == null) {
            throw new IllegalArgumentException("결제 승인에 실패했습니다.");
        }

        // 2) HTTP 헤더 세팅 (Basic Auth: secretKey:)
        String auth = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3) 요청 바디
        Map<String, Object> body = Map.of(
                "orderId", req.getOrderId(),
                "amount",  req.getAmount()
        );

        HttpEntity<Map<String,Object>> httpReq = new HttpEntity<>(body, headers);

        // 4) REST 호출
        String url = UriComponentsBuilder
                .fromHttpUrl(apiBaseUrl)
                .path("/v1/payments/{paymentKey}/confirm")
                .buildAndExpand(req.getPaymentKey())
                .toUriString();

        ResponseEntity<Map> resp = restTemplate.exchange(
                url, HttpMethod.POST, httpReq, Map.class
        );
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new RuntimeException("결제 승인 API 호출 실패");
        }

        Map<String, Object> data = (Map<String, Object>) resp.getBody().get("payment");
        // 5) DB에 승인 시각 업데이트
        paymentMapper.confirmPayment(req);

        // 6) DTO로 변환
        return PaymentConfirmResponse.from(data);
    }
}

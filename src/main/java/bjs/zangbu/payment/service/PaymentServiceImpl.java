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
    private final RestTemplate restTemplate;
    // Toss 샌드박스 키
    @Value("${toss.secretKey}")
    private String secretKey;

    @Value("${toss.apiBaseUrl}")
    private String apiBaseUrl;


    //프론트엔드에 연결하면
    //POST /payment 호출 → { orderId, paymentPageUrl } 응답
    //window.location.href = paymentPageUrl; 로 테스트용 결제창 띄우기
    @Override
    public PaymentResponse createPayment(PaymentRequest req, String memberId) {
        // 1) 요청 유효성 검사
        if (req.getOrderId()    == null ||
                req.getAmount()     == null || req.getAmount() <= 0 ||
                req.getOrderName()  == null ||
                req.getSuccessUrl() == null ||
                req.getFailUrl()    == null ||
                req.getMethod()     == null ||
                req.getEasyPay()    == null ||
                req.getFlowMode()   == null) {
            throw new IllegalArgumentException("결제 요청이 실패했습니다.");
        }

        // 2) Basic Auth 헤더 준비
        String auth = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3) Toss Ready API 호출
        Map<String, Object> readyBody = Map.of(
                "amount",       req.getAmount(),
                "orderId",      req.getOrderId(),
                "orderName",    req.getOrderName(),
                "customerName", req.getCustomerName(),
                "successUrl",   req.getSuccessUrl(),
                "failUrl",      req.getFailUrl(),
                "method",       req.getMethod(),
                "easyPay",      req.getEasyPay(),
                "flowMode",     req.getFlowMode()
        );
        HttpEntity<Map<String,Object>> readyReq = new HttpEntity<>(readyBody, headers);

        // 4) REST 호출 → 결제 페이지 URL 획득
        String readyUrl = UriComponentsBuilder
                .fromHttpUrl(apiBaseUrl)
                .path("/v1/payments/ready")
                .toUriString();
        ResponseEntity<Map> readyResp = restTemplate.exchange(
                readyUrl, HttpMethod.POST, readyReq, Map.class
        );
        if (!readyResp.getStatusCode().is2xxSuccessful() || readyResp.getBody() == null) {
            throw new RuntimeException("결제 준비 API 호출 실패");
        }
        Map<String, Object> paymentNode = (Map<String,Object>)readyResp.getBody().get("payment");
        Map<String, String> urlNode     = (Map<String,String>)paymentNode.get("url");
        String paymentPageUrl           = urlNode.get("mobile");

        // 5) DB에 주문 + 금액 기록
        PaymentInsertParam param = new PaymentInsertParam();
        param.setMemberId(memberId);
        param.setOrderId(req.getOrderId());
        param.setAmount(req.getAmount());
        paymentMapper.insertPayment(param);

        // 6) Response DTO 반환 (orderId + paymentPageUrl)
        return new PaymentResponse(req.getOrderId(), paymentPageUrl);
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

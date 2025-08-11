package bjs.zangbu.payment.service;

import bjs.zangbu.payment.mapper.PaymentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public void confirmPayment(String memberId, Map<String, Object> payload) {
        // payload: paymentKey, orderId, amount, productType, productId, price, orderName, method, pgPayload
        paymentMapper.upsertPaymentOnConfirm(Map.of(
                "memberId", memberId,
                "orderId", payload.get("orderId"),
                "paymentKey", payload.get("paymentKey"),
                "amount", payload.get("amount"),
                "productType", payload.get("productType"),
                "productId", payload.get("productId"),
                "method", payload.getOrDefault("method", "CARD"),
                "pgPayload", payload.get("pgPayload")
        ));

        String productType = String.valueOf(payload.get("productType"));
        if ("MEMBERSHIP".equals(productType)) {
            paymentMapper.upsertMembership(memberId);
        } else if ("PER_CASE".equals(productType)) {
            paymentMapper.addPerCaseBalance(memberId, 1);
        }
    }

    public Map<String, Object> getEntitlements(String memberId) {
        return paymentMapper.selectEntitlements(memberId);
    }

    @Transactional
    public boolean consumePerCase(String memberId) {
        return paymentMapper.consumePerCase(memberId) == 1;
    }

    public void recordDownload(String memberId, String resourceType, String resourceId, String usedPaymentType, String orderId) {
        paymentMapper.insertDownloadHistory(new HashMap<String, Object>() {{
            put("memberId", memberId);
            put("resourceType", resourceType);
            put("resourceId", resourceId);
            put("usedPaymentType", usedPaymentType);
            put("orderId", orderId);
        }});
    }
}
package bjs.zangbu.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PaymentConfirmResponse {
    private String paymentId;   // Toss가 돌려준 결제 고유 키
    private String orderId;     // 최초 요청 orderId
    private Integer amount;     // 승인된 금액
    private String approvedAt;  // 승인 완료 시간 (ISO 8601)

    public static PaymentConfirmResponse from(Map<String, Object> data) {
        return new PaymentConfirmResponse(
                (String) data.get("paymentKey"),
                (String) data.get("orderId"),
                ((Number) data.get("totalAmount")).intValue(),
                (String) data.get("approvedAt")
        );
    }
}

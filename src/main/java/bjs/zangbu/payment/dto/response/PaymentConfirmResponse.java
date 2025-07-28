package bjs.zangbu.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentConfirmResponse {
    private String paymentId;   // Toss가 돌려준 결제 고유 키
    private String orderId;     // 최초 요청 orderId
    private Integer amount;     // 승인된 금액
    private String approvedAt;  // 승인 완료 시간 (ISO 8601)
}

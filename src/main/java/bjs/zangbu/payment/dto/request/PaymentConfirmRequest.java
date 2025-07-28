package bjs.zangbu.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentConfirmRequest {
    private String paymentKey; // Toss 제공 결제 키
    private String orderId;     // 최초 주문 요청 ID (우리 쪽 주문 ID)
    private Integer amount;     // 최초 결제 요정 금액
}

package bjs.zangbu.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResponse {
    private String orderId;
    private final String paymentPageUrl;  // Toss가 준 결제 페이지 URL
}

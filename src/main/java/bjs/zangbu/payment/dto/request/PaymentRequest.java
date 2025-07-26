package bjs.zangbu.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {
    private String method;        // 간편결제 공통 방식
    private String easyPay;       // "TOSSPAY" | "KAKAOPAY" | "NAVERPAY" | "PAYCO"
    private Integer amount;       // 결제 금액
    private String orderId;       // 주문 고유번호 (우리가 만들어야 함)
    private String orderName;     // 상품명
    private String customerName;  // 구매자 이름
    private String successUrl;    // 결제 성공 콜백 "https://yourdomain.com/success"
    private String failUrl;       // 결제 실패 콜백 "https://yourdomain.com/fail"
    private String flowMode;      // "DEFAULT"
}

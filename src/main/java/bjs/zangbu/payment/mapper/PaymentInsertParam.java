package bjs.zangbu.payment.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInsertParam {
    private Long    paymentId;  // 자동 생성 결제 id
    private String  memberId;   // 토큰에서 꺼낸 유저 식별자
    private String  orderId;    // 주문 고유번호
    private Integer amount;     // 결제 금액
}

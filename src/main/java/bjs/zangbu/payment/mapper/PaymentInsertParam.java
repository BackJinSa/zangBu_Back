package bjs.zangbu.payment.mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInsertParam {
    private Long paymentId; // 자동 생성 결제 id
    private String memberId; // 리뷰처럼 토큰에서 꺼내는 유저 식별자
    private Integer token; // 결제 토큰(여기선 amount 넣어둠)
}

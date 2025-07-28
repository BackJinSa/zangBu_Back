package bjs.zangbu.payment.service;

import bjs.zangbu.payment.dto.request.PaymentRequest;
import bjs.zangbu.payment.dto.response.PaymentResponse;
import bjs.zangbu.payment.mapper.PaymentInsertParam;
import bjs.zangbu.payment.mapper.PaymentMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentMapper paymentMapper;

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
}

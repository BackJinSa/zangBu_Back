package bjs.zangbu.payment.service;

import bjs.zangbu.payment.dto.request.PaymentConfirmRequest;
import bjs.zangbu.payment.dto.request.PaymentRequest;
import bjs.zangbu.payment.dto.response.PaymentConfirmResponse;
import bjs.zangbu.payment.dto.response.PaymentResponse;
import org.springframework.stereotype.Service;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest req, String memberId);
    PaymentConfirmResponse confirmPayment(PaymentConfirmRequest req);
}

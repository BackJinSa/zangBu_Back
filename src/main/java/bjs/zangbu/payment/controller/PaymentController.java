package bjs.zangbu.payment.controller;

import bjs.zangbu.payment.dto.request.PaymentRequest;
import bjs.zangbu.payment.dto.response.PaymentResponse;
import bjs.zangbu.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // POST /payment
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PaymentRequest req,
                                    @RequestHeader("Authorization") String bearerToken) {
        try {
            // 토큰에서 memberId 추출 (JwtUtil 등)
            //String memberId = TokenUtil.getUserId(bearerToken);
            PaymentResponse resp = paymentService.createPayment(req, "임시 상단 memberId 구현 시 memberId 로 변경");
            return ResponseEntity.ok(resp);  // 200
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("결제 요청에 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("서버에서 결제 요청을 처리하는데 실패했습니다.");
        }
    }
}

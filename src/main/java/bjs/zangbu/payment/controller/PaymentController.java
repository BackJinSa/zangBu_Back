package bjs.zangbu.payment.controller;

import bjs.zangbu.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody Map<String, Object> body, HttpServletRequest req) {
        String memberId = /* 인증 컨텍스트에서 추출 or 테스트용 하드코딩 */ (String) req.getAttribute("memberId");
        paymentService.confirmPayment(memberId, body);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/entitlements")
    public ResponseEntity<?> entitlements(HttpServletRequest req) {
        String memberId = (String) req.getAttribute("memberId");
        return ResponseEntity.ok(paymentService.getEntitlements(memberId));
    }

    @PostMapping("/consume")
    public ResponseEntity<?> consume(@RequestBody Map<String, Object> body, HttpServletRequest req) {
        String memberId = (String) req.getAttribute("memberId");
        boolean ok = paymentService.consumePerCase(memberId);
        if (!ok) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","잔여 건수가 없습니다."));
        return ResponseEntity.ok(Map.of("success", true));
    }
}
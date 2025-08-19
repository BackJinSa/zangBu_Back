package bjs.zangbu.payment.controller;

import bjs.zangbu.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import bjs.zangbu.security.account.vo.CustomUser;

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
    public ResponseEntity<?> confirm(@RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUser customUser) {
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String memberId = customUser.getMember().getMemberId();
        paymentService.confirmPayment(memberId, body);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/entitlements")
    public ResponseEntity<?> entitlements(@AuthenticationPrincipal CustomUser customUser) {
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String memberId = customUser.getMember().getMemberId();
        return ResponseEntity.ok(paymentService.getEntitlements(memberId));
    }

    @PostMapping("/consume")
    public ResponseEntity<?> consume(@RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUser customUser) {
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String memberId = customUser.getMember().getMemberId();
        boolean ok = paymentService.consumePerCase(memberId);
        if (!ok)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "잔여 건수가 없습니다."));
        return ResponseEntity.ok(Map.of("success", true));
    }
}
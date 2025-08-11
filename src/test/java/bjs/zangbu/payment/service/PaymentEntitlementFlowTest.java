package bjs.zangbu.payment.service;

import bjs.zangbu.payment.mapper.PaymentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEntitlementFlowTest {

    @Mock
    PaymentMapper paymentMapper;

    @InjectMocks
    PaymentService paymentService;

    @Test
    @DisplayName("멤버십 우선 허용, 아니면 건당 차감")
    void entitlementPriority() {
        String memberId = "member-10";

        // membership active
        when(paymentMapper.selectEntitlements(memberId)).thenReturn(Map.of(
                "membershipActive", true,
                "perCaseRemaining", 0));

        Map<String, Object> ent = paymentService.getEntitlements(memberId);
        assertThat(ent).containsEntry("membershipActive", true);

        // consume should not be called if we just check; ensure explicit consume works
        when(paymentMapper.consumePerCase(memberId)).thenReturn(1);
        boolean consumed = paymentService.consumePerCase(memberId);
        assertThat(consumed).isTrue();

        // membership inactive, fallback to per-case
        when(paymentMapper.selectEntitlements(memberId)).thenReturn(Map.of(
                "membershipActive", false,
                "perCaseRemaining", 1));
        ent = paymentService.getEntitlements(memberId);
        assertThat(ent).containsEntry("perCaseRemaining", 1);
    }
}

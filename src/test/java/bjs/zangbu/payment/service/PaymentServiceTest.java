package bjs.zangbu.payment.service;

import bjs.zangbu.payment.mapper.PaymentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private String memberId;

    @BeforeEach
    void setUp() {
        memberId = "member-123";
    }

    @Test
    @DisplayName("결제확인 - MEMBERSHIP: payment upsert, membership upsert 호출")
    void confirmPayment_membership() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentKey", "pay_123");
        payload.put("orderId", "order_1");
        payload.put("amount", 9900);
        payload.put("productType", "MEMBERSHIP");
        payload.put("productId", "plan_standard");
        payload.put("method", "CARD");
        payload.put("pgPayload", Map.of("raw", "toss"));

        when(paymentMapper.upsertPaymentOnConfirm(anyMap())).thenReturn(1);
        when(paymentMapper.upsertMembership(memberId)).thenReturn(1);

        paymentService.confirmPayment(memberId, payload);

        ArgumentCaptor<Map<String, Object>> cap = ArgumentCaptor.forClass(Map.class);
        verify(paymentMapper, times(1)).upsertPaymentOnConfirm(cap.capture());
        verify(paymentMapper, times(1)).upsertMembership(memberId);
        verify(paymentMapper, never()).addPerCaseBalance(anyString(), anyInt());

        Map<String, Object> saved = cap.getValue();
        assertThat(saved.get("memberId")).isEqualTo(memberId);
        assertThat(saved.get("orderId")).isEqualTo("order_1");
        assertThat(saved.get("paymentKey")).isEqualTo("pay_123");
        assertThat(saved.get("amount")).isEqualTo(9900);
        assertThat(saved.get("productType")).isEqualTo("MEMBERSHIP");
        assertThat(saved.get("productId")).isEqualTo("plan_standard");
        assertThat(saved.get("method")).isEqualTo("CARD");
        assertThat(saved.get("pgPayload")).isInstanceOf(Map.class);
    }

    @Test
    @DisplayName("결제확인 - PER_CASE: payment upsert, per_case 적립 호출")
    void confirmPayment_perCase() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentKey", "pay_456");
        payload.put("orderId", "order_2");
        payload.put("amount", 1900);
        payload.put("productType", "PER_CASE");
        payload.put("productId", "per_case_1");
        payload.put("method", "CARD");
        payload.put("pgPayload", Map.of("raw", "toss"));

        when(paymentMapper.upsertPaymentOnConfirm(anyMap())).thenReturn(1);
        when(paymentMapper.addPerCaseBalance(memberId, 1)).thenReturn(1);

        paymentService.confirmPayment(memberId, payload);

        verify(paymentMapper, times(1)).upsertPaymentOnConfirm(anyMap());
        verify(paymentMapper, times(1)).addPerCaseBalance(memberId, 1);
        verify(paymentMapper, never()).upsertMembership(anyString());
    }

    @Test
    @DisplayName("권한 조회 위임")
    void getEntitlements() {
        when(paymentMapper.selectEntitlements(memberId))
                .thenReturn(Map.of("membershipActive", true, "perCaseRemaining", 3));

        Map<String, Object> result = paymentService.getEntitlements(memberId);

        assertThat(result).containsEntry("membershipActive", true)
                .containsEntry("perCaseRemaining", 3);
        verify(paymentMapper, times(1)).selectEntitlements(memberId);
    }

    @Test
    @DisplayName("건당 차감: 성공/실패 반환")
    void consumePerCase() {
        when(paymentMapper.consumePerCase(memberId)).thenReturn(1).thenReturn(0);

        boolean first = paymentService.consumePerCase(memberId);
        boolean second = paymentService.consumePerCase(memberId);

        assertThat(first).isTrue();
        assertThat(second).isFalse();
        verify(paymentMapper, times(2)).consumePerCase(memberId);
    }

    @Test
    @DisplayName("다운로드 기록 위임")
    void recordDownload() {
        when(paymentMapper.insertDownloadHistory(anyMap())).thenReturn(1);

        paymentService.recordDownload(memberId, "ANALYSIS_REPORT", "R-1", "MEMBERSHIP", null);

        verify(paymentMapper, times(1)).insertDownloadHistory(anyMap());
    }
}

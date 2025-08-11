package bjs.zangbu.payment.controller;

import bjs.zangbu.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest {

    private final PaymentService paymentService = Mockito.mock(PaymentService.class);
    private final PaymentController controller = new PaymentController(paymentService);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("/payment/confirm 호출 시 서비스로 위임하고 200 반환")
    void confirm() throws Exception {
        Map<String, Object> body = Map.of(
                "paymentKey", "pay_1",
                "orderId", "order_1",
                "amount", 1000,
                "productType", "PER_CASE",
                "productId", "per_case_1");

        mockMvc.perform(post("/payment/confirm")
                .requestAttr("memberId", "member-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(paymentService, times(1)).confirmPayment(eq("member-1"), any());
    }

    @Test
    @DisplayName("/payment/entitlements 권한 조회 200")
    void entitlements() throws Exception {
        Mockito.when(paymentService.getEntitlements("member-2")).thenReturn(Map.of(
                "membershipActive", true,
                "perCaseRemaining", 2));

        mockMvc.perform(get("/payment/entitlements").requestAttr("memberId", "member-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membershipActive").value(true))
                .andExpect(jsonPath("$.perCaseRemaining").value(2));
    }

    @Test
    @DisplayName("/payment/consume 차감 성공/실패 응답")
    void consume() throws Exception {
        Mockito.when(paymentService.consumePerCase("member-3")).thenReturn(true);

        mockMvc.perform(post("/payment/consume")
                .requestAttr("memberId", "member-3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Mockito.when(paymentService.consumePerCase("member-4")).thenReturn(false);

        mockMvc.perform(post("/payment/consume")
                .requestAttr("memberId", "member-4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

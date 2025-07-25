// src/test/java/bjs/zangbu/deal/controller/DealConsumerControllerTest.java
package bjs.zangbu.deal.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import bjs.zangbu.deal.dto.join.DealWithSaleType;
import bjs.zangbu.deal.dto.request.DealRequest.Status;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.deal.service.ContractService;
import bjs.zangbu.deal.service.ContractServiceImpl;
import bjs.zangbu.deal.vo.DealEnum;
import bjs.zangbu.notification.vo.SaleType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


/**
 * 계약서 다운로드 URL API 컨트롤러 단위 테스트 ------------------------------------------------------------- •
 * Spring-test 의 MockMvc stand-alone 모드만 사용 • 케이스 1) 매매(TRADING)  → /contracts/sale_contract.pdf 2)
 * 월세(MONTHLY) → /contracts/lease_contract.pdf
 */
class DealControllerTest {

  private MockMvc mockMvc;

  /**
   * 테스트용 stub DealMapper — 필요한 메서드만 구현
   */
  private static class StubDealMapper implements DealMapper {

    private final long dealId;
    private final SaleType saleType;

    StubDealMapper(long dealId, SaleType saleType) {
      this.dealId = dealId;
      this.saleType = saleType;
    }

    private DealWithSaleType build() {
      DealWithSaleType d = new DealWithSaleType();
      d.setDealId(dealId);
      d.setStatus(DealEnum.MIDDLE_DEAL);
      d.setUserId("dummy");
      d.setBuildingId(100L);
      d.setSaleType(saleType);
      return d;
    }

    /* -------- DealMapper 메서드 구현 -------- */
    @Override
    public DealWithSaleType findWithType(Long id) {          // 테스트 대상
      return id.equals(dealId) ? build() : null;
    }

    /* 나머지 메서드는 테스트에 필요 없으므로 기본 구현 */
    @Override
    public List<DealWithChatRoom> getAllWaitingList(String userId) {
      return Collections.emptyList();
    }

    @Override
    public int deleteDealById(Long dealId) {
      return 0;
    }

    @Override
    public int patchStatus(Status status) {
      return 0;
    }

    @Override
    public String getStatusByDealId(Long dealId) {
      return "";
    }

    @Override
    public List<DealWithChatRoom> getPurchaseWaitingList(String userId) {
      return List.of();
    }

    @Override
    public List<DealWithChatRoom> getOnSaleWaitingList(String userId) {
      return List.of();
    }


  }

  @BeforeEach
  void setUp() {
    long dealId = 42L;                 // 공통 ID
    SaleType type = SaleType.TRADING;    // 기본값 (매매)

    DealMapper stubMapper = new StubDealMapper(dealId, type);
    ContractService service = new ContractServiceImpl(stubMapper);
    DealController ctl = new DealController(null, null, service);

    mockMvc = MockMvcBuilders.standaloneSetup(ctl).build();
  }

  @Test
  @DisplayName("매매_거래는_sale_contract_pdf_링크를_반환한다")
  void saleContractTest() throws Exception {
    mockMvc.perform(get("/deal/consumer/contract/{dealId}/download", 42))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.url")
            .value("http://localhost/contracts/sale_contract.pdf"));
  }

  @Test
  @DisplayName("월세_거래는_lease_contract_pdf_링크를_반환한다")
  void leaseContractTest() throws Exception {
    // ↙ 새로운 stub으로 월세 값 주입
    DealMapper stub = new StubDealMapper(77L, SaleType.MONTHLY);
    ContractService svc = new ContractServiceImpl(stub);
    DealController ctl = new DealController(null, null, svc);
    mockMvc = MockMvcBuilders.standaloneSetup(ctl).build();

    mockMvc.perform(get("/deal/consumer/contract/{dealId}/download", 77))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.url")
            .value("http://localhost/contracts/lease_contract.pdf"));
  }
}

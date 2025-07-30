package bjs.zangbu.deal.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import bjs.zangbu.global.config.RootConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(classes = RootConfig.class)
@Transactional
class DealMapperTest {


  private DealMapper dealMapper;

  @Test
  @DisplayName("getAllWaitingList")
  void getAllWaitingList() {
    String memberId = "user01e296@mail.com";
    List<DealWithChatRoom> waitingList = dealMapper.getAllWaitingList(memberId);
    assertNotNull(waitingList);
  }

  @Test
  void deleteDealById() {
  }

  @Test
  void patchStatus() {
  }

  @Test
  void getStatusByDealId() {
  }

  @Test
  void getPurchaseWaitingList() {
  }

  @Test
  void getOnSaleWaitingList() {
  }

  @Test
  void findWithType() {
  }

  @Test
  void selectTodayTradedBuildingIds() {
  }

  @Test
  void createDeal() {
  }

  @Test
  void getBuildingIdByDealId() {
  }
}
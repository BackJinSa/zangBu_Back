package bjs.zangbu.deal.service;

import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.deal.dto.response.DealResponse.Notice;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingList;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingListElement;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingListPurchase;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.deal.vo.Deal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

  private final DealMapper dealMapper;
  private final BuildingMapper buildingMapper;

  @Override
  public Notice getNotice(Long buildingId) {
    // buildMapper에서 Building 조회
    Building buildVO = buildingMapper.getBuildingById(buildingId);

    return Notice.toDto(buildingId, buildVO);
  }

  @Override
  public WaitingList getAllWaitingList(String userId, String nickname) {
    List<DealWithChatRoom> deals = dealMapper.getWaitingDealsWithChatRoom(userId);
    return WaitingList.toDto(deals, nickname);
  }

  @Override
  public WaitingListPurchase getPurchaseWaitingList(String userId) {
    List<Deal> dealVOList = dealMapper.getAllDealByUserId(userId);
    WaitingListPurchase response = WaitingListPurchase.toDto(dealVOList);
    return response;
  }

  @Override
  public List<WaitingListElement> getOnSaleWaitingList(String userId) {
    return List.of();
  }

  @Override
  public boolean deleteDealById(Long dealId) {
    return false;
  }

  public boolean isDealer(String userNickname, String nickname) {
    return userNickname.equals(nickname);
  }

}

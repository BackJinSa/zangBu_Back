package bjs.zangbu.deal.service;

import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import bjs.zangbu.deal.dto.request.DealRequest.Status;
import bjs.zangbu.deal.dto.response.DealResponse.CreateResult;
import bjs.zangbu.deal.dto.response.DealResponse.Notice;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingList;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.deal.vo.DealEnum;
import bjs.zangbu.imageList.service.ImageListService;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

  private final DealMapper dealMapper;
  private final BuildingMapper buildingMapper;
  private final ImageListService imageListService;

  // 거래 전 안내
  @Override
  public Notice getNotice(Long buildingId) {
    // buildMapper 에서 Building 조회
    Building buildVO = buildingMapper.getBuildingById(buildingId);

    return Notice.toDto(buildingId, buildVO);
  }

  // 거래중인 list 모두 조회
  @Override
  public WaitingList getAllWaitingList(String userId, String nickname) {
    List<DealWithChatRoom> deals = dealMapper.getAllWaitingList(userId);
    return buildWaitingList(deals, nickname);

  }

  // 구매 중인 매물 조회
  @Override
  public WaitingList getPurchaseWaitingList(String userId, String nickname) {
    List<DealWithChatRoom> deals = dealMapper.getPurchaseWaitingList(userId);
    return buildWaitingList(deals, nickname);

  }

  // 판매중인 매물 조회
  @Override
  public WaitingList getOnSaleWaitingList(String userId, String nickname) {
    List<DealWithChatRoom> deals = dealMapper.getOnSaleWaitingList(userId);
    return buildWaitingList(deals, nickname);
  }

  // 상위 이미지 포함해서 dto 생성
  private WaitingList buildWaitingList(List<DealWithChatRoom> deals, String nickname) {
    PageInfo<DealWithChatRoom> pageInfo = new PageInfo<>(deals);

    // image map 생성
    Map<Long, String> imageMap = deals.stream()
        .map(DealWithChatRoom::getBuildingId)
        .distinct()
        .collect(Collectors.toMap(
            Function.identity(),
            imageListService::representativeImage
        ));

    return WaitingList.toDto(pageInfo, nickname, imageMap);
  }

  // Deal 삭제 메서드
  @Override
  public boolean deleteDealById(Long dealId) {
    return dealMapper.deleteDealById(dealId) == 1;
  }

  // 상태 변환 메서드
  @Override
  public boolean patchStatus(Status status) {
    // 이전 상태
    String from = status.getStatus();
    // 바꿀 상태
    String to = dealMapper.getStatusByDealId(status.getDealId());
    // 상태 FLOW 가 맞는 지 체크
    if (checkStatus(from, to)) {
      // status PATCH
      return dealMapper.patchStatus(status) == 1;
    } else {
      return false;
    }
  }

  // 거래 생성 (생성된 dealId 반환)
  @Override
  public Long createDeal(String chatRoomId) {
    CreateResult result = new CreateResult();
    dealMapper.createDeal(chatRoomId, result);
    return result.getDealId();
  }

  // 이전 상태에서 다음 상태 이어지는 게 맞는지 체크
  /*
     [거래 전] -> 채팅 시작 ->
     [판매자 수락 전] -> 판매자 수락 ->
     [구매자 수락 전] -> 구매자 수락 ->
     [거래 중] -> 거래 완료 ->
     [거래 성사]
   */
  private static final Map<DealEnum, DealEnum> validTransitions = Map.of(
      DealEnum.BEFORE_TRANSACTION, DealEnum.BEFORE_OWNER,
      DealEnum.BEFORE_OWNER, DealEnum.BEFORE_CONSUMER,
      DealEnum.BEFORE_CONSUMER, DealEnum.MIDDLE_DEAL,
      DealEnum.MIDDLE_DEAL, DealEnum.CLOSE_DEAL
  );

  private boolean checkStatus(String from, String to) {
    try {
      DealEnum fromEnum = DealEnum.valueOf(from);
      DealEnum toEnum = DealEnum.valueOf(to);
      // FLOW 가 맞는 지 체크
      return validTransitions.getOrDefault(fromEnum, null) == toEnum;
    } catch (IllegalArgumentException | NullPointerException e) {
      return false;
    }
  }


}

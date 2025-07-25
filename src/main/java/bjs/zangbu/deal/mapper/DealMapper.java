package bjs.zangbu.deal.mapper;

import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import bjs.zangbu.deal.dto.join.DealWithSaleType;
import bjs.zangbu.deal.dto.request.DealRequest.Status;
import bjs.zangbu.deal.dto.response.DealResponse.CreateResult;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DealMapper {

  /**
   * 거래중인 list 모두 조회
   */
  List<DealWithChatRoom> getAllWaitingList(String userId);

  /**
   * Deal 삭제
   */
  int deleteDealById(Long dealId);

  /**
   * Deal.status
   */
  int patchStatus(Status status);

  /**
   * Deal.status 가져오기
   */
  String getStatusByDealId(Long dealId);

  /**
   * 구매중인 list 모두 조회
   */
  List<DealWithChatRoom> getPurchaseWaitingList(String userId);

  /**
   * 판매중인 list 모두 조회
   */
  List<DealWithChatRoom> getOnSaleWaitingList(String userId);

  /**
   * 표준계약서 xml 코드
   */
  DealWithSaleType findWithType(Long dealId);

  /**
   * 오늘 거래된 매물들의 building_id 조회
   */
  List<Long> selectTodayTradedBuildingIds();

  /**
   * 거래 신규 생성
   */
  int createDeal(@Param("chatRoomId") String chatRoomId,
      @Param("result") CreateResult result);  // insert count 반환됨

  /**
   * dealId로 buildingId 조회
   */
  Long getBuildingIdByDealId(Long dealId);
}

package bjs.zangbu.deal.mapper;

import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import bjs.zangbu.deal.dto.request.DealRequest.Status;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DealMapper {

  List<DealWithChatRoom> getAllWaitingList(String userId);

  int deleteDealById(Long dealId);

  int patchStatus(Status status);

  String getStatusByDealId(Long dealId);

  List<DealWithChatRoom> getPurchaseWaitingList(String userId);

  List<DealWithChatRoom> getOnSaleWaitingList(String userId);
}

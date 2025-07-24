package bjs.zangbu.deal.mapper;

import bjs.zangbu.deal.vo.Deal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DealMapper {

  List<DealWithChatRoom> getWaitingDealsWithChatRoom(String userId);


  List<Deal> getAllDealByUserId(String userId);
}

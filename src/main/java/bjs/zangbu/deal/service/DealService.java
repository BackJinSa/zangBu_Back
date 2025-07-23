package bjs.zangbu.deal.service;

import bjs.zangbu.deal.dto.response.DealResponse.Notice;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingList;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingListElement;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingListPurchase;
import java.util.List;

public interface DealService {

  Notice getNotice(Long buildingId);

  WaitingList getAllWaitingList(String userId, String nickname);

  WaitingListPurchase getPurchaseWaitingList(String userId);

  List<WaitingListElement> getOnSaleWaitingList(String userId);

  boolean deleteDealById(Long dealId);
}

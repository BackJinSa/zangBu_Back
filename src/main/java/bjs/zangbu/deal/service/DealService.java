package bjs.zangbu.deal.service;

import bjs.zangbu.deal.dto.request.DealRequest.Status;
import bjs.zangbu.deal.dto.response.DealResponse.Notice;
import bjs.zangbu.deal.dto.response.DealWaitingListResponse.WaitingList;

public interface DealService {

  // 거래 전 안내
  Notice getNotice(Long buildingId);

  // 거래중인 list 모두 조회
  WaitingList getAllWaitingList(String memberId, String nickname);

  // 구매 중인 매물 조회
  WaitingList getPurchaseWaitingList(String memberId, String nickname);

  // 판매중인 매물 조회
  WaitingList getOnSaleWaitingList(String memberId, String nickname);

  // Deal 삭제 메서드
  boolean deleteDealById(Long dealId);

  // 상태 변환 메서드
  boolean patchStatus(Status status);

  // 거래 생성
  Long createDeal(String chatRoomId);
}

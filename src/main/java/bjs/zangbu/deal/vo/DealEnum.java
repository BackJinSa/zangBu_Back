package bjs.zangbu.deal.vo;

public enum DealEnum {

  // [거래 전] -> 채팅 시작 ->
  // [판매자 수락 전] -> 판매자 수락 ->
  // [구매자 수락 전] -> 구매자 수락 ->
  // [거래 중] -> 거래 완료 ->
  // [거래 성사]

  BEFORE_TRANSACTION, // 거래 전
  BEFORE_OWNER, // 판매자 수락 전
  BEFORE_CONSUMER, // 구매자 수락 전
  MIDDLE_DEAL, // 거래 중
  CLOSE_DEAL // 거래 성사
}

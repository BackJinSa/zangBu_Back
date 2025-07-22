package bjs.zangbu.deal.vo;

public enum DealEnum {
     BEFORE_TRANSACTION, // 거래 전
    BEFORE_OWNER, // 판매자 수락 전
    BEFORE_CONSUMER, // 구매자 수락 전
    MIDDLE_DEAL, // 거래 중
    CLOSE_DEAL // 거래 성사
}

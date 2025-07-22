package bjs.zangbu.deal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class DealWaitingListResponse {

    // /deal/waiting Response Element
    // /deal/waitinglist/purchase Response Element
    // /deal/waitinglist/onsale Response Element
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitingListElement{

        private Long buildingId; // building 식별 id
        private int price; // 매물 가격
        private String buildingName; // 매물 이름
        private String houseType; // 부동산 유형 ('APARTMENT', 'OFFICETEL', 'VILLA', 'HOUSE')
        private String saleType; // 매매, 전세, 월세 구분 ('MONTHLY', 'CHARTER', 'TRADING')
        private String imageUrl; // 매물 이미지 링크
        private String address; // 매물 주소
        private String dealStatus; // 거래 상태(구매 중, 판매 중)
    }

    // /deal/waiting Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitingList{
        private List<WaitingListElement> allDeals; // WaitingListElement 를 갖는 리스트
    }

    // /deal/waitinglist/purchase Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitingListPurchase{
        private List<WaitingListElement> activeDeals; // WaitingListElement 를 갖는 리스트
    }

    // /deal/waitinglist/onsale Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitingListOnSale{
        private List<WaitingListElement> availableDeals; // WaitingListElement 를 갖는 리스트
    }

}


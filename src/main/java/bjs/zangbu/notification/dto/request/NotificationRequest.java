package bjs.zangbu.notification.dto.request;

import bjs.zangbu.notification.vo.SaleType;
import bjs.zangbu.notification.vo.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationRequest {

    /**
     * 1. 시세 변동 감지용 알림 요청 Request
     * type: BUILDING
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceChangedRequest {
        private Long buildingId;    // 빌딩id
        private String memberId;      // 유저id
        private int price;          // 시세
        private String address;     // 매물 주소
        private Type type = Type.BUILDING; // 알림 유형 BUILDING 고정(시세 변동 감지)
        private SaleType saleType;    // 거래 유형 ex) 월세, 전세, 매매
    }

    /**
     * 2. 실거래 발생 감지용 알림 요청 Request
     * type: TRADE
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TradeHappenedRequest {
        private Long buildingId;    // 빌딩id
        private String memberId;      // 유저id
        private int price;          // 시세
        private String address;     // 매물 주소
        private Type type = Type.TRADE; // 알림 유형 TRADE 고정(실거래 발생 감지)
        private SaleType saleType;    // 거래 유형 ex) 월세, 전세, 매매
    }

    /**
     * 3. 리뷰 등록 감지용 알림 요청 Request
     * type: REVIEW
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewPostedRequest {
        private Long buildingId;    // 빌딩id
        private String memberId;    // 유저id
        private int rank;           // 리뷰 평점
        private String address;     // 매물 주소
        private Type type = Type.REVIEW; // 알림 유형 REVIEW 고정(리뷰 등록 감지)
        private SaleType saleType;    // 거래 유형 ex) 월세, 전세, 매매
    }
}

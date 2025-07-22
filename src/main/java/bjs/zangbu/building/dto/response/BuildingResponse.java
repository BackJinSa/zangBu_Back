package bjs.zangbu.building.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class BuildingResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViewDetailResponse {
        // 사용자 정보 (멤버십 등)
        private User user;
        // 등록일 (YYYY-MM-DD 등 문자열)
        private String resFixedDate;
        // 거래 유형 (예: 매매, 전세, 월세)
        private String resType;
        // 단지명, 건물 이름
        private String resComplexName;
        // 도로명 주소
        private String commAddrRoadName;
        // 지번 주소
        private String commAddrLotNumber;
        // 동 수(동 개수)
        private String resDongCnt;
        // 전체 세대 수 등, 건물의 구성(매물) 수
        private String resCompositionCnt;
        // 건물 준공 승인일
        private String resApprovalDate;
        // 난방 방식
        private String resHeatingSystem;
        // 제공 시설(엘리베이터, 주차장 등)
        private String resFacility;
        // 부동산 종류 및 정보
        private String resRealty;
        // 부동산 연락처
        private String resTelNo;
        // 이미지(썸네일)
        private String resImageLink;
        // 면적별 가격 정보 리스트
        private List<ResAreaPrice> resAreaPriceList;
        // 한 줄 소개
        private String infoOneline;
        // 제목
        private String title;
        // 상세 설명
        private String infoBuilding;
        // 썸네일/대표 이미지 링크
        private String imageUrl;
        // 연락 담당자 이름
        private String contactName;
        // 연락 담당자 전화번호
        private String contactPhone;
        // 리뷰 목록(사용자 후기)
        private List<Review> review;
        // 사용자가 북마크했는지 여부
        private boolean isBookmarked;
        // 알림 설정 여부
        private boolean isNotification;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        // 가입일 또는 프리미엄 시작일
        private LocalDateTime membershipDate;
        // 사용자 식별 토큰
        private String token;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResAreaPrice {
        // 전용/공급 면적
        private String resArea;
        // 표기용 면적 명칭
        private String resArea1;
        // 구성(세대) 수
        private String resCompositionCnt;
        // 해당 면적의 층 정보
        private String resFloor;
        // 하위 평균 가격
        private String resLowerAveragePrice;
        // 상위 평균 가격
        private String resTopAveragePrice;
        // 부가 하위 평균 가격
        private String resLowerAveragePrice1;
        // 부가 상위 평균 가격
        private String resTopAveragePrice1;
        // 보증금 금액
        private String resSuretyAmt;
        // 월세 금액
        private String resMonthlyRent;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Review {
        // 리뷰 고유 ID
        private String reviewId;
        // 작성자 닉네임
        private String reviewerNickname;
        // 평점 (예: 4, 4.5)
        private String rank;
        // 리뷰 내용
        private String content;
        // 리뷰 작성일자(문자열)
        private String createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilteredResponse {
        // 매물 목록
        private List<Filtered> filtered;
        // 다음 페이지 존재 여부 (true: 다음 페이지 있음)
        private boolean hasNext;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filtered {
        // 매물 고유 ID
        private String buildingId;
        // 건물명/단지명
        private String buildingName;
        // 가격 (원 단위)
        private Integer price;
        // 사용자 평균 평점
        private Integer rankAverage;
        // 사용자가 북마크 했는지 여부
        private boolean isBookMarked;
    }
}

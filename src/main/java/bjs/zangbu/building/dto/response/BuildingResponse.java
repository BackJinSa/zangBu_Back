package bjs.zangbu.building.dto.response;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 건물 관련 응답 DTO 모음 클래스
 */
public class BuildingResponse {

    /**
     * 매물 상세 조회 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViewDetailResponse {
        private User user;                      // 사용자 정보 (멤버십 등)
        private String resFixedDate;            // 등록일 (YYYY-MM-DD 등 문자열)
        private String resType;                 // 거래 유형 (매매, 전세, 월세 등)
        private String resComplexName;          // 단지명, 건물 이름
        private String commAddrRoadName;        // 도로명 주소
        private String commAddrLotNumber;       // 지번 주소
        private String resDongCnt;              // 동 수(동 개수)
        private String resCompositionCnt;       // 전체 세대 수 등, 건물 구성(매물) 수
        private String resApprovalDate;         // 건물 준공 승인일
        private String resHeatingSystem;        // 난방 방식
        private String resFacility;              // 제공 시설(엘리베이터, 주차장 등)
        private String resRealty;                // 부동산 종류 및 정보
        private String resTelNo;                 // 부동산 연락처
        private String resImageLink;             // 이미지(썸네일)
        private List<ResAreaPrice> resAreaPriceList;  // 면적별 가격 정보 리스트
        private String infoOneline;              // 한 줄 소개
        private String title;                    // 제목
        private String infoBuilding;             // 상세 설명
        private String imageUrl;                 // 썸네일/대표 이미지 링크
        private String contactName;              // 연락 담당자 이름
        private String contactPhone;             // 연락 담당자 전화번호
        private List<Review> review;             // 리뷰 목록(사용자 후기)
        private boolean isBookmarked;            // 사용자가 북마크했는지 여부
        private boolean isNotification;          // 알림 설정 여부

        /**
         * 사용자 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class User {
            private LocalDateTime membershipDate;   // 가입일 또는 프리미엄 시작일
            private String token;                   // 사용자 식별 토큰
        }

        /**
         * 면적별 가격 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ResAreaPrice {
            private String resArea;                 // 전용/공급 면적
            private String resArea1;                // 표기용 면적 명칭
            private String resCompositionCnt;      // 구성(세대) 수
            private String resFloor;                // 해당 면적의 층 정보
            private String resLowerAveragePrice;   // 하위 평균 가격
            private String resTopAveragePrice;     // 상위 평균 가격
            private String resLowerAveragePrice1;  // 부가 하위 평균 가격
            private String resTopAveragePrice1;    // 부가 상위 평균 가격
            private String resSuretyAmt;            // 보증금 금액
            private String resMonthlyRent;          // 월세 금액
        }

        /**
         * 리뷰 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Review {
            private String reviewId;                // 리뷰 고유 ID
            private String reviewerNickname;       // 작성자 닉네임
            private String rank;                   // 평점 (예: 4, 4.5)
            private String content;                // 리뷰 내용
            private String createdAt;              // 리뷰 작성일자 (문자열)
        }
    }

    /**
     * 필터링된 매물 목록 응답 DTO (페이징 포함)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilteredResponse {
        private List<Filtered> content;    // 매물 목록
        private int pageNum;               // 현재 페이지 번호
        private int pageSize;              // 페이지 크기
        private long total;                // 전체 매물 수
        private int pages;                 // 전체 페이지 수

        /**
         * PageInfo 객체를 DTO로 변환
         * @param pageInfo PageInfo 객체
         * @return FilteredResponse DTO
         */
        public static FilteredResponse toDto(PageInfo<Filtered> pageInfo) {
            return new FilteredResponse(
                    pageInfo.getList(),
                    pageInfo.getPageNum(),
                    pageInfo.getPageSize(),
                    pageInfo.getTotal(),
                    pageInfo.getPages()
            );
        }

        /**
         * 필터링된 매물 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Filtered {
            private Long buildingId;
            private String buildingName;
            private Integer price;
            private Float rankAverage;
            private boolean isBookMarked;
        }
    }
}

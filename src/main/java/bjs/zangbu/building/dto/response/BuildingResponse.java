package bjs.zangbu.building.dto.response;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

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
    @Schema(description = "매물 상세 조회 응답 DTO")
    public static class ViewDetailResponse {
        @Schema(description = "사용자 정보(멤버십 등)")
        private User user;                      // 사용자 정보 (멤버십 등)
        @Schema(description = "등록일 (YYYY-MM-DD 등 문자열)", example = "2024-07-16")
        private String resFixedDate;            // 등록일 (YYYY-MM-DD 등 문자열)
        @Schema(description = "거래 유형 (매매, 전세, 월세 등)", example = "전세")
        private String resType;                 // 거래 유형 (매매, 전세, 월세 등)
        @Schema(description = "단지명 또는 건물 이름", example = "래미안 SQ2")
        private String resComplexName;          // 단지명, 건물 이름
        @Schema(description = "도로명 주소", example = "서울시 강남구 테헤란로 110")
        private String commAddrRoadName;        // 도로명 주소
        @Schema(description = "지번 주소", example = "서울시 강남구 역삼동 611-1")
        private String commAddrLotNumber;       // 지번 주소
        @Schema(description = "동 수(동 개수)", example = "5")
        private String resDongCnt;              // 동 수(동 개수)
        @Schema(description = "전체 세대 수 등, 건물 구성(매물) 수", example = "250")
        private String resCompositionCnt;       // 전체 세대 수 등, 건물 구성(매물) 수
        @Schema(description = "건물 준공 승인일", example = "2022-09-15")
        private String resApprovalDate;         // 건물 준공 승인일
        @Schema(description = "난방 방식", example = "지역난방")
        private String resHeatingSystem;        // 난방 방식
        @Schema(description = "제공 시설(엘리베이터, 주차장 등)", example = "엘리베이터, 주차장, CCTV, 헬스장")
        private String resFacility;             // 제공 시설(엘리베이터, 주차장 등)
        @Schema(description = "부동산 종류 및 정보", example = "아파트")
        private String resRealty;               // 부동산 종류 및 정보
        @Schema(description = "부동산 연락처", example = "02-555-1234")
        private String resTelNo;                // 부동산 연락처
        @Schema(description = "이미지(썸네일)", example = "https://mycdn.com/img/abc.jpg")
        private String resImageLink;            // 이미지(썸네일)
        @Schema(description = "면적별 가격 정보 리스트")
        private List<ResAreaPrice> resAreaPriceList;  // 면적별 가격 정보 리스트
        @Schema(description = "한 줄 소개", example = "신축 역세권 아파트, 가성비 우수")
        private String infoOneline;             // 한 줄 소개
        @Schema(description = "제목", example = "역삼역 도보 3분, 올리모델링 전세")
        private String title;                   // 제목
        @Schema(description = "상세 설명", example = "2023년 입주, 5층, 남향, 주변 완비")
        private String infoBuilding;            // 상세 설명
        @Schema(description = "썸네일/대표 이미지 링크", example = "https://mycdn.com/detail/123.jpg")
        private String imageUrl;                // 썸네일/대표 이미지 링크
        @Schema(description = "연락 담당자 이름", example = "박부동")
        private String contactName;             // 연락 담당자 이름
        @Schema(description = "연락 담당자 전화번호", example = "010-1234-5678")
        private String contactPhone;            // 연락 담당자 전화번호
        @Schema(description = "리뷰 목록(사용자 후기)")
        private List<Review> review;            // 리뷰 목록(사용자 후기)
        @Schema(description = "사용자가 북마크했는지 여부", example = "false")
        private boolean isBookmarked;           // 사용자가 북마크했는지 여부
        @Schema(description = "알림 설정 여부", example = "true")
        private boolean isNotification;         // 알림 설정 여부

        /**
         * 사용자 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "매물 상세 조회 - 사용자 정보 DTO")
        public static class User {
            @Schema(description = "가입일 또는 프리미엄 시작일", example = "2023-04-10T10:15:00")
            private LocalDateTime membershipDate;   // 가입일 또는 프리미엄 시작일
            @Schema(description = "사용자 식별 토큰", example = "user_abc123token")
            private String token;                   // 사용자 식별 토큰
        }

        /**
         * 면적별 가격 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "매물 상세 조회 - 면적별 가격 정보 DTO")
        public static class ResAreaPrice {
            @Schema(description = "전용/공급 면적", example = "59.7㎡")
            private String resArea;                 // 전용/공급 면적
            @Schema(description = "표기용 면적 명칭", example = "24평")
            private String resArea1;                // 표기용 면적 명칭
            @Schema(description = "구성(세대) 수", example = "20")
            private String resCompositionCnt;       // 구성(세대) 수
            @Schema(description = "해당 면적의 층 정보", example = "5층")
            private String resFloor;                // 해당 면적의 층 정보
            @Schema(description = "하위 평균 가격", example = "6.5억")
            private String resLowerAveragePrice;    // 하위 평균 가격
            @Schema(description = "상위 평균 가격", example = "7억")
            private String resTopAveragePrice;      // 상위 평균 가격
            @Schema(description = "부가 하위 평균 가격", example = "6.3억")
            private String resLowerAveragePrice1;   // 부가 하위 평균 가격
            @Schema(description = "부가 상위 평균 가격", example = "6.8억")
            private String resTopAveragePrice1;     // 부가 상위 평균 가격
            @Schema(description = "보증금 금액", example = "1000만원")
            private String resSuretyAmt;            // 보증금 금액
            @Schema(description = "월세 금액", example = "60")
            private String resMonthlyRent;          // 월세 금액
        }

        /**
         * 리뷰 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "매물 상세 조회 - 리뷰 정보 DTO")
        public static class Review {
            @Schema(description = "리뷰 고유 ID", example = "revw_1004")
            private String reviewId;                // 리뷰 고유 ID
            @Schema(description = "작성자 닉네임", example = "리뷰왕")
            private String reviewerNickname;        // 작성자 닉네임
            @Schema(description = "평점 (예: 4, 4.5)", example = "4.5")
            private String rank;                    // 평점 (예: 4, 4.5)
            @Schema(description = "리뷰 내용", example = "매우 깨끗해서 마음에 들었습니다.")
            private String content;                 // 리뷰 내용
            @Schema(description = "리뷰 작성일자 (문자열)", example = "2024-07-12")
            private String createdAt;               // 리뷰 작성일자 (문자열)
        }
    }

    /**
     * 필터링된 매물 목록 응답 DTO (페이징 포함)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "필터링된 매물 목록(페이징 포함) 응답 DTO")
    public static class FilteredResponse {
        @Schema(description = "매물 목록")
        private List<Filtered> content;    // 매물 목록
        @Schema(description = "현재 페이지 번호", example = "1")
        private int pageNum;               // 현재 페이지 번호
        @Schema(description = "페이지 크기", example = "20")
        private int pageSize;              // 페이지 크기
        @Schema(description = "전체 매물 수", example = "154")
        private long total;                // 전체 매물 수
        @Schema(description = "전체 페이지 수", example = "8")
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
        @Schema(description = "필터링된 매물 정보 DTO")
        public static class Filtered {
            @Schema(description = "건물 ID", example = "10001")
            private Long buildingId;
            @Schema(description = "건물명", example = "래미안 SQ2")
            private String buildingName;
            @Schema(description = "매매/전세/월세 가격", example = "90000000")
            private Integer price;
            @Schema(description = "평균 평점", example = "4.2")
            private Float rankAverage;
            @Schema(description = "사용자 북마크 여부", example = "false")
            private boolean isBookMarked;
        }
    }
}

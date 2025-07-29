package bjs.zangbu.building.dto.request;

import bjs.zangbu.building.vo.Building;
import bjs.zangbu.building.vo.PropertyType;
import bjs.zangbu.building.vo.SellerType;
import bjs.zangbu.complexList.vo.ComplexList;
import bjs.zangbu.imageList.vo.ImageList;
import bjs.zangbu.notification.vo.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 건물 관련 요청 DTO를 모아둔 클래스.
 * 내부 클래스 형태로 매물 상세 조회, 찜 요청, 등록 요청에 사용되는 DTO들을 정의함.
 */
public class BuildingRequest {

    /**
     * 매물 상세 조회 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViewDetailRequest {
        /** 조회할 건물 ID (필수) */
        private Long buildingId;
        /** 단지 번호 (옵션) */
        private String complexNo;
        /** 동 정보 (옵션) */
        private String dong;
        /** 호 정보 (옵션) */
        private String ho;
    }

    /**
     * 매물 찜 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkRequest {
        /** 북마크할 건물 ID */
        private Long buildingId;
    }

    /**
     * 매물 등록 요청 DTO
     * 내부에 건물 정보, 단지 정보, 이미지 정보 DTO를 포함한다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class SaleRegistrationRequest {
        /** 등록할 건물 정보 */
        private BuildingDetails building;
        /** 단지 정보 */
        private ComplexDetails complexList;
        /** 대표 이미지 정보 */
        private ImageDetails image;

        /**
         * 건물 상세 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BuildingDetails {
            private String sellerNickname;        // 판매자 닉네임
            private SaleType saleType;            // 매물 거래 유형 (매매, 전세 등)
            private Integer price;                // 매매가 또는 전세가
            private Long deposit;                 // 보증금 (전세 또는 월세일 경우)
            private Integer bookmarkCount;        // 초기 찜 횟수 (기본값 0)

            private String buildingName;          // 건물명
            private SellerType sellerType;        // 판매자 유형 (예: 개인, 중개업자)
            private PropertyType propertyType;    // 부동산 유형 (예: 아파트, 오피스텔 등)
            private LocalDateTime moveDate;       // 입주 가능일
            private String infoOneline;           // 한 줄 요약 정보
            private String infoBuilding;          // 상세 건물 정보 설명
            private String contactName;           // 연락 담당자 이름
            private String contactPhone;          // 연락처 전화번호
            private String facility;              // 편의시설 및 주변 시설 정보

            /**
             * DTO → VO 변환 메서드
             * @param request BuildingDetails DTO
             * @param complexId 단지 ID
             * @param memberId 등록자 ID
             * @return Building VO 객체
             */
            public static Building toVo(BuildingDetails request, Long complexId, String memberId) {
                return new Building(
                        null, // buildingId는 DB에서 자동 생성
                        request.getSellerNickname(),
                        request.getSaleType(),
                        request.getPrice(),
                        request.getDeposit(),
                        request.getBookmarkCount(),
                        null, // 등록일 등은 서버에서 설정
                        request.getBuildingName(),
                        request.getSellerType(),
                        request.getPropertyType(),
                        request.getMoveDate(),
                        request.getInfoOneline(),
                        request.getInfoBuilding(),
                        request.getContactName(),
                        request.getContactPhone(),
                        request.getFacility(),
                        memberId,
                        complexId
                );
            }
        }

        /**
         * 단지(복합단지) 상세 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ComplexDetails {
            private String resType;           // 주거 유형 (예: 아파트, 오피스텔)
            private String complexName;       // 단지명
            private Integer complexNo;        // 단지 번호 (외부 시스템 기준)
            private String sido;              // 시/도
            private String sigungu;           // 시/군/구
            private String siCode;            // 행정구역 코드
            private String eupmyeondong;      // 읍/면/동
            private String transactionId;     // Codef 트랜잭션 ID
            private String address;           // 기본 주소
            private String zonecode;          // 우편번호
            private String buildingName;      // 건물명
            private String bname;             // 법정동명
            private String dong;              // 동
            private String ho;                // 호수

            /**
             * DTO → VO 변환 메서드
             * @param request ComplexDetails DTO
             * @return ComplexList VO 객체
             */
            public static ComplexList toVo(ComplexDetails request) {
                return new ComplexList(
                        null, // complexId는 DB에서 자동 생성
                        request.getResType(),
                        request.getComplexName(),
                        request.getComplexNo(),
                        request.getSido(),
                        request.getSigungu(),
                        request.getSiCode(),
                        request.getEupmyeondong(),
                        request.getTransactionId(),
                        request.getAddress(),
                        request.getZonecode(),
                        request.getBuildingName(),
                        request.getBname(),
                        request.getDong(),
                        request.getHo()
                );
            }
        }

        /**
         * 이미지 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ImageDetails {
            private String imageUrl;          // 이미지 URL

            /**
             * DTO → VO 변환 메서드
             * @param request ImageDetails DTO
             * @param complexId 단지 ID
             * @param memberId 회원 ID
             * @param buildingId 건물 ID
             * @return ImageList VO 객체
             */
            public static ImageList toVo(ImageDetails request, Long complexId, String memberId, Long buildingId) {
                return new ImageList(
                        null,           // imageId는 DB에서 자동 생성
                        buildingId,     // 연결된 건물 ID
                        memberId,       // 등록자 ID
                        complexId,      // 연결된 단지 ID
                        request.getImageUrl() // 이미지 경로(URL)
                );
            }
        }
    }
}

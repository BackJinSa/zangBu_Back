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
 * 건물 관련 요청 DTO를 모아둔 클래스
 * 내부에 조회, 찜, 등록 관련 요청 DTO 포함
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
     * 내부에 건물 상세, 단지 상세, 이미지 정보 DTO 포함
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class SaleRegistrationRequest {
        /** 등록할 건물 상세 정보 DTO */
        private BuildingDetails building;
        /** 단지(복합단지) 상세 정보 DTO */
        private ComplexDetails complexList;
        /** 이미지 정보 DTO */
        private ImageDetails image;

        /**
         * 건물 상세 정보 DTO
         */
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BuildingDetails {
            private String sellerNickname;        // 판매자 닉네임
            private SaleType saleType;            // 매물 거래 타입 (전세, 매매 등)
            private Integer price;                // 가격
            private Long deposit;                 // 보증금 (전세일 경우)
            private Integer bookmarkCount;        // 찜 개수

            private String buildingName;          // 건물명
            private SellerType sellerType;        // 판매자 유형 (개인, 중개업자 등)
            private PropertyType propertyType;    // 부동산 유형 (아파트, 오피스텔 등)
            private LocalDateTime moveDate;       // 입주 가능일
            private String infoOneline;           // 간단 정보(한줄 설명)
            private String infoBuilding;          // 상세 건물 정보
            private String contactName;           // 연락처 이름
            private String contactPhone;          // 연락처 전화번호
            private String facility;              // 편의시설 정보

            /**
             * DTO -> VO 변환 메서드
             * @param request DTO 객체
             * @param complexId 단지 ID
             * @param memberId 회원 ID
             * @return Building VO 객체
             */
            public static Building toVo(BuildingDetails request, Long complexId, String memberId) {
                return new Building(
                        null, // buildingId는 DB 생성 시 자동 할당
                        request.getSellerNickname(),
                        request.getSaleType(),
                        request.getPrice(),
                        request.getDeposit(),
                        request.getBookmarkCount(),
                        null, // 기타 필드 (예: 등록일 등), 필요 시 추가
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
            private String resType;           // 주거 유형 (예: 아파트, 연립주택 등)
            private String complexName;       // 단지명
            private Integer complexNo;        // 단지 번호
            private String sido;              // 시도
            private String sigungu;           // 시군구
            private String siCode;            // 시 코드
            private String eupmyeondong;      // 읍면동
            private String transactionId;     // 거래 ID
            private String address;           // 주소
            private String zonecode;          // 우편번호
            private String buildingName;      // 건물명
            private String bname;             // 법정동명

            /**
             * DTO -> VO 변환 메서드
             * @param request DTO 객체
             * @return ComplexList VO 객체
             */
            public static ComplexList toVo(ComplexDetails request) {
                return new ComplexList(
                        null, // complexId는 DB 자동 생성
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
                        request.getBname()
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
             * DTO -> VO 변환 메서드
             * @param request DTO 객체
             * @param complexId 단지 ID
             * @param memberId 회원 ID
             * @param buildingId 건물 ID
             * @return ImageList VO 객체
             */
            public static ImageList toVo(ImageDetails request, Long complexId, String memberId, Long buildingId) {
                return new ImageList(
                        null, // imageId는 DB 자동 생성
                        buildingId,
                        memberId,
                        complexId,
                        request.getImageUrl()
                );
            }
        }
    }
}

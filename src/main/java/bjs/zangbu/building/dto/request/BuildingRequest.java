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


public class BuildingRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViewDetailRequest {
        // 조회할 건물 ID (필수)
        private Long buildingId;
        // 검색 구분자 (옵션, 예: 단지/매물 등)
        private String searchGbn;
        // 단지 번호 (옵션)
        private String complexNo;
        // 동 정보 (옵션)
        private String dong;
        // 호 정보 (옵션)
        private String ho;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkRequest {
        // 북마크할 건물 ID
        private Long buildingId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class SaleRegistrationRequest {
        // 등록할 건물 상세 정보 DTO
        private BuildingDetails building;
        // 단지(복합단지) 상세 정보 DTO
        private ComplexDetails complexList;
        // 이미지 정보 DTO
        private ImageDetails image;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BuildingDetails {
            // 판매자 닉네임
            private String sellerNickname;
            // 매물 거래 타입 (ex: 전세, 매매 등)
            private SaleType saleType;
            // 가격
            private Integer price;
            // 보증금 (전세일 경우)
            private Long deposit;
            // 찜 개수
            private Integer bookmarkCount;

            // 건물명
            private String buildingName;
            // 판매자 유형 (개인, 중개업자 등)
            private SellerType sellerType;
            // 부동산 유형 (아파트, 오피스텔 등)
            private PropertyType propertyType;
            // 입주 가능일
            private LocalDateTime moveDate;
            // 간단 정보(한줄 설명)
            private String infoOneline;
            // 상세 건물 정보
            private String infoBuilding;
            // 연락처 이름
            private String contactName;
            // 연락처 전화번호
            private String contactPhone;
            // 편의시설 정보
            private String facility;

            // DTO -> VO 변환 메서드
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

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ComplexDetails {
            // 주거 유형 (예: 아파트, 연립주택 등)
            private String resType;
            // 단지명
            private String complexName;
            // 단지 번호
            private Integer complexNo;
            // 시도
            private String sido;
            // 시군구
            private String sigungu;
            // 시 코드
            private String siCode;
            // 읍면동
            private String eupmyeondong;
            // 거래 ID
            private String transactionId;
            // 주소
            private String address;
            // 우편번호
            private String zonecode;
            // 건물명
            private String buildingName;
            // 법정동명
            private String bname;

            // DTO -> VO 변환 메서드
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

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ImageDetails {
            // 이미지 URL
            private String imageUrl;

            // DTO -> VO 변환 메서드
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

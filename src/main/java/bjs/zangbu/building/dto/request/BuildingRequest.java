package bjs.zangbu.building.dto.request;

import bjs.zangbu.building.vo.PropertyType;
import bjs.zangbu.building.vo.SellerType;
import bjs.zangbu.notification.vo.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;


public class BuildingRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class viewDetailRequest {
        // 조회할 건물 ID
        private Long buildingId;
        // 검색 구분자(옵션, ex: 단지/매물 등)
        private String searchGbn;
        // 단지 번호(옵션)
        private String complexNo;
        // 동 정보(옵션)
        private String dong;
        // 호 정보(옵션)
        private String ho;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class bookmarkRequest {
        // 북마크할 건물 ID
        private Long buildingId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class saleRegistrationRequest {
        // 판매자 유형(일반인, 중개사 등)
        private SellerType sellerType;
        // 거래 유형(매매, 전세, 월세 등)
        private Type tradeType;
        // 매물 가격(원)
        private Integer price;
        // 보증금(원)
        private Long deposit;
        // 부동산 유형(아파트, 오피스텔 등)
        private PropertyType propertyType;
        // 도로명 주소
        private String roadAddress;
        // 상세 주소
        private String detailAddress;
        // 전용면적(㎡ 등, 소수 있음)
        private Float area;
        // 입주 가능일(문자열, ex: 즉시/년-월-일)
        private String avaliableFrom;
        // 입주 가능일(LocalDate, 날짜 데이터로)
        private LocalDateTime moveDate;
        // 가격 협의 여부(true: 협의 가능)
        private boolean negotiable;
        // 한 줄 소개 문구
        private String infoOneline;
        // 건물명
        private String buildingName;
        // 상세 소개
        private String infoBuilding;
        // 이미지 리스트
        private List<Image> images;
        // 담당자 연락처 정보
        private Contact contact;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        // 이미지 URL
        private String imageUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact {
        // 담당자 이름
        private String name;
        // 담당자 전화번호
        private String phone;
    }
}

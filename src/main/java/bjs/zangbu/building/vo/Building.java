package bjs.zangbu.building.vo;

import bjs.zangbu.notification.vo.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    // 건물 ID (고유 식별자)
    private Long buildingId;
    // 판매자 닉네임
    private String sellerNickname;
    // 매물의 판매 유형 (예: 매매, 전세, 월세 등)
    private SaleType saleType;
    // 매물 가격 (단위: 원)
    private Integer price;
    // 보증금 (월세 등 일부 매물에 해당)
    private Long deposit;
    // 이 건물을 북마크한 사용자 수
    private Integer bookmarkCount;
    // 매물 등록 일시
    private LocalDateTime createdAt;
    // 건물 이름 또는 매물명
    private String buildingName;
    // 판매자 유형 (예: 일반인, 공인중개사 등)
    private SellerType sellerType;
    // 부동산 유형 (예: 아파트, 오피스텔, 상가 등)
    private PropertyType propertyType;
    // 입주 가능 날짜
    private LocalDateTime moveDate;
    // 한 줄 소개 문구
    private String infoOneline;
    // 건물 상세 설명
    private String infoBuilding;
    // 연락처 담당자 이름
    private String contactName;
    // 연락처 전화번호
    private String contactPhone;
    // 제공되는 시설 정보 (예: 엘리베이터, 주차장, 보안 등)
    private String facility;
    // 외래키
    // 유저 식별자(UUID)
    private String memberId;
    // 단지 식별자(BIGINT)
    private Long complexId;

}

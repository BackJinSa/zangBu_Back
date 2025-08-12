package bjs.zangbu.building.dto.request;

import bjs.zangbu.building.vo.Building;
import bjs.zangbu.building.vo.PropertyType;
import bjs.zangbu.building.vo.SellerType;
import bjs.zangbu.complexList.vo.ComplexList;
import bjs.zangbu.imageList.vo.ImageList;
import bjs.zangbu.notification.vo.SaleType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

/**
 * 건물 관련 요청 DTO를 모아둔 클래스. 내부 클래스 형태로 매물 상세 조회, 찜 요청, 등록 요청에 사용되는 DTO들을 정의함.
 */
public class BuildingRequest {

  /**
   * 매물 상세 조회 요청 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(description = "매물 상세 조회 요청 DTO")
  public static class ViewDetailRequest {

    /**
     * 조회할 건물 ID (필수)
     */
//         @Schema(description = "조회할 건물 ID", example = "10001", required = true)
    private Long buildingId;
    /**
     * 동 정보 (옵션)
     */
//         @Schema(description = "동 정보 (옵션)", example = "102동")
    private String dong;
    /**
     * 호 정보 (옵션)
     */
//         @Schema(description = "호 정보 (옵션)", example = "702호")
    private String ho;
  }

  /**
   * 매물 찜 요청 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(description = "매물 찜(북마크) 요청 DTO")
  public static class BookmarkRequest {

    /**
     * 북마크할 건물 ID
     */
//         @Schema(description = "북마크할 건물 ID", example = "10001", required = true)
    private Long buildingId;
  }

  /**
   * 매물 등록 요청 DTO 내부에 건물 정보, 단지 정보, 이미지 정보 DTO를 포함한다.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Setter
//     @Schema(description = "매물 등록 요청 DTO. 건물, 단지, 이미지 정보를 모두 포함")
  public static class SaleRegistrationRequest {

    /**
     * 등록할 건물 정보
     */
//         @Schema(description = "등록할 건물 상세 정보")
    private BuildingDetails building;
    /**
     * 단지 정보
     */
//         @Schema(description = "단지(복합단지) 상세 정보")
    private ComplexDetails complexList;
    /**
     * 대표 이미지 정보
     */
//         @Schema(description = "대표 이미지 정보")
    private ImageDetails image;
  }

  /**
   * 건물 상세 정보 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Setter

//         @Schema(description = "등록할 건물(매물) 상세 정보 DTO")
  public static class BuildingDetails {

    //             @Schema(description = "판매자 닉네임", example = "부동산천재")
    private String sellerNickname;        // 판매자 닉네임
    //
//       @Schema(description = "매물 거래 유형 (SALE: 매매, JEONSE: 전세, MONTHLY: 월세)", example = "SALE")
    private SaleType saleType;            // 매물 거래 유형 (매매, 전세 등)
    //
//       @Schema(description = "매매가 또는 전세가", example = "85000000")
    private Integer price;                // 매매가 또는 전세가
    //
//       @Schema(description = "보증금(전세 또는 월세일 경우)", example = "2000000")
    private Long deposit;                 // 보증금 (전세 또는 월세일 경우)
    //
//       @Schema(description = "초기 찜 횟수 (기본값 0)", example = "0")
    private Integer bookmarkCount;        // 초기 찜 횟수 (기본값 0)
    //
//       @Schema(description = "건물명", example = "롯데캐슬아파트")
    private String buildingName;          // 건물명
    //
//       @Schema(description = "판매자 유형 (PERSONAL: 개인, AGENT: 중개업자)", example = "AGENT")
    private SellerType sellerType;        // 판매자 유형 (예: 개인, 중개업자)
    //
//       @Schema(description = "부동산 유형 (APARTMENT, OFFICETEL 등)", example = "APARTMENT")
    private PropertyType propertyType;    // 부동산 유형 (예: 아파트, 오피스텔 등)
    //
//       @Schema(description = "입주 가능일 (yyyy-MM-ddTHH:mm:ss)", example = "2025-09-01T00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime moveDate;       // 입주 가능일
    //
//       @Schema(description = "한 줄 요약 정보", example = "역세권 신축 풀옵션")
    private String infoOneline;           // 한 줄 요약 정보
    //
//       @Schema(description = "상세 건물 정보 설명", example = "남향, 2023년 준공, 대형마트 근접")
    private String infoBuilding;          // 상세 건물 정보 설명
    //
//       @Schema(description = "연락 담당자 이름", example = "김철수")
    private String contactName;           // 연락 담당자 이름
    //
//       @Schema(description = "연락처 전화번호", example = "010-1234-5678")
    private String contactPhone;          // 연락처 전화번호
    //
//       @Schema(description = "편의시설 및 주변 시설 정보", example = "지하철 3분, 도서관 도보 5분 거리")
    private String facility;              // 편의시설 및 주변 시설 정보

    /**
     * DTO → VO 변환 메서드
     *
     * @param request   BuildingDetails DTO
     * @param complexId 단지 ID
     * @param memberId  등록자 ID
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
  @Setter

//         @Schema(description = "등록할 단지(복합단지) 상세 정보 DTO")
  public static class ComplexDetails {

    //             @Schema(description = "주거 유형 (예: 아파트, 오피스텔 등)", example = "아파트")
    private String resType;           // 주거 유형 (예: 아파트, 오피스텔)
    //
//       @Schema(description = "단지명", example = "롯데캐슬")
    private String complexName;       // 단지명
    //
//       @Schema(description = "단지 번호 (외부 시스템 기준)", example = "1234")
    private Long complexNo;        // 단지 번호 (외부 시스템 기준)
    //
//       @Schema(description = "시/도", example = "서울특별시")
    private String sido;              // 시/도
    //
//       @Schema(description = "시/군/구", example = "강남구")
    private String sigungu;           // 시/군/구
    //
//       @Schema(description = "행정구역 코드", example = "11680")
    private String siCode;            // 행정구역 코드
    //
//       @Schema(description = "읍/면/동", example = "역삼동")
    private String eupmyeondong;      // 읍/면/동
    //
//       @Schema(description = "Codef 트랜잭션 ID", example = "bjs20240001")
    private String transactionId;     // Codef 트랜잭션 ID
    //
//       @Schema(description = "기본 주소", example = "서울 강남구 역삼로 120")
    private String address;           // 기본 주소
    //
//       @Schema(description = "우편번호", example = "06236")
    private String zonecode;          // 우편번호
    //
//       @Schema(description = "건물명", example = "롯데캐슬아파트")
    private String buildingName;      // 건물명
    //
//       @Schema(description = "법정동명", example = "역삼동")
    private String bname;             // 법정동명
    //
//       @Schema(description = "동", example = "101동")
    private String dong;              // 동
    //
//       @Schema(description = "호수", example = "502호")
    private String ho;                // 호수
    //
//       @Schema(description = "도로명", example = "세종대로")
    private String roadName; // 도로명

    /**
     * DTO → VO 변환 메서드
     *
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
          request.getHo(),
          request.getRoadName()
      );
    }
  }

  /**
   * 이미지 정보 DTO
   */
  @Setter

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//         @Schema(description = "대표 이미지 정보 DTO")
  public static class ImageDetails {

    //             @Schema(description = "업로드할 이미지 파일", format = "binary")
    private MultipartFile imageFile;

    /**
     * DTO → VO 변환 메서드
     *
     * @param imageUrl   ImageDetails DTO
     * @param complexId  단지 ID
     * @param memberId   회원 ID
     * @param buildingId 건물 ID
     * @return ImageList VO 객체
     */
    public static ImageList toVo(String imageUrl, Long complexId, String memberId,
        Long buildingId) {
      return new ImageList(
          null,           // imageId는 DB에서 자동 생성
          buildingId,     // 연결된 건물 ID
          memberId,       // 등록자 ID
          complexId,      // 연결된 단지 ID
          imageUrl // 이미지 경로(URL)
      );
    }
  }
}


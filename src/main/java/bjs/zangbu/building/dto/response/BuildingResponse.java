package bjs.zangbu.building.dto.response;

import bjs.zangbu.building.vo.Building;
import bjs.zangbu.global.formatter.LocalDateFormatter;
import bjs.zangbu.review.dto.response.ReviewListResponse;
import bjs.zangbu.review.vo.ReviewListResponseVO;
import com.github.pagehelper.PageInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 건물 관련 응답 DTO 모음 클래스
 * <p>
 * 매물 상세 조회와 필터링된 매물 목록 조회에 사용되는 응답 객체들을 포함한다.
 */
public class BuildingResponse {

  /**
   * 매물 상세 조회 응답 DTO
   * <p>
   * 매물의 상세 정보 및 관련 리뷰, 이미지, 연락처 등 세부 정보를 포함한다.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true) // JSON에 없는 필드는 무시합니다.
  public static class ViewDetailResponse {

    @JsonProperty("resFixedDate")
    private String resFixedDate; // 등록일 (예: "YYYY-MM-DD")

    @JsonProperty("resType")
    private String resType; // 거래 유형 (예: 매매, 전세, 월세)

    @JsonProperty("resComplexName")
    private String resComplexName; // 단지명 또는 건물 이름

    @JsonProperty("commAddrRoadName")
    private String commAddrRoadName; // 도로명 주소

    @JsonProperty("commAddrLotNumber")
    private String commAddrLotNumber; // 지번 주소

    @JsonProperty("resDongCnt")
    private String resDongCnt; // 동 수 (건물 내 동 개수)

    @JsonProperty("resCompositionCnt")
    private String resCompositionCnt; // 전체 세대 수 혹은 건물 구성 수

    @JsonProperty("resApprovalDate")
    private String resApprovalDate; // 건물 준공 승인일

    @JsonProperty("resHeatingSystem")
    private String resHeatingSystem; // 난방 방식

    @JsonProperty("resFacility")
    private String resFacility; // 제공 시설 (엘리베이터, 주차장 등)

    @JsonProperty("resRealty")
    private String resRealty; // 부동산 종류 및 정보

    @JsonProperty("resTelNo")
    private String resTelNo; // 부동산 연락처 전화번호

    @JsonProperty("resImageLink")
    private String resImageLink; // 대표 이미지(썸네일) URL

    @JsonProperty("resAreaPriceList")
    private List<ResAreaPrice> resAreaPriceList; // 면적별 가격 정보 리스트

    private String infoOneline; // 매물 한 줄 소개

    private String buildingName; // 매물 제목 또는 이름

    private String infoBuilding; // 매물 상세 설명

    private List<String> imageUrl; // 이미지 URL 리스트 (썸네일 포함)

    private String contactName; // 연락 담당자 이름

    private String contactPhone; // 연락 담당자 전화번호

    private List<ReviewContent> review; // 사용자 리뷰 목록

    private boolean isBookmarked; // 사용자가 해당 매물을 북마크 했는지 여부

    /**
     * 면적별 가격 정보 DTO
     * <p>
     * 매물 면적과 해당 면적별 가격 정보 등을 포함한다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResAreaPrice {

      @JsonProperty("resArea")
      private String resArea; // 면적

      @JsonProperty("resArea1")
      private String resArea1; // 추가 면적 정보 (예: 평형)

      @JsonProperty("resCompositionCnt")
      private String resCompositionCnt; // 세대 수 (해당 면적 기준)

      @JsonProperty("resFloor")
      private String resFloor; // 층수

      @JsonProperty("resLowerAveragePrice")
      private String resLowerAveragePrice; // 최저 평균 가격

      @JsonProperty("resTopAveragePrice")
      private String resTopAveragePrice; // 최고 평균 가격

      @JsonProperty("resLowerAveragePrice1")
      private String resLowerAveragePrice1; // 추가 최저 가격 정보

      @JsonProperty("resTopAveragePrice1")
      private String resTopAveragePrice1; // 추가 최고 가격 정보

      @JsonProperty("resSuretyAmt")
      private String resSuretyAmt; // 보증금 금액

      @JsonProperty("resMonthlyRent")
      private String resMonthlyRent; // 월세 금액
    }

    /**
     * 리뷰 정보 DTO
     * <p>
     * 사용자 리뷰 아이디, 작성자 닉네임, 평점, 내용, 작성일 등을 포함한다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewContent {

      private Long reviewId; // 리뷰 ID

      private String reviewerNickname; // 작성자 닉네임

      private Integer rank; // 평점 (예: 별점)

      private String content; // 리뷰 내용

      private String createdAt; // 작성일
    }

    public static ViewDetailResponse toDto(ViewDetailResponse codefData, Building building, List<String> imageUrl,
        boolean isBookmarked, List<ReviewListResponseVO> reviewListResponse) {
      List<ReviewContent> reviews = reviewListResponse.stream()
          .map(r -> new ReviewContent(
              r.getReviewId(),
              r.getReviewerNickName(),
              r.getRank(),
              r.getContent(), // 수정된 부분
              LocalDateFormatter.CreatedAt.formattingCreatedAt(r.getCreatedAt())))
          .collect(Collectors.toList());
      return new ViewDetailResponse(
          codefData.getResFixedDate(),
          codefData.getResType(),
          codefData.getResComplexName(),
          codefData.getCommAddrRoadName(),
          codefData.getCommAddrLotNumber(),
          codefData.getResDongCnt(),
          codefData.getResCompositionCnt(),
          codefData.getResApprovalDate(),
          codefData.getResHeatingSystem(),
          codefData.getResFacility(),
          codefData.getResRealty(),
          codefData.getResTelNo(),
          codefData.getResImageLink(),
          codefData.getResAreaPriceList(),
          building.getInfoOneline(),
          building.getBuildingName(),
          building.getInfoBuilding(),
          imageUrl,
          building.getContactName(),
          building.getContactPhone(),
          reviews,
          isBookmarked);
    }

  }

  /**
   * 필터링된 매물 목록 응답 DTO (페이징 포함)
   * <p>
   * 필터 조건에 따라 조회된 매물 목록과 페이징 정보를 포함한다.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FilteredResponse {

    private List<Filtered> content; // 매물 리스트

    private int pageNum; // 현재 페이지 번호

    private int pageSize; // 페이지 당 데이터 수

    private long total; // 전체 매물 수

    private int pages; // 전체 페이지 수

    /**
     * PageInfo 객체를 DTO로 변환하는 유틸 메서드
     *
     * @param pageInfo PageHelper의 PageInfo 객체
     * @return 변환된 FilteredResponse DTO
     */
    public static FilteredResponse toDto(PageInfo<Filtered> pageInfo) {
      return new FilteredResponse(
          pageInfo.getList(),
          pageInfo.getPageNum(),
          pageInfo.getPageSize(),
          pageInfo.getTotal(),
          pageInfo.getPages());
    }

    /**
     * 필터링된 매물 기본 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filtered {

      private Long buildingId; // 매물 ID

      private String buildingName; // 매물 이름

      private Integer price; // 매물 가격

      private Float rankAverage; // 평점 평균

      private boolean isBookMarked; // 사용자가 북마크 했는지 여부
    }
  }

  /**
   * 매물 상세 정보 + 공공데이터 통합 응답 DTO
   * 사진의 매물 정보에 표시되는 모든 정보를 포함
   */
  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BuildingDetailWithPublicDataResponse {

    private ViewDetailResponse buildingDetail; // 기본 매물 정보
    private List<Object> aptComplexInfo; // 아파트 단지 정보 (공공데이터)
    private boolean publicDataAvailable; // 공공데이터 사용 가능 여부
    private String errorMessage; // 오류 메시지 (공공데이터 조회 실패 시)

    @lombok.Builder
    public BuildingDetailWithPublicDataResponse(
        ViewDetailResponse buildingDetail,
        List<Object> aptComplexInfo,
        boolean publicDataAvailable,
        String errorMessage) {
      this.buildingDetail = buildingDetail;
      this.aptComplexInfo = aptComplexInfo;
      this.publicDataAvailable = publicDataAvailable;
      this.errorMessage = errorMessage;
    }
  }
}

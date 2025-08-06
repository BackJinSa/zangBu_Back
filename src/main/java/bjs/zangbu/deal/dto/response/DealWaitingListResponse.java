package bjs.zangbu.deal.dto.response;

import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 대기 매물 목록 응답 DTO 모음
 */
// @Schema(description = "거래 대기 매물 목록 응답 DTO 모음")
public class DealWaitingListResponse {

  // /deal/waiting Response Element
  // /deal/waitinglist/purchase Response Element
  // /deal/waitinglist/onsale Response Element

  /**
   * 개별 거래 대기 매물 요소 응답 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//   @Schema(description = "거래 대기 매물 개별 요소 응답 DTO")
  public static class WaitingListElement {

    /**
     * 건물 ID
     */
//     @Schema(description = "건물 ID", example = "101")
    private Long buildingId;

    /**
     * 매물 가격 (매매가 또는 월세가 등)
     */
//     @Schema(description = "가격", example = "120000")
    private int price;

    /**
     * 건물 이름
     */
//     @Schema(description = "건물 이름", example = "신촌 스위트아파트")
    private String buildingName;

    /**
     * 주택 유형: 아파트, 오피스텔 등
     */
//     @Schema(description = "부동산 유형", example = "APARTMENT", allowableValues = {
//        "APARTMENT","OFFICETEL","VILLA","HOUSE"
//  })
    private String houseType;

    /**
     * 거래 유형: 매매, 전세, 월세
     */
//     @Schema(description = "거래 유형", example = "CHARTER", allowableValues = {
//        "MONTHLY","CHARTER","TRADING"
//})
    private String saleType;

    /**
     * 매물 대표 이미지 URL
     */
//     @Schema(description = "매물 이미지 링크", example = "https://cdn.zangbu.com/images/101.jpg")
    private String imageUrl;

    /**
     * 매물 주소
     */
//     @Schema(description = "매물 주소", example = "서울 마포구 신촌로 123")
    private String address;

    /**
     * 거래 상태: 구매중 또는 판매중
     */
//     @Schema(description = "거래 상태", example = "구매중", allowableValues = {"구매중", "판매중"})
    private String dealStatus;

    /**
     * DealWithChatRoom 객체를 WaitingListElement로 변환
     *
     * @param dto        거래 및 채팅 정보
     * @param myNickname 현재 로그인 사용자의 닉네임
     * @param imageUrl   매물 이미지 URL
     * @return WaitingListElement 객체
     */
    public static WaitingListElement toDto( // DealWithChatRoom DTO -> WaitingListElement DTO
        DealWithChatRoom dto,
        String myNickname,
        String imageUrl) {
      String status = "";
      if (myNickname.equals(dto.getConsumerNickname())) {
        status = "구매중";
      } else if (myNickname.equals(dto.getSellerNickname())) {
        status = "판매중";
      }

      return new WaitingListElement(
          dto.getBuildingId(),
          dto.getPrice(),
          dto.getBuildingName(),
          dto.getHouseType(),
          dto.getSaleType(),
          imageUrl,
          dto.getAddress(),
          status
      );
    }

    // 다건 변환: Service에서 imageMap 만들어서 넘겨줌
    public static List<WaitingListElement> fromList(
        List<DealWithChatRoom> deals,
        String nickname,
        Map<Long, String> imageMap
    ) {
      return deals.stream()
          .map(deal -> {
            String imageUrl = imageMap.getOrDefault(deal.getBuildingId(), "");
            return toDto(deal, nickname, imageUrl);
          })
          .collect(Collectors.toList());
    }
  }

// /deal/waiting Response
// /deal/waitinglist/purchase Response
// /deal/waitinglist/onsale Response

  /**
   * 거래 대기 매물 목록 전체 응답 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//   @Schema(description = "거래 대기 매물 목록 응답 DTO (페이징 포함)")
  public static class WaitingList {

    /**
     * 현재 페이지 번호 (1부터 시작)
     */
//     @Schema(description = "현재 페이지 번호", example = "1")
    private int pageNum;

    /**
     * 페이지당 항목 수
     */
//     @Schema(description = "페이지당 항목 수", example = "10")
    private int pageSize;

    /**
     * 전체 항목 수
     */
//     @Schema(description = "전체 항목 수", example = "45")
    private long total;

    /**
     * 전체 페이지 수
     */
//     @Schema(description = "전체 페이지 수", example = "5")
    private int pages;

    /**
     * 거래 대기 매물 리스트
     */
//     @Schema(description = "대기 중인 매물 목록")
    private List<WaitingListElement> deals;

    /**
     * PageInfo<DealWithChatRoom> 객체를 WaitingList DTO로 변환합니다.
     *
     * @param dtoList  PageInfo 형태의 거래 리스트
     * @param nickname 현재 로그인한 사용자 닉네임
     * @param imageMap 건물 ID와 이미지 URL 매핑 정보
     * @return WaitingList DTO
     */
    public static WaitingList toDto(
        PageInfo<DealWithChatRoom> dtoList,
        String nickname,
        Map<Long, String> imageMap
    ) {
      List<WaitingListElement> convertedList = WaitingListElement.fromList(dtoList.getList(),
          nickname, imageMap);

      return new WaitingList(
          dtoList.getPageNum(),
          dtoList.getPageSize(),
          dtoList.getTotal(),
          dtoList.getPages(),
          convertedList
      );
    }
  }
}


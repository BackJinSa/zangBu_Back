package bjs.zangbu.deal.dto.response;

import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 대기 매물 목록 응답 DTO 모음
 *
 * <p>
 * - /deal/waiting - /deal/waitinglist/purchase - /deal/waitinglist/onsale
 * </p>
 *
 * <p>개별 매물 요소 DTO({@link WaitingListElement})와 페이징된 전체 목록 DTO({@link WaitingList})를 포함</p>
 */
@ApiModel(description = "거래 대기 매물 목록 응답 DTO 모음")
public class DealWaitingListResponse {

  /**
   * 개별 거래 대기 매물 요소 응답 DTO
   *
   * <p>건물 기본 정보, 거래 유형, 대표 이미지, 주소, 사용자 관점의 거래 상태를 포함</p>
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "거래 대기 매물 개별 요소 응답 DTO")
  public static class WaitingListElement {

    @ApiModelProperty(value = "건물 ID", example = "101")
    private Long buildingId;

    @ApiModelProperty(value = "가격", example = "120000")
    private int price;

    @ApiModelProperty(value = "건물 이름", example = "신촌 스위트아파트")
    private String buildingName;

    @ApiModelProperty(value = "부동산 유형", example = "APARTMENT", allowableValues = "APARTMENT,OFFICETEL,VILLA,HOUSE")
    private String propertyType;

    @ApiModelProperty(value = "거래 유형", example = "CHARTER", allowableValues = "MONTHLY,CHARTER,TRADING")
    private String saleType;

    @ApiModelProperty(value = "매물 이미지 링크", example = "https://cdn.zangbu.com/images/101.jpg")
    private String imageUrl;

    @ApiModelProperty(value = "매물 주소", example = "서울 마포구 신촌로 123")
    private String address;

    @ApiModelProperty(value = "거래 상태", example = "구매중", allowableValues = "구매중,판매중")
    private String userStatus;

    @ApiModelProperty(value = "거래 상태 상세", example = "BEFORE_TRANSACTION")
    private String dealStatus;

    /**
     * {@link DealWithChatRoom} → {@link WaitingListElement} 변환
     *
     * @param dto        거래 및 채팅 정보
     * @param myNickname 현재 로그인 사용자의 닉네임
     * @param imageUrl   매물 대표 이미지 URL
     * @return 변환된 WaitingListElement
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
          dto.getPropertyType(),
          dto.getSaleType(),
          imageUrl,
          dto.getAddress(),
          status,
          dto.getStatus().toString()
      );
    }

    /**
     * {@link DealWithChatRoom} 리스트 → {@link WaitingListElement} 리스트 변환
     *
     * @param deals    거래·채팅 데이터 목록
     * @param nickname 현재 로그인한 사용자 닉네임
     * @param imageMap 건물 ID별 대표 이미지 URL 매핑
     * @return 변환된 WaitingListElement 리스트
     */
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

  /**
   * 거래 대기 매물 목록 전체 응답 DTO (페이징 포함)
   *
   * <p>{@link PageInfo}를 기반으로 {@link WaitingListElement} 목록과 페이징 메타데이터를 제공</p>
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "거래 대기 매물 목록 응답 DTO (페이징 포함)")
  public static class WaitingList {

    @ApiModelProperty(value = "현재 페이지 번호", example = "1")
    private int pageNum;

    @ApiModelProperty(value = "페이지당 항목 수", example = "10")
    private int pageSize;

    @ApiModelProperty(value = "전체 항목 수", example = "45")
    private long total;

    @ApiModelProperty(value = "전체 페이지 수", example = "5")
    private int pages;

    @ApiModelProperty(value = "대기 중인 매물 목록")
    private List<WaitingListElement> deals;

    /**
     * {@link PageInfo}&lt;{@link DealWithChatRoom}&gt; → {@link WaitingList} 변환
     *
     * @param dtoList  PageInfo 형태의 거래 목록
     * @param nickname 현재 로그인한 사용자 닉네임
     * @param imageMap 건물 ID별 대표 이미지 URL 매핑
     * @return 변환된 WaitingList
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


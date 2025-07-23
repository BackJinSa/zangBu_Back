package bjs.zangbu.deal.dto.response;

import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import bjs.zangbu.deal.vo.Deal;
import bjs.zangbu.deal.vo.DealEnum;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DealWaitingListResponse {


  // /deal/waiting Response Element
  // /deal/waitinglist/purchase Response Element
  // /deal/waitinglist/onsale Response Element
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WaitingListElement {

    private Long buildingId; // building 식별 id
    private int price; // 매물 가격
    private String buildingName; // 매물 이름
    private String houseType; // 부동산 유형 ('APARTMENT', 'OFFICETEL', 'VILLA', 'HOUSE')
    private String saleType; // 매매, 전세, 월세 구분 ('MONTHLY', 'CHARTER', 'TRADING')
    private String imageUrl; // 매물 이미지 링크
    private String address; // 매물 주소
    private String dealStatus; // 거래 상태(구매 중, 판매 중)

    // DealWithChatRoom DTO -> WaitingListElement DTO
    public static WaitingListElement toDto(DealWithChatRoom dto, String myNickname) {
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
          dto.getImageUrl(),
          dto.getAddress(),
          status
      );
    }
  }

  // /deal/waiting Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WaitingList {

    private List<WaitingListElement> allDeals; // WaitingListElement 를 갖는 리스트

    public static WaitingList toDto(List<DealWithChatRoom> dtoList, String myNickname) {
      List<WaitingListElement> elements = dtoList.stream()
          .filter(dto -> dto.getStatus() == DealEnum.BEFORE_TRANSACTION)
          .map(dto -> WaitingListElement.toDto(dto, myNickname))
          .collect(Collectors.toList());

      return new WaitingList(elements);
    }
  }

  // /deal/waitinglist/purchase Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WaitingListPurchase {

    private List<WaitingListElement> activeDeals; // WaitingListElement 를 갖는 리스트

    public static WaitingListPurchase toDto(List<Deal> dealVOList) {
      return new WaitingListPurchase();
    }
  }

  // /deal/waitinglist/onsale Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WaitingListOnSale {

    private List<WaitingListElement> availableDeals; // WaitingListElement 를 갖는 리스트
  }

}


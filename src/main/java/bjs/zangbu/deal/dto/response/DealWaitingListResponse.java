package bjs.zangbu.deal.dto.response;

import bjs.zangbu.deal.dto.join.DealWithChatRoom;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.Map;
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
    private String imageUrl; // 매물 이미지 링크 TODO:상위 하나의 이미지만 가져오도록 서비스 메서드 추가되면 수정 예정
    private String address; // 매물 주소
    private String dealStatus; // 거래 상태(구매 중, 판매 중)

    // DealWithChatRoom DTO -> WaitingListElement DTO
    public static WaitingListElement toDto(
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
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class WaitingList {

    private int pageNum;     // 현재 페이지
    private int pageSize;    // 페이지당 항목 수
    private long total;      // 전체 항목 수
    private int pages;       // 전체 페이지 수

    private List<WaitingListElement> deals; // WaitingListElement 를 갖는 리스트

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


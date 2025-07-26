package bjs.zangbu.deal.dto.response;

import bjs.zangbu.building.vo.Building;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DealResponse {

  // /deal/notice/{buildingId} Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Notice {

    private Long buildingId;     // 매물 식별 id
    private String buildingName; // 매물명
    private String infoBuilding; // 매물 한 줄 설명

    public static Notice toDto(Long buildingId, Building buildVO) {
      return new Notice(
          buildingId,
          buildVO.getBuildingName(),
          buildVO.getInfoBuilding()
      );
    }
  }

  // /deal/consumer/intent Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IntentResponse {

    private Long dealId;             // 거래 식별 id (신규)
    private String chatRoomId;       // 채팅 식별 id (신규)
    private String status;           // 거래 상태
    private String sellerNickname;   // 판매자 닉네임
    private String consumerNickname; // 구매자 닉네임
    private IntentBuilding building;
  }

  // /deal/consumer/intent building Element
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IntentBuilding {

    private Long buildingId; // 매물 식별 id
    private String saleType; // 매매, 전세, 월세
    private int price;       // 가격
    private int deposit;     // 보증금
  }

  // /deal/consumer/documents/{dealId}/{type}/download Response
  // /deal/consumer/report/{reportId}/download Response
  // /deal/consumer/contract/download Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Download {

    private String url; // 다운로드 링크
  }

  // /deal/consumer/membership Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Membership {

    private Long reportId; // report 식별 id
  }


  // Deal 생성 후 dealId 반환 DTO
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreateResult {

    private Long dealId;
  }
}

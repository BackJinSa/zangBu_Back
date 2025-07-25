package bjs.zangbu.deal.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DealRequest {

  // /deal/consumer/intent Request
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IntentRequest {

    private Long buildingId; // building 식별 id
  }

  //  /deal/status Request
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Status {

    private Long dealId;   // building 식별 id
    private String status; // 상태
  }

}

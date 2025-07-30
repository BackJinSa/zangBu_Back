package bjs.zangbu.deal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DealRequest 클래스는 거래 관련 요청 DTO들을 포함하는 래퍼 클래스
 */
public class DealRequest {

  // /deal/consumer/intent Request

  /**
   * 분석 리포트 결제 의도를 전달 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "IntentRequest", description = "분석 리포트 결제 의도 요청 DTO")
  public static class IntentRequest {

    /**
     * 건물 ID
     */
    @Schema(description = "건물 ID", example = "101")
    private Long buildingId; // building 식별 id
  }

  //  /deal/status Request

  /**
   * 거래 상태 변경 요청을 전달하는 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "Status", description = "거래 상태 변경 요청 DTO")
  public static class Status {

    /**
     * 거래 ID
     */
    @Schema(description = "거래 ID", example = "2001")
    private Long dealId;   // building 식별 id

    /**
     * 변경할 거래 상태 (DealEnum 상수 값 중 하나)
     */
    @Schema(description = "변경할 거래 상태", example = "CLOSE_DEAL", allowableValues = {
        "BEFORE_TRANSACTION", "BEFORE_OWNER", "BEFORE_CONSUMER", "MIDDLE_DEAL", "CLOSE_DEAL"
    })
    private String status; // 상태
  }

}

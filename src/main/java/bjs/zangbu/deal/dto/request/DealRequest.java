package bjs.zangbu.deal.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
  @ApiModel(description = "분석 리포트 결제 의도 요청 DTO")
  public static class IntentRequest {

    @ApiModelProperty(value = "건물 ID", example = "101")
    private Long buildingId;
  }

  //  /deal/status Request

  /**
   * 거래 상태 변경 요청을 전달하는 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "거래 상태 변경 요청 DTO")
  public static class Status {

    @ApiModelProperty(value = "거래 ID", example = "2001")
    private Long dealId;

    @ApiModelProperty(value = "변경할 거래 상태", example = "CLOSE_DEAL", allowableValues = "BEFORE_TRANSACTION,BEFORE_OWNER,BEFORE_CONSUMER,MIDDLE_DEAL,CLOSE_DEAL")
    private String status;
  }

}

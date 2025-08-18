package bjs.zangbu.deal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 관련 요청 DTO 모음
 *
 * <p>
 * - 거래 의향 전달 - 거래 상태 변경 요청
 * </p>
 */
public class DealRequest {

  /**
   * 분석 리포트 결제 의도 요청 DTO
   *
   * <p>/deal/consumer/intent API 요청 시 사용</p>
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "분석 리포트 결제 의도 요청 DTO")
  public static class IntentRequest {

    /**
     * 건물 식별 ID
     */
    @ApiModelProperty(value = "건물 ID", example = "101")
    private Long buildingId;
  }

  /**
   * 거래 상태 변경 요청 DTO
   *
   * <p>/deal/status API 요청 시 사용</p>
   *
   * <p>{@link bjs.zangbu.deal.vo.DealEnum}의 값 중 하나로 상태 변경</p>
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "거래 상태 변경 요청 DTO")
  public static class Status {

    /**
     * 거래 식별 ID
     */
    @ApiModelProperty(value = "거래 ID", example = "2001")
    private Long dealId;

    /**
     * 채팅 식별 ID
     */
    @ApiModelProperty(value = "채팅 식별 ID", example = "201")
    private String chatRoomId;

    /**
     * 변경할 거래 상태 {@link bjs.zangbu.deal.vo.DealEnum} 참고
     */
    @ApiModelProperty(
        value = "변경할 거래 상태",
        example = "CLOSE_DEAL",
        allowableValues = "BEFORE_TRANSACTION,BEFORE_OWNER,BEFORE_CONSUMER,MIDDLE_DEAL,CLOSE_DEAL"
    )
    private String status;
  }

  @Getter
  @NoArgsConstructor
  @ApiModel(description = "거래 생성 요청 DTO")
  public static final class CreateDeal {

    private String chatRoomId;

    @JsonCreator
    public CreateDeal(@JsonProperty("chatRoomId") String chatRoomId) {
      this.chatRoomId = chatRoomId;
    }

    public String getChatRoomId() {
      return chatRoomId;
    }
  }

}

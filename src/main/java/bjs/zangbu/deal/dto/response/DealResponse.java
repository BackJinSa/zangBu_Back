package bjs.zangbu.deal.dto.response;

import bjs.zangbu.building.vo.Building;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 관련 응답 DTO 모음 클래스입니다. 거래 안내, 의향 응답, 다운로드 링크, 리포트 관련 응답 등을 포함합니다.
 */
// @Schema(description = "거래 관련 응답 DTO 모음")
public class DealResponse {

  // /deal/notice/{buildingId} Response

  /**
   * 거래 전 안내 정보 응답 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//   @Schema(description = "거래 전 안내 정보 응답 DTO")
  public static class Notice {

    /**
     * 매물 식별 ID
     */
//     @Schema(description = "건물 ID", example = "1001")
    private Long buildingId;

    /**
     * 매물명
     */
//     @Schema(description = "건물 이름", example = "신촌 한울타리 아파트")
    private String buildingName;

    /**
     * 매물 한 줄 설명
     */
//     @Schema(description = "건물 간단 설명", example = "교통 좋은 역세권 아파트")
    private String infoBuilding;

    /**
     * Building VO 객체로부터 Notice DTO 생성
     *
     * @param buildingId 건물 ID
     * @param buildVO    Building 객체
     * @return Notice DTO
     */
    public static Notice toDto(Long buildingId, Building buildVO) {
      return new Notice(
          buildingId,
          buildVO.getBuildingName(),
          buildVO.getInfoBuilding()
      );
    }
  }

  // /deal/consumer/intent Response

  /**
   * 거래 의향 응답 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//   @Schema(description = "거래 의향 응답 DTO")
  public static class IntentResponse {

    /**
     * 거래 ID
     */
//     @Schema(description = "거래 ID", example = "2002")
    private Long dealId;

    /**
     * 채팅방 ID
     */
//     @Schema(description = "채팅방 ID", example = "chat-room-uuid-1234")
    private String chatRoomId;

    /**
     * 거래 상태
     */
//     @Schema(description = "거래 상태", example = "BEFORE_TRANSACTION", allowableValues = {
//        "BEFORE_TRANSACTION","BEFORE_OWNER","BEFORE_CONSUMER","MIDDLE_DEAL","CLOSE_DEAL"
//  })
    private String status;

    /**
     * 판매자 닉네임
     */
//     @Schema(description = "판매자 닉네임", example = "부동산박사")
    private String sellerNickname;

    /**
     * 구매자 닉네임
     */
//     @Schema(description = "구매자 닉네임", example = "집찾는사람")
    private String consumerNickname;

    /**
     * 거래 대상 매물 정보
     */
//     @Schema(description = "거래 대상 매물 정보")
    private IntentBuilding building;
  }

// /deal/consumer/intent building Element

  /**
   * 거래 시 포함되는 매물 정보 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//   @Schema(description = "거래 시 매물 정보")
  public static class IntentBuilding {

    /**
     * 매물 ID
     */
//     @Schema(description = "건물 ID", example = "3003")
    private Long buildingId;

    /**
     * 거래 유형: 월세, 전세, 매매
     */
//     @Schema(description = "거래 유형", example = "MONTHLY", allowableValues = {"MONTHLY", "CHARTER",
//        "TRADING"
//})
    private String saleType;

    /**
     * 가격 (월세/매매 금액)
     */
//     @Schema(description = "가격 (월세/매매 금액)", example = "120000")
    private int price;

    /**
     * 보증금
     */
//     @Schema(description = "보증금", example = "5000")
    private int deposit;
  }

// /deal/consumer/documents/{dealId}/{type}/download Response
// /deal/consumer/report/{reportId}/download Response
// /deal/consumer/contract/download Response

  /**
   * 파일 다운로드 링크 응답 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//   @Schema(description = "파일 다운로드 링크 응답 DTO")
  public static class Download {

    /**
     * 파일 다운로드 URL
     */
//     @Schema(description = "파일 다운로드 URL", example = "https://zangbu.s3.kr-object.ncloudstorage.com/report/abc123.pdf")
    private String url;
  }

// /deal/consumer/membership Response

  /**
   * 분석 리포트 결제 완료 응답 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//   @Schema(description = "분석 리포트 결제 완료 응답 DTO")
  public static class Membership {

    /**
     * 리포트 ID
     */
//     @Schema(description = "리포트 ID", example = "4004")
    private Long reportId;
  }


  /**
   * 거래 생성 결과 응답 DTO
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//   @Schema(description = "거래 생성 결과 응답 DTO")
  public static class CreateResult {

    /**
     * 생성된 거래 ID
     */
//     @Schema(description = "생성된 거래 ID", example = "5005")
    private Long dealId;
  }
}

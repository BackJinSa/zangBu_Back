package bjs.zangbu.codef.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CODEF API 응답 DTO들을 모아놓은 클래스.
 */
@ApiModel(description = "CODEF API 응답 DTO")
public class CodefResponse {

  /**
   * CODEF 응답의 'data' 필드에 해당하는 단지 정보 리스트를 담는 래퍼 DTO.
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "CODEF 응답의 'data' 필드에 해당하는 단지 정보 리스트를 담는 래퍼 DTO")
  public static class ComplexResponse {

    /**
     * CODEF 응답의 'data' 필드에 해당하는 단지 정보 리스트.
     */
    @ApiModelProperty(value = "CODEF 응답의 'data' 필드에 해당하는 단지 정보 리스트")
    private List<ComplexInfo> data;

    /**
     * CODEF 단지 정보 API의 개별 단지 정보를 담는 DTO.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "CODEF 단지 정보 API의 개별 단지 정보를 담는 DTO")
    public static class ComplexInfo {

      /**
       * 구분 (예: 아파트, 오피스텔 등).
       */
      @ApiModelProperty(value = "구분 (예: 아파트, 오피스텔 등)")
      private String resType;
      /**
       * 단지명 (예: 송파더센트레...).
       */
      @ApiModelProperty(value = "단지명 (예: 송파더센트레...)")
      private String resComplexName;
      /**
       * 단지 일련번호.
       */
      @ApiModelProperty(value = "단지 일련번호")
      private String commComplexNo;
    }
  }
}
package bjs.zangbu.codef.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CODEF API 응답 DTO들을 모아놓은 클래스.
 */
@Schema(description = "CODEF API 응답 DTO")
public class CodefResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "CODEF 응답의 'data' 필드에 해당하는 단지 정보 리스트를 담는 래퍼 DTO")
    public static class ComplexResponse {
        @Schema(description = "CODEF 응답의 'data' 필드에 해당하는 단지 정보 리스트")
        private List<ComplexInfo> data;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Schema(description = "CODEF 단지 정보 API의 개별 단지 정보를 담는 DTO")
        public static class ComplexInfo {
            @Schema(description = "구분 (예: 아파트, 오피스텔 등)")
            private String resType;
            @Schema(description = "단지명 (예: 송파더센트레...)")
            private String resComplexName;
            @Schema(description = "단지 일련번호")
            private String commComplexNo;
        }
    }
}
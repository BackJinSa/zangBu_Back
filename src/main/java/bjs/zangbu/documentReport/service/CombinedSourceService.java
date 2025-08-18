// src/main/java/bjs/zangbu/report/service/CombinedSourceService.java
package bjs.zangbu.documentReport.service;

import java.util.Map;

public interface CombinedSourceService {
    /**
     * Mongo에 저장된 (등기부등본 + 건축물대장) 원본을 읽어
     * 제시한 스키마의 합쳐진 JSON(Map)으로 변환합니다.
     * @param memberId  멤버 식별자(String)
     * @param buildingId 매물 ID
     * @param inputs 사용자가 제공한 입력값(시세, 내 보증금 등) - null 가능
     */
    Map<String, Object> buildCombined(String memberId, Long buildingId, Map<String, Object> inputs);
}

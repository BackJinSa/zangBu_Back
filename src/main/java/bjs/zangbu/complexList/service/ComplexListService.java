package bjs.zangbu.complexList.service;

import bjs.zangbu.complexList.vo.ComplexList;

/**
 * 복합 단지(ComplexList) 관련 서비스 인터페이스
 */
public interface ComplexListService {

    /**
     * 단지 정보를 등록하고 생성된 ID를 반환합니다.
     *
     * @param complexList 등록할 단지 정보 객체
     * @return 등록 후 생성된 단지 ID
     */
    Long createComplexList(ComplexList complexList);

    String getComplexNoByBuildingId(Long buildingId);

    Long getComplexIdByBuildingId(Long buildingId);
}

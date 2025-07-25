package bjs.zangbu.complexList.service;

import bjs.zangbu.complexList.vo.ComplexList;

public interface ComplexListService {

    // 단지 정보 등록 후 생성된 ID 반환
    Long createComplexList(ComplexList complexList);
}

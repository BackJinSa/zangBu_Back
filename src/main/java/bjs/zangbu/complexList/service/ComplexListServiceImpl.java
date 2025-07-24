package bjs.zangbu.complexList.service;

import bjs.zangbu.complexList.mapper.ComplexListMapper;
import bjs.zangbu.complexList.vo.ComplexList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComplexListServiceImpl implements ComplexListService {

    private final ComplexListMapper complexListMapper;

    // 단지 정보 등록 처리 및 생성된 ID 반환
    @Override
    public Long createComplexList(ComplexList complexList) {
        return complexListMapper.createComplexList(complexList);
    }
}

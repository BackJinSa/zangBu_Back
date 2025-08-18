package bjs.zangbu.complexList.service;

import bjs.zangbu.complexList.mapper.ComplexListMapper;
import bjs.zangbu.complexList.vo.ComplexList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 복합 단지(ComplexList) 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class ComplexListServiceImpl implements ComplexListService {

  /**
   * 복합 단지 매퍼 객체 주입
   */
  private final ComplexListMapper complexListMapper;

  /**
   * 단지 정보를 등록하고, 생성된 단지 ID를 반환합니다.
   *
   * @param complexList 등록할 단지 정보 객체
   * @return 등록 후 생성된 단지 ID
   */
  @Override
  public Long createComplexList(ComplexList complexList) {
    complexListMapper.createComplexList(complexList); // 여기서 vo.complexId 가 채워짐 (selectKey 덕분)
    return complexList.getComplexId();
  }

  @Override
  public String getComplexNoByBuildingId(Long buildingId) {
    return complexListMapper.getComplexNoByBuildingId(buildingId);
  }

  @Override
  public Long getComplexIdByBuildingId(Long buildingId) {
    return complexListMapper.getComplexIdByBuildingId(buildingId);
  }

  @Override
  public ComplexList getComplexListByBuildingId(Long buildingId) {
    return complexListMapper.getComplexListByBuildingId(buildingId);
  }
}

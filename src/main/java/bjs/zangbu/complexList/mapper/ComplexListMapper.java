package bjs.zangbu.complexList.mapper;

import bjs.zangbu.complexList.vo.ComplexList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 복합 단지(ComplexList) 관련 DB 매핑 인터페이스 (MyBatis Mapper) 단지 정보의 삽입 기능을 담당합니다.
 */
@Mapper
public interface ComplexListMapper {

  /**
   * 단지(ComplexList) 정보를 데이터베이스에 삽입하고, 생성된 단지 ID를 반환합니다.
   *
   * @param complexList 삽입할 단지 정보 객체
   * @return 삽입 후 생성된 단지 ID
   */
  int createComplexList(@Param("complexList") ComplexList complexList);

  /**
   * 건물 ID로 단지 ID를 조회합니다.
   *
   * @param buildingId 건물 ID
   * @return 단지 ID
   */
  Long selectComplexIdByBuildingId(Long buildingId);

  /**
   * 단지 ID로 단지 정보를 조회합니다.
   *
   * @param complexId 단지 ID
   * @return 단지 정보
   */
  ComplexList selectById(Long complexId);

  String getComplexNoByBuildingId(@Param("buildingId") Long buildingId);

  Long getComplexIdByBuildingId(@Param("buildingId") Long buildingId);

  ComplexList getComplexListByBuildingId(@Param("buildingId") Long buildingId);
}

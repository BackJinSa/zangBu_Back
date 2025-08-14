package bjs.zangbu.documentReport.mapper;

import bjs.zangbu.documentReport.vo.EstateEul;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EstateEulMapper {

    List<EstateEul> findByBuildingId(@Param("buildingId") Long buildingId);

    int deleteByBuildingId(@Param("buildingId") Long buildingId);

    int batchInsert(@Param("list") List<EstateEul> list);

    /** 선순위 1건만 가져오기(정렬 기준) */
    EstateEul findFirstEncumbrance(@Param("buildingId") Long buildingId);
}

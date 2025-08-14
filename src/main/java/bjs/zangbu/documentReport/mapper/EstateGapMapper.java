package bjs.zangbu.documentReport.mapper;

import bjs.zangbu.documentReport.vo.EstateGap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EstateGapMapper {

    List<EstateGap> findByBuildingId(@Param("buildingId") Long buildingId);

    int deleteByBuildingId(@Param("buildingId") Long buildingId);

    /** 대체 저장: 기존 삭제 후 일괄 삽입 */
    int batchInsert(@Param("list") List<EstateGap> list);
}

package bjs.zangbu.documentReport.mapper;

import bjs.zangbu.documentReport.vo.BuildingRegisterChange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BuildingRegisterChangeMapper {

    List<BuildingRegisterChange> findByBuildingId(@Param("buildingId") Long buildingId);

    int deleteByBuildingId(@Param("buildingId") Long buildingId);

    int batchInsert(@Param("list") List<BuildingRegisterChange> list);
}

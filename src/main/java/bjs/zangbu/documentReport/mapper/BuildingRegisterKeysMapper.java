package bjs.zangbu.documentReport.mapper;

import bjs.zangbu.documentReport.vo.BuildingRegisterKeys;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BuildingRegisterKeysMapper {

    BuildingRegisterKeys findByBuildingId(@Param("buildingId") Long buildingId);

    int upsert(BuildingRegisterKeys keys);
}

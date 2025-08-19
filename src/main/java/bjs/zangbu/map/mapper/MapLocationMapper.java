package bjs.zangbu.map.mapper;

import bjs.zangbu.map.vo.MapLocation;
import bjs.zangbu.notification.vo.SaleType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import bjs.zangbu.map.dto.BuildingComplexInfo;
import bjs.zangbu.notification.vo.SaleType;
import bjs.zangbu.building.vo.PropertyType;

import java.util.List;

@Mapper
public interface MapLocationMapper {
    List<BuildingComplexInfo> findLocationsByFilters(
            @Param("saleTypes") List<SaleType> saleTypes,
            @Param("propertyTypes") List<PropertyType> propertyTypes,
            @Param("priceMin") Integer priceMin,
            @Param("priceMax") Integer priceMax);

    List<BuildingComplexInfo> findAllBuildingComplexInfo();
}
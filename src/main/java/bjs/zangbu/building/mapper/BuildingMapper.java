package bjs.zangbu.building.mapper;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface BuildingMapper {
    boolean isBuildingExists(Long buildingId);
}

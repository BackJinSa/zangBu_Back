package bjs.zangbu.building.mapper;
import bjs.zangbu.building.vo.Building;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BuildingMapper {

    boolean isBuildingExists(Long buildingId);

    Building getBuildingById(Long buildingId);

    void insertBookMark(String userId, Long buildingId);

    void incrementBookmarkCount(Long buildingId);

    void deleteBookMark(String userId, Long buildingId);

    void decrementBookmarkCount(Long buildingId);
}

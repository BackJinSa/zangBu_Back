package bjs.zangbu.building.mapper;

import bjs.zangbu.building.dto.response.BuildingResponse;
import bjs.zangbu.building.dto.response.BuildingResponse.FilteredResponse.Filtered;
import bjs.zangbu.building.dto.response.MainResponse;
import bjs.zangbu.building.dto.response.MainResponse.BuildingInfo;
import bjs.zangbu.building.vo.Building;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BuildingMapper {
    // 찜 수 1 감소
    void decrementBookmarkCount(Long buildingId);
    // 찜 수 1 증가
    void incrementBookmarkCount(Long buildingId);
    // 매물 존재 여부 확인
    boolean isBuildingExists(Long buildingId);
    // 매물 상세 조회
    Building getBuildingById(Long buildingId);
    // 현재 가격 조회
    Integer selectCurrentPrice(Long buildingId);
    // 매물 등록 및 ID 반환
    Long createBuilding(Building building);
    // 매물 삭제
    void deleteBuilding(Long buildingId);
    // 필터링된 매물 목록 조회 (평점 포함)
    List<Filtered> selectFilteredBuildings(
            @Param("buildingName") String buildingName,
            @Param("saleType") String saleType,
            @Param("startPrice") Long startPrice,
            @Param("endPrice") Long endPrice,
            @Param("propertyType") String propertyType,
            @Param("offset") int offset,
            @Param("size") int size
    );
    // 필터링된 매물 총 개수 조회
    long countFilteredBuildings(
            @Param("buildingName") String buildingName,
            @Param("saleType") String saleType,
            @Param("startPrice") Long startPrice,
            @Param("endPrice") Long endPrice,
            @Param("propertyType") String propertyType
    );

    // 리뷰 많은 매물 Top 3
    List<BuildingInfo> selectTopReviewedBuildings(@Param("memberId") String memberId);

    // 찜 많은 매물 Top 3
    List<BuildingInfo> selectTopLikedBuildings(@Param("memberId") String memberId);

    // 신규 등록 매물 Top 3
    List<BuildingInfo> selectNewRooms(@Param("memberId") String memberId);

}

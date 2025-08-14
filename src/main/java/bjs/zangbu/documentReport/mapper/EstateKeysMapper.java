package bjs.zangbu.documentReport.mapper;

import bjs.zangbu.documentReport.vo.EstateKeys;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EstateKeysMapper {

    EstateKeys findByBuildingId(@Param("buildingId") Long buildingId);

    /** building_id PK 기준 UPSERT */
    int upsert(EstateKeys keys);

    /** 선순위 채권/건물만 여부만 부분 업데이트 (옵션) */
    int updateSummaryFields(@Param("buildingId") Long buildingId,
                            @Param("firstMaxClaim") Long firstMaxClaim,
                            @Param("buildingOnlyOverall") Boolean buildingOnlyOverall);
}

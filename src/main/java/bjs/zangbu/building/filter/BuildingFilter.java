package bjs.zangbu.building.filter;
import bjs.zangbu.building.mapper.BuildingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuildingFilter {
    private final BuildingMapper buildingMapper;
    // 건물이 존재하는지 여부 검증
    public void validateBuildingExists(Long buildingId) {
        boolean isTrue = buildingMapper.isBuildingExists(buildingId);
        if (!isTrue) {
            throw new IllegalArgumentException("해당 건물을 찾을 수 없습니다.");
        }
    }
}

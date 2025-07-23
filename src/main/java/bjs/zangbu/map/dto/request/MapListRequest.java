package bjs.zangbu.map.dto.request;

import bjs.zangbu.map.vo.MapLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapListRequest {
    private String address;
    private String buildingName;

    // DTO 를 VO 로 변환하는 메서드
    public MapLocation toVo() {
        return new MapLocation(
            null,
            null,
            this.buildingName
        );
    }
}

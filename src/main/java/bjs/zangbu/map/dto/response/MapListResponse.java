package bjs.zangbu.map.dto.response;

import bjs.zangbu.map.vo.MapLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MapListResponse {
    private String address;
    private String latitude;
    private String longitude;
    private String buildingName;

    // VO 를 DTO 로 변환하는 메서드
    public static MapListResponse fromVo(MapLocation vo) {
        return new MapListResponse(
                vo.getAddress(),
                vo.getLatitude(),
                vo.getLongitude(),
                vo.getBuildingName()
        );
    }
}

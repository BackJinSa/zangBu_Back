package bjs.zangbu.map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MapListResponse {
    private Long buildingId;
    private String buildingName;
    private String address;
}

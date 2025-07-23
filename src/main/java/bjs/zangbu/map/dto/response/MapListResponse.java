package bjs.zangbu.map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MapListResponse {
    private String address;
    private String latitude;
    private String longitude;
    private String buildingName;
}

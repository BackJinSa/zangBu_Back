package bjs.zangbu.map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MapResponse {
    private String address;
    private String latitude;
    private String longitude;
    private String buildingName;
}

package bjs.zangbu.map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapListRequest {
    private String address;
    private String buildingName;
}

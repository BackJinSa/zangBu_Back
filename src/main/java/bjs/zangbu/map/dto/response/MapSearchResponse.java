package bjs.zangbu.map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MapSearchResponse {
    private String placeName;       // JSON의 place_name
    private String addressName;     // address_name
    private String roadAddressName; // road_address_name
    private String x;               // longitude
    private String y;               // latitude
    private String placeUrl;        // place_url
}

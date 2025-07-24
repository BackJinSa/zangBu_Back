package bjs.zangbu.map.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MapCategoryResponse {
    // Java 필드명과 실제 JSON 키가 다르기 때문에 @JsonProperty 사용하여 명시함

    // Java 필드명(placeName)과 JSON 키("place_name")가 다르므로 매핑을 위해 사용
    @JsonProperty("place_name")
    private final String placeName;

    // JSON 키("distance")와 Java 필드명이 동일하여 어노테이션 없이도 매핑 가능
    private final String distance;

    // Java 필드명(placeUrl)과 JSON 키("place_url")가 다르므로 매핑을 위해 사용
    @JsonProperty("place_url")
    private final String placeUrl;

    // Java 필드명(categoryGroupCode)과 JSON 키("category_group_code")가 다르므로 매핑을 위해 사용
    @JsonProperty("category_group_code")
    private final String categoryGroupCode;

    // Java 필드명(categoryGroupName)과 JSON 키("category_group_name")가 다르므로 매핑을 위해 사용
    @JsonProperty("category_group_name")
    private final String categoryGroupName;

    // Java 필드명(addressName)과 JSON 키("address_name")가 다르므로 매핑을 위해 사용
    @JsonProperty("address_name")
    private final String addressName;

    // Java 필드명(roadAddressName)과 JSON 키("road_address_name")가 다르므로 매핑을 위해 사용
    @JsonProperty("road_address_name")
    private final String roadAddressName;

    // JSON 키("x")와 Java 필드명이 동일하여 어노테이션 없이도 매핑 가능 (경도)
    private final String x;

    // JSON 키("y")와 Java 필드명이 동일하여 어노테이션 없이도 매핑 가능 (위도)
    private final String y;

    // JSON 키("phone")와 Java 필드명이 동일하여 어노테이션 없이도 매핑 가능 (전화번호)
    private final String phone;

    // JSON 키("id")와 Java 필드명이 동일하여 어노테이션 없이도 매핑 가능 (장소 ID)
    private final String id;
}

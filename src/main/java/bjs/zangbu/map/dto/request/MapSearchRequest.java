package bjs.zangbu.map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapSearchRequest {

    // 입력받은 검색어
    private String query;
}

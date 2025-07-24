package bjs.zangbu.map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapCategoryRequest {
    private String category_group_code; // 카테고리 그룹 코드 (예: 마트: MT1, 편의점: CS2 등)
    private String x; // 중심 좌표 X (longitude, 경도)
    private String y; // 중심 좌표 Y (latitude, 위도)
    private int radius; // 반경 (미터 단위, 최대 20000)
    private int page; // 결과 페이지 번호 (기본 1)
    private int size; // 한 페이지 결과 수 (기본 15, 최대 45)
    private String sort; // 정렬 기준 (distance 또는 accuracy)
}

package bjs.zangbu.map.dto.request;

import bjs.zangbu.building.vo.PropertyType;
import bjs.zangbu.notification.vo.SaleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MapFilterRequest {
    /** 매매, 전세, 월세 등 (VO 의 Enum) */
    private List<SaleType> saleTypes;
    /** 아파트, 오피스텔, 주택, 빌라 등 (VO 의 Enum) */
    private List<PropertyType> propertyTypes;
    /** 최소 가격 (단위: 원) */
    private Integer priceMin;
    /** 최대 가격 (단위: 원) */
    private Integer priceMax;
}

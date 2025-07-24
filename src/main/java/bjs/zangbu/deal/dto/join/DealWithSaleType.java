package bjs.zangbu.deal.dto.join;

import bjs.zangbu.deal.vo.DealEnum;
import bjs.zangbu.notification.vo.SaleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/** deal  +  building.sale_type JOIN 결과 전용 DTO */
public class DealWithSaleType {
    private Long     dealId;
    private DealEnum status;
    private String   userId;
    private Long     buildingId;
    private SaleType saleType;   // MONTHLY / CHARTER / TRADING
}

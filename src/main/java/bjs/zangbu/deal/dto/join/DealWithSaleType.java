package bjs.zangbu.deal.dto.join;

import bjs.zangbu.deal.vo.DealEnum;
import bjs.zangbu.notification.vo.SaleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/** deal  +  building.sale_type JOIN 결과 전용 DTO */
public class DealWithSaleType {

  private Long dealId; // 거래 식별 id
  private DealEnum status; // 거래 상태
  private String memberId; // 유저 id
  private Long buildingId; // 건물 id
  private SaleType saleType;   // MONTHLY / CHARTER / TRADING
}

package bjs.zangbu.publicdata.service.managecost;

import bjs.zangbu.publicdata.dto.managecost.GasCost;
import bjs.zangbu.publicdata.dto.managecost.HeatCost;

public interface ManageCostService {
    HeatCost fetchHeatCost(String kaptCode, String searchDate);
    GasCost fetchGasCost (String kaptCode, String searchDate);
}

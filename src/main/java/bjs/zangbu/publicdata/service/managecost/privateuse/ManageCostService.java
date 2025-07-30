package bjs.zangbu.publicdata.service.managecost.privateuse;

import bjs.zangbu.publicdata.dto.managecost.privateuse.GasCost;
import bjs.zangbu.publicdata.dto.managecost.privateuse.HeatCost;

public interface ManageCostService {
    HeatCost fetchHeatCost(String kaptCode, String searchDate);
    GasCost fetchGasCost (String kaptCode, String searchDate);
}

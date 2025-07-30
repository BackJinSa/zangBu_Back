package bjs.zangbu.publicdata.service.managecost.publicuse;

import bjs.zangbu.publicdata.dto.managecost.publicuse.CleanCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.EduCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.EtcCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.VehicleCost;

public interface PublicUseService {
    VehicleCost fetchVehicleCost(String kaptCode, String searchDate);
    EtcCost fetchEtcCost    (String kaptCode, String searchDate);
    EduCost fetchEduCost    (String kaptCode, String searchDate);
    CleanCost fetchCleanCost  (String kaptCode, String searchDate);
}

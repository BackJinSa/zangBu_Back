package bjs.zangbu.publicdata.service.aptlist;

import bjs.zangbu.publicdata.dto.aptlist.AptComplex;
import bjs.zangbu.publicdata.dto.aptlist.RoadAptComplex;
import java.util.List;

public interface AptListService {
    List<AptComplex> getTotalAptList(int pageNo, int numOfRows);
    List<AptComplex> getSidoAptList(String sidoCode, int pageNo, int numOfRows);
    List<AptComplex> getSigunguAptList(String sigunguCode, int pageNo, int numOfRows);
    List<AptComplex> getLegaldongAptList(String bjdCode, int pageNo, int numOfRows);
    List<RoadAptComplex> getRoadnameAptList(String roadCode, int pageNo, int numOfRows);
}

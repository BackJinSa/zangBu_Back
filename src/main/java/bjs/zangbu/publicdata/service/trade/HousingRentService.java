package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.HousingRent;

import java.util.List;

public interface HousingRentService {
    List<HousingRent> fetchSHRents(String lawdCd, String dealYmd, int pageNo, int numOfRows);
}

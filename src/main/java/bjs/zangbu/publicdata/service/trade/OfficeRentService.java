package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.OfficeRent;

import java.util.List;

public interface OfficeRentService {
    List<OfficeRent> fetchOfficeRents(String lawdCd, String dealYmd, int pageNo, int numOfRows);
}

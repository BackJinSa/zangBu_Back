package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.VillaRent;

import java.util.List;

public interface VillaRentService {
    List<VillaRent> fetchVillaRents(String lawdCd, String dealYmd);
}

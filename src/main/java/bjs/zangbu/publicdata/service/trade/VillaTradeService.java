package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.VillaTrade;

import java.util.List;

public interface VillaTradeService {
    List<VillaTrade> fetchVillaTrades(String lawdCd, String dealYmd);
}

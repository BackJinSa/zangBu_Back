package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.HousingTrade;

import java.util.List;

public interface HousingTradeService {
    List<HousingTrade> fetchHousingTrades(String lawdCd, String dealYmd, int pageNo, int numOfRows);
}

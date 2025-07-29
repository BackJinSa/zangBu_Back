package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.OfficeTrade;

import java.util.List;

public interface OfficeTradeService {
    List<OfficeTrade> fetchOfficeTrades(String lawdCd, String dealYmd, int pageNo, int numOfRows);
}

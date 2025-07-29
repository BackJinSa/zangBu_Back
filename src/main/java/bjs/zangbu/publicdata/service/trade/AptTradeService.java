package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.AptTrade;

import java.util.List;

public interface AptTradeService {
    List<AptTrade> fetchAptTrades(
            String lawdCd5,
            String dealYmd,
            int pageNo,
            int numOfRows
    );
}
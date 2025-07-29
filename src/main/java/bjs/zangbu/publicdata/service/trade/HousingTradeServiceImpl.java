package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.HousingTrade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HousingTradeServiceImpl implements HousingTradeService{
    private final RestTemplate rt;

    @Value("")
    private String serviceKey;

    @Override
    public List<HousingTrade> fetchHousingTrades(String lawdCd, String dealYmd, int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1613000/RTMSDataSvcSHTrade/getRTMSDataSvcSHTrade")
                .queryParam("serviceKey", serviceKey)
                .queryParam("LAWD_CD", lawdCd)
                .queryParam("DEAL_YMD", dealYmd)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("_type", "json")
                .build().encode().toUri();

        @SuppressWarnings("unchecked")
        Map<String,Object> resp = rt.getForObject(uri, Map.class);
        Map<String,Object> response = (Map<String,Object>) resp.get("response");
        Map<String,Object> body = (Map<String,Object>) response.get("body");
        Map<String,Object> items = (Map<String,Object>) body.get("items");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> list = (List<Map<String,Object>>) items.get("item");

        List<HousingTrade> result = new ArrayList<>();
        for (Map<String,Object> m : list) {
            HousingTrade it = new HousingTrade();
            it.setBuildYear((Integer)m.get("buildYear"));
            it.setDealYear((Integer)m.get("dealYear"));
            it.setDealMonth((Integer)m.get("dealMonth"));
            it.setDealDay((Integer)m.get("dealDay"));
            it.setDealAmount((String)m.get("dealAmount"));
            it.setHouseType((String)m.get("houseType"));
            it.setPlottageAr(((Number)m.get("plottageAr")).doubleValue());
            it.setTotalFloorAr(((Number)m.get("totalFloorAr")).doubleValue());
            it.setJibun((String)m.get("jibun"));
            it.setUmdNm((String)m.get("umdNm"));
            it.setSggCd((Integer)m.get("sggCd"));
            result.add(it);
        }
        return result;
    }
}

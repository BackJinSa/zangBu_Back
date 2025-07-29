package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.OfficeTrade;
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
public class OfficeTradeServiceImpl implements OfficeTradeService{
    private final RestTemplate rt;

    @Value("${publicdata.offitrade.serviceKey}")
    private String serviceKey;

    @Override
    public List<OfficeTrade> fetchOfficeTrades(String lawdCd, String dealYmd, int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1613000/RTMSDataSvcOffiTrade/getRTMSDataSvcOffiTrade")
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
        Map<String,Object> body     = (Map<String,Object>) response.get("body");
        Map<String,Object> items    = (Map<String,Object>) body.get("items");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> list = (List<Map<String,Object>>) items.get("item");

        List<OfficeTrade> result = new ArrayList<>();
        for (Map<String,Object> m : list) {
            OfficeTrade it = new OfficeTrade();
            it.setBuildYear(((Number)m.get("buildYear")).intValue());
            it.setDealYear(((Number)m.get("dealYear")).intValue());
            it.setDealMonth(((Number)m.get("dealMonth")).intValue());
            it.setDealDay(((Number)m.get("dealDay")).intValue());
            it.setDealAmount((String)m.get("dealAmount"));
            it.setExcluUseAr(((Number)m.get("excluUseAr")).doubleValue());
            it.setFloor(((Number)m.get("floor")).intValue());
            it.setJibun(m.get("jibun") != null ? m.get("jibun").toString() : null);
            it.setOffiNm((String)m.get("offiNm"));
            it.setUmdNm((String)m.get("umdNm"));
            it.setSggCd(((Number)m.get("sggCd")).intValue());
            it.setSggNm((String)m.get("sggNm"));
            result.add(it);
        }
        return result;
    }
}

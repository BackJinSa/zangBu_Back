package bjs.zangbu.publicdata.service.trade;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bjs.zangbu.publicdata.dto.trade.AptTrade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class AptTradeServiceImpl implements AptTradeService {

    private final RestTemplate rt;

    @Value("${KI6q5jPyEQnEa9tmhllI6W8ufddtQ68gxlGFxHFspiOhfRCEF%2BfoUQ4oHLgL%2Bs61oIGO%2F1lS75LSfB%2FIBuFeSQ%3D%3D}")
    private String serviceKey;

    @Override
    @SuppressWarnings("unchecked")
    public List<AptTrade> fetchAptTrades(
            String lawdCd5,
            String dealYmd,
            int pageNo,
            int numOfRows
    ) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade")
                .queryParam("serviceKey", serviceKey)
                .queryParam("LAWD_CD", lawdCd5)
                .queryParam("DEAL_YMD", dealYmd)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("_type", "json")
                .build().encode().toUri();

        Map<String,Object> resp = rt.getForObject(uri, Map.class);
        Map<String,Object> response = (Map<String,Object>) resp.get("response");
        Map<String,Object> body     = (Map<String,Object>) response.get("body");
        Map<String,Object> items    = (Map<String,Object>) body.get("items");
        List<Map<String,Object>> rows = (List<Map<String,Object>>) items.get("item");

        List<AptTrade> list = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            AptTrade t = new AptTrade();
            t.setAptDong((String)m.get("aptDong"));
            t.setAptNm((String)m.get("aptNm"));
            t.setBuildYear(((Number)m.get("buildYear")).intValue());
            t.setDealAmount((String)m.get("dealAmount"));
            t.setDealYear(((Number)m.get("dealYear")).intValue());
            t.setDealMonth(((Number)m.get("dealMonth")).intValue());
            t.setDealDay(((Number)m.get("dealDay")).intValue());
            t.setExcluUseAr(((Number)m.get("excluUseAr")).doubleValue());
            t.setFloor(((Number)m.get("floor")).intValue());
            t.setJibun(String.valueOf(m.get("jibun")));
            t.setLandLeaseholdGbn((String)m.get("landLeaseholdGbn"));
            t.setUmdNm((String)m.get("umdNm"));
            t.setSggCd(((Number)m.get("sggCd")).intValue());
            list.add(t);
        }
        return list;
    }
}

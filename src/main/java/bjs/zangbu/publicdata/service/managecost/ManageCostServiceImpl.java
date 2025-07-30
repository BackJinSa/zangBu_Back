package bjs.zangbu.publicdata.service.managecost;

import bjs.zangbu.publicdata.dto.managecost.GasCost;
import bjs.zangbu.publicdata.dto.managecost.HeatCost;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ManageCostServiceImpl implements ManageCostService {

    private final RestTemplate rt;

    @Value("서비스 키 사용하는 곡ㅅ")
    private String serviceKey;

    @Override
    public HeatCost fetchHeatCost(String kaptCode, String searchDate) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1613000/AptIndvdlzManageCostServiceV2/getHsmpHeatCostInfoV2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("kaptCode", kaptCode)
                .queryParam("searchDate", searchDate)
                .build().encode().toUri();

        @SuppressWarnings("unchecked")
        Map<String,Object> resp     = rt.getForObject(uri, Map.class);
        Map<String,Object> response = (Map<String,Object>) resp.get("response");
        Map<String,Object> body     = (Map<String,Object>) response.get("body");
        Map<String,Object> item     = (Map<String,Object>) body.get("item");

        HeatCost c = new HeatCost();
        c.setKaptCode((String)item.get("kaptCode"));
        c.setKaptName((String)item.get("kaptName"));
        c.setHeatC(Long.valueOf((String)item.get("heatC")));
        c.setHeatP(Long.valueOf((String)item.get("heatP")));
        return c;
    }

    @Override
    public GasCost fetchGasCost(String kaptCode, String searchDate) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1613000/AptIndvdlzManageCostServiceV2/getHsmpGasRentalFeeInfoV2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("kaptCode", kaptCode)
                .queryParam("searchDate", searchDate)
                .build().encode().toUri();

        @SuppressWarnings("unchecked")
        Map<String,Object> resp     = rt.getForObject(uri, Map.class);
        Map<String,Object> response = (Map<String,Object>) resp.get("response");
        Map<String,Object> body     = (Map<String,Object>) response.get("body");
        Map<String,Object> item     = (Map<String,Object>) body.get("item");

        GasCost g = new GasCost();
        g.setKaptCode((String)item.get("kaptCode"));
        g.setKaptName((String)item.get("kaptName"));
        g.setGasC(Long.valueOf((String)item.get("gasC")));
        g.setGasP(Long.valueOf((String)item.get("gasP")));
        return g;
    }
}
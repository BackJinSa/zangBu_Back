package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.VillaRent;
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
public class VillaRentServiceImpl implements VillaRentService{
    private final RestTemplate rt;

    @Value("${publicdata.rhrent.serviceKey}")
    private String serviceKey;

    @Override
    public List<VillaRent> fetchVillaRents(String lawdCd, String dealYmd) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1613000/RTMSDataSvcRHRent/getRTMSDataSvcRHRent")
                .queryParam("serviceKey", serviceKey)
                .queryParam("LAWD_CD", lawdCd)
                .queryParam("DEAL_YMD", dealYmd)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1500)
                .queryParam("_type", "json")
                .build().encode().toUri();

        @SuppressWarnings("unchecked")
        Map<String,Object> resp = rt.getForObject(uri, Map.class);

        @SuppressWarnings("unchecked")
        Map<String,Object> response = (Map<String,Object>) resp.get("response");
        @SuppressWarnings("unchecked")
        Map<String,Object> body = (Map<String,Object>) response.get("body");
        @SuppressWarnings("unchecked")
        Map<String,Object> items = (Map<String,Object>) body.get("items");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> list = (List<Map<String,Object>>) items.get("item");

        List<VillaRent> result = new ArrayList<>();
        for (Map<String,Object> m : list) {
            VillaRent it = new VillaRent();
            it.setMhouseNm((String) m.get("mhouseNm"));
            it.setBuildYear((Integer) m.get("buildYear"));
            it.setDealYear((Integer) m.get("dealYear"));
            it.setDealMonth((Integer) m.get("dealMonth"));
            it.setDealDay((Integer) m.get("dealDay"));
            it.setDeposit((String) m.get("deposit"));
            it.setMonthlyRent((Integer) m.get("monthlyRent"));
            it.setExcluUseAr((Double) m.get("excluUseAr"));
            it.setFloor((Integer) m.get("floor"));
            it.setHouseType((String) m.get("houseType"));
            it.setJibun(String.valueOf(m.get("jibun")));
            it.setUmdNm((String) m.get("umdNm"));
            it.setSggCd((Integer) m.get("sggCd"));
            result.add(it);
        }
        return result;
    }
}

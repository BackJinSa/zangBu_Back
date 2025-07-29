package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.OfficeRent;
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
public class OfficeRentServiceImpl implements OfficeRentService{
    private final RestTemplate rt;

    @Value("서비스 키 넣는 곳")
    private String serviceKey;

    @Override
    public List<OfficeRent> fetchOfficeRents(String lawdCd, String dealYmd, int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1613000/RTMSDataSvcOffiRent/getRTMSDataSvcOffiRent")
                .queryParam("serviceKey", serviceKey)
                .queryParam("LAWD_CD", lawdCd)
                .queryParam("DEAL_YMD", dealYmd)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("_type", "json")
                .build().encode().toUri();

        @SuppressWarnings("unchecked")
        Map<String,Object> resp     = rt.getForObject(uri, Map.class);
        Map<String,Object> response = (Map<String,Object>) resp.get("response");
        Map<String,Object> body     = (Map<String,Object>) response.get("body");
        Map<String,Object> items    = (Map<String,Object>) body.get("items");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> list = (List<Map<String,Object>>) items.get("item");

        List<OfficeRent> result = new ArrayList<>();
        for (Map<String,Object> m : list) {
            OfficeRent it = new OfficeRent();
            if (m.get("buildYear") != null)
                it.setBuildYear(((Number)m.get("buildYear")).intValue());
            it.setDealYear(((Number)m.get("dealYear")).intValue());
            it.setDealMonth(((Number)m.get("dealMonth")).intValue());
            it.setDealDay(((Number)m.get("dealDay")).intValue());
            it.setDeposit((String)m.get("deposit"));
            it.setMonthlyRent(((Number)m.get("monthlyRent")).intValue());
            it.setExcluUseAr(((Number)m.get("excluUseAr")).doubleValue());
            it.setFloor(((Number)m.get("floor")).intValue());
            it.setOffiNm((String)m.get("offiNm"));
            it.setJibun(m.get("jibun") != null ? m.get("jibun").toString() : null);
            it.setUmdNm((String)m.get("umdNm"));
            it.setSggCd(((Number)m.get("sggCd")).intValue());
            it.setSggNm((String)m.get("sggNm"));
            result.add(it);
        }
        return result;
    }
}

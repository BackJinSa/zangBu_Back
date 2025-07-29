package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.AptRent;
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
public class AptRentServiceImpl implements AptRentService {

    private final RestTemplate rt;

    @Value("서비스 키 값 넣는 곳, 그대로 붙여넣기")
    private String serviceKey;

    @Override
    @SuppressWarnings("unchecked")
    public List<AptRent> fetchAptRent(String lawdCd, String dealYmd) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1613000/RTMSDataSvcAptRent/getRTMSDataSvcAptRent")
                .queryParam("serviceKey", serviceKey)
                .queryParam("LAWD_CD", lawdCd)
                .queryParam("DEAL_YMD", dealYmd)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1500)
                .queryParam("_type", "json")
                .build().encode().toUri();

        Map<String, Object> resp = rt.getForObject(uri, Map.class);
        Map<String, Object> response = (Map<String, Object>) resp.get("response");
        Map<String, Object> body     = (Map<String, Object>) response.get("body");
        Map<String, Object> items    = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> rows = (List<Map<String, Object>>) items.get("item");

        List<AptRent> result = new ArrayList<>();
        for (Map<String, Object> m : rows) {
            AptRent a = new AptRent();
            a.setAptNm((String) m.get("aptNm"));
            a.setBuildYear((Integer) m.get("buildYear"));
            a.setDealYear((Integer) m.get("dealYear"));
            a.setDealMonth((Integer) m.get("dealMonth"));
            a.setDealDay((Integer) m.get("dealDay"));
            a.setDeposit((String) m.get("deposit"));
            a.setMonthlyRent((Integer) m.get("monthlyRent"));
            a.setExcluUseAr((Double) m.get("excluUseAr"));
            a.setFloor((Integer) m.get("floor"));
            // jibun, sggCd 는 숫자 형태
            a.setJibun((m.get("jibun") != null) ? (Integer) m.get("jibun") : null);
            a.setUmdNm((String) m.get("umdNm"));
            a.setSggCd((Integer) m.get("sggCd"));
            result.add(a);
        }
        return result;
    }
}
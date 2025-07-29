package bjs.zangbu.publicdata.service.law;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bjs.zangbu.publicdata.dto.law.LawCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class LawCodeServiceImpl implements LawCodeService {

    private final RestTemplate rt;

    @Value("서비스 키 값 넣는 곳, 그대로 붙여넣기")
    private String serviceKey;

    @Override
    public List<LawCode> fetchLawCodes(String locataddNm) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/1741000/StanReginCd/getStanReginCdList")
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1500)
                .queryParam("type", "json")
                .queryParam("locatadd_nm", locataddNm)
                .build().encode().toUri();

        @SuppressWarnings("unchecked")
        Map<String,Object> resp = rt.getForObject(uri, Map.class);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> stan = (List<Map<String,Object>>) resp.get("StanReginCd");
        // 두 번째 요소에 실제 row 데이터가 들어있습니다
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>) stan.get(1).get("row");

        List<LawCode> result = new ArrayList<>();
        for (Map<String,Object> m : rows) {
            LawCode c = new LawCode();
            c.setRegionCd((String)m.get("region_cd"));
            c.setLocataddNm((String)m.get("locatadd_nm"));
            c.setLocallowNm((String)m.get("locallow_nm"));
            result.add(c);
        }
        return result;
    }
}

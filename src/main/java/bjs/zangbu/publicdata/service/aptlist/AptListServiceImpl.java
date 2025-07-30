package bjs.zangbu.publicdata.service.aptlist;

import bjs.zangbu.publicdata.dto.aptlist.AptComplex;
import bjs.zangbu.publicdata.dto.aptlist.RoadAptComplex;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * RestTemplate 으로 공공데이터 OpenAPI 를 호출하고
 * 받은 JSON 을 DTO 로 변환하여 리턴
 */
@Service
@RequiredArgsConstructor
public class AptListServiceImpl implements AptListService {

    private final RestTemplate rt;

    @Value("서비스 키 사용하는 곳")
    private String serviceKey;

    /**
     * 공통 REST 호출 메서드
     * @param baseUrl 호출할 엔드포인트 URL
     * @param params  쿼리파라미터 맵
     * @return API 응답 전체를 Map 으로 리턴
     */
    private <T> Map<String,Object> fetch(String baseUrl, Map<String,String> params) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("serviceKey", serviceKey);
        params.forEach(b::queryParam);
        URI uri = b.build().encode().toUri();
        return rt.getForObject(uri, Map.class);
    }

    private List<AptComplex> extractComplexList(Map<String,Object> resp) {
        Map<String,Object> body = (Map)resp.get("response");
        List<Map<String,Object>> items = (List)((Map)body.get("body")).get("items");
        List<AptComplex> list = new ArrayList<>();
        for (Map<String,Object> m : items) {
            AptComplex a = new AptComplex();
            a.setKaptCode((String)m.get("kaptCode"));
            a.setKaptName((String)m.get("kaptName"));
            a.setBjdCode((String)m.get("bjdCode"));
            a.setAs1((String)m.get("as1"));
            a.setAs2((String)m.get("as2"));
            a.setAs3((String)m.get("as3"));
            a.setAs4((String)m.get("as4"));
            list.add(a);
        }
        return list;
    }

    // 전체 단지 조회 API 호출
    @Override
    public List<AptComplex> getTotalAptList(int pageNo, int numOfRows) {
        Map<String,String> p = Map.of(
                "pageNo", String.valueOf(pageNo),
                "numOfRows", String.valueOf(numOfRows)
        );
        Map<String,Object> resp = fetch(
                "https://apis.data.go.kr/1613000/AptListService3/getTotalAptList3", p);
        return extractComplexList(resp);
    }

    // 시도별 단지 조회
    @Override
    public List<AptComplex> getSidoAptList(String sidoCode, int pageNo, int numOfRows) {
        Map<String,String> p = Map.of(
                "sidoCode", sidoCode,
                "pageNo", String.valueOf(pageNo),
                "numOfRows", String.valueOf(numOfRows)
        );
        Map<String,Object> resp = fetch(
                "https://apis.data.go.kr/1613000/AptListService3/getSidoAptList3", p);
        return extractComplexList(resp);
    }

    // 시군구별 단지 조회
    @Override
    public List<AptComplex> getSigunguAptList(String sigunguCode, int pageNo, int numOfRows) {
        Map<String,String> p = Map.of(
                "sigunguCode", sigunguCode,
                "pageNo", String.valueOf(pageNo),
                "numOfRows", String.valueOf(numOfRows)
        );
        Map<String,Object> resp = fetch(
                "https://apis.data.go.kr/1613000/AptListService3/getSigunguAptList3", p);
        return extractComplexList(resp);
    }

    // 법정동별 단지 조회
    @Override
    public List<AptComplex> getLegaldongAptList(String bjdCode, int pageNo, int numOfRows) {
        Map<String,String> p = Map.of(
                "bjdCode", bjdCode,
                "pageNo", String.valueOf(pageNo),
                "numOfRows", String.valueOf(numOfRows)
        );
        Map<String,Object> resp = fetch(
                "https://apis.data.go.kr/1613000/AptListService3/getLegaldongAptList3", p);
        return extractComplexList(resp);
    }

    // 도로명 기반 단지 조회
    @Override
    public List<RoadAptComplex> getRoadnameAptList(String roadCode, int pageNo, int numOfRows) {
        Map<String,String> p = Map.of(
                "roadCode", roadCode,
                "pageNo", String.valueOf(pageNo),
                "numOfRows", String.valueOf(numOfRows)
        );
        Map<String,Object> resp = fetch(
                "https://apis.data.go.kr/1613000/AptListService3/getRoadnameAptList3", p);

        List<Map<String,Object>> items =
                (List)((Map)((Map)resp.get("response")).get("body")).get("items");
        List<RoadAptComplex> list = new ArrayList<>();
        for (Map<String,Object> m : items) {
            RoadAptComplex a = new RoadAptComplex();
            a.setKaptCode((String)m.get("kaptCode"));
            a.setKaptName((String)m.get("kaptName"));
            a.setDoroJuso((String)m.get("doroJuso"));
            list.add(a);
        }
        return list;
    }
}
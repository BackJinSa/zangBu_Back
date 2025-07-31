package bjs.zangbu.publicdata.service.aptinfo;

import bjs.zangbu.publicdata.dto.aptinfo.AptInfo;
import bjs.zangbu.publicdata.dto.aptinfo.DongInfo;
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
public class AptIdInfoServiceImpl implements AptIdInfoService {

    /** HTTP 호출용 RestTemplate */
    private final RestTemplate rt;

    /** odcloud API 인증키 */
    @Value("${publicdata.aptidinfo.serviceKey}")
    private String serviceKey;

    /**
     * 공통 GET 호출 메서드
     * @param baseUrl 엔드포인트 URL
     * @param params   쿼리 파라미터 맵
     * @return JSON 전체를 Map 으로 반환
     */
    private Map<String,Object> getForMap(String baseUrl, Map<String,String> params) {
        UriComponentsBuilder b = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                // 서비스키는 항상 추가
                .queryParam("serviceKey", serviceKey);
        // 나머지 파라미터 설정
        params.forEach(b::queryParam);
        URI uri = b.build(true).toUri();
        return rt.getForObject(uri, Map.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AptInfo> fetchAptInfo(String adres, int page, int perPage) {
        // 1) API 호출
        Map<String,String> p = Map.of(
                "page",    String.valueOf(page),
                "perPage", String.valueOf(perPage),
                // LIKE 검색 조건 key 는 cond[ADRES::LIKE]
                "cond[ADRES::LIKE]", adres
        );
        Map<String,Object> resp = getForMap(
                "https://api.odcloud.kr/api/AptIdInfoSvc/v1/getAptInfo", p);

        // 2) data 배열 추출
        List<Map<String,Object>> data = (List<Map<String,Object>>) resp.get("data");
        List<AptInfo> list = new ArrayList<>();

        // 3) DTO 로 매핑
        for (Map<String,Object> m : data) {
            AptInfo dto = new AptInfo();
            dto.setAdres((String)m.get("ADRES"));
            dto.setComplexGbCd((String)m.get("COMPLEX_GB_CD"));
            dto.setComplexNm1((String)m.get("COMPLEX_NM1"));
            dto.setComplexNm2((String)m.get("COMPLEX_NM2"));
            dto.setComplexNm3((String)m.get("COMPLEX_NM3"));
            dto.setComplexPk((String)m.get("COMPLEX_PK"));
            dto.setDongCnt((Integer)m.get("DONG_CNT"));
            dto.setPnu((String)m.get("PNU"));
            dto.setUnitCnt((Integer)m.get("UNIT_CNT"));
            dto.setUseaprDt((String)m.get("USEAPR_DT"));
            list.add(dto);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DongInfo> fetchDongInfo(String complexPk, int page, int perPage) {
        // 1) API 호출
        Map<String,String> p = Map.of(
                "page",    String.valueOf(page),
                "perPage", String.valueOf(perPage),
                // EQ 검색 조건 key 는 cond[COMPLEX_PK::EQ]
                "cond[COMPLEX_PK::EQ]", complexPk
        );
        Map<String,Object> resp = getForMap(
                "https://api.odcloud.kr/api/AptIdInfoSvc/v1/getDongInfo", p);

        // 2) data 배열 추출
        List<Map<String,Object>> data = (List<Map<String,Object>>) resp.get("data");
        List<DongInfo> list = new ArrayList<>();

        // 3) DTO 로 매핑
        for (Map<String,Object> m : data) {
            DongInfo dto = new DongInfo();
            dto.setComplexPk((String)m.get("COMPLEX_PK"));
            dto.setDongNm1((String)m.get("DONG_NM1"));
            dto.setDongNm2((String)m.get("DONG_NM2"));
            dto.setDongNm3((String)m.get("DONG_NM3"));
            dto.setGrndFlrCnt((Integer)m.get("GRND_FLR_CNT"));
            list.add(dto);
        }
        return list;
    }
}
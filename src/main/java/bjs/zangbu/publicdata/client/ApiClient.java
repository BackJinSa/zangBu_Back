package bjs.zangbu.publicdata.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiClient {

    private final RestTemplate restTemplate;

    @Value("${publicdata.serviceKey}")
    private String serviceKey;

    /**
     * 공공데이터 API 호출을 위한 공통 메서드
     * 
     * @param baseUrl API 기본 URL
     * @param params  쿼리 파라미터
     * @return API 응답을 Map으로 반환
     */
    public Map<String, Object> getForMap(String baseUrl, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("serviceKey", serviceKey);

        // 나머지 파라미터 추가
        params.forEach(builder::queryParam);

        URI uri = builder.build(true).toUri();
        return restTemplate.getForObject(uri, Map.class);
    }

    /**
     * 공공데이터 API 호출을 위한 공통 메서드 (응답 타입 지정)
     * 
     * @param baseUrl      API 기본 URL
     * @param params       쿼리 파라미터
     * @param responseType 응답 타입
     * @return 지정된 타입으로 응답 반환
     */
    public <T> T getForObject(String baseUrl, Map<String, String> params, Class<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("serviceKey", serviceKey);

        // 나머지 파라미터 추가
        params.forEach(builder::queryParam);

        URI uri = builder.build(true).toUri();
        return restTemplate.getForObject(uri, responseType);
    }
}

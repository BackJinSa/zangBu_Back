package bjs.zangbu.map.util;

import bjs.zangbu.map.vo.MapLocation;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CodefClient {
    private final RestTemplate rt = new RestTemplate();
    public MapLocation lookup(MapLocation vo) {
        // 실제로는 headers, URL, body 세팅 후 rt.postForEntity(...)
        // → 반환 JSON을 MapLocationVo로 매핑

        // 실제 구현 시:
        //  1) HTTP 헤더(Authorization, Content-Type 등) 설정
        //  2) URL, 요청 바디(vo) 설정
        //  3) rt.postForEntity(...) 또는 exchange(...) 호출
        //  4) 응답 JSON → MapLocation으로 매핑
        return rt.postForObject("https://api.codef.io/v1/map", vo, MapLocation.class);
    }
}

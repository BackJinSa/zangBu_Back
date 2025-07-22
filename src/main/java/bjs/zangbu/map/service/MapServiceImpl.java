package bjs.zangbu.map.service;

import bjs.zangbu.map.dto.request.MapRequest;
import bjs.zangbu.map.dto.response.MapResponse;
import bjs.zangbu.map.util.CodefClient;
import bjs.zangbu.map.vo.MapLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapService 구현체
 - CodefClient를 이용해 외부 지오코딩 API 호출 후 DTO 매핑 처리
 */

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {
    
    // 외부 API 호출용 클라이언트 (RestTemplate 래핑)
    private final CodefClient client;

    @Override
    public List<MapResponse> locate(List<MapRequest> reqs) {
        return reqs.stream()
                .map(r -> {

                    // 1) 요청 DTO → VO 변환
                    MapLocation vo = new MapLocation(
                            r.getAddress(),
                            null,
                            null,
                            r.getBuildingName()
                    );

                    // 2) 외부 API 호출하여 실제 위도/경도 값 조회
                    MapLocation res = client.lookup(vo);

                    // 3) 결과 VO → 응답 DTO로 변환
                    return new MapResponse(
                            res.getAddress(),
                            res.getLatitude(),
                            res.getLongitude(),
                            res.getBuildingName()
                    );
                })
                .toList();
    }
}

package bjs.zangbu.map.service;

import bjs.zangbu.map.dto.request.MapRequest;
import bjs.zangbu.map.dto.response.MapResponse;

import java.util.List;

/**
 * MapService 인터페이스
 - 외부 API 연동을 통해 주소 리스트를 위도/경도로 변환하는 기능을 정의
 */

public interface MapService {

    /**
     * 주소 리스트를 받아 각각 위도/경도 정보를 조회해 MapResponse 리스트로 반환
     *
     * @param requests 클라이언트로부터 전달된 주소·건물명 DTO 리스트
     * @return 위도·경도 정보가 포함된 MapResponse DTO 리스트
     */

    List<MapResponse> locate(List<MapRequest> requests);
}

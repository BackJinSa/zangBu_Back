package bjs.zangbu.codef.service;

import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.codef.encryption.CodefEncryption;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CodefServiceImpl implements CodefService {

    // CODEF 암호화 및 인증을 위한 유틸 클래스
    private final CodefEncryption codefEncryption;

    // CODEF SDK 객체 초기화에 사용
    private EasyCodef codef;

    // 애플리케이션이 시작되면 CODEF 인스턴스를 초기화
    @PostConstruct
    public void init() {
        codef = codefEncryption.getCodefInstance();
    }

    @Override
    public String priceInformation(BuildingRequest.ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {

        // 단일 건물 정보 매핑을 위한 map 구성
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0011"); // CODEF 공공부동산기관 코드
        map.put("searchGbn", request.getSearchGbn()); // 검색 구분 (전체/호수/동 등)
        map.put("complexNo", request.getComplexNo()); // 단지번호
        map.put("dong", request.getDong());           // 동 정보
        map.put("ho", request.getHo());               // 호 정보

        // 리스트로 감싸서 "buildingList" 구조에 맞게 구성
        List<HashMap<String, Object>> buildingList = new ArrayList<>();
        buildingList.add(map);

        // 전체 파라미터 구성
        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("buildingList", buildingList);

        // CODEF 공개 시세조회 API 요청 URL
        String url = "/v1/kr/public/lt/real-estate-board/market-price-information";

        // CODEF Demo 환경에 API 요청 전송
        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, parameterMap);

        // 응답 JSON 문자열 그대로 반환 (파싱은 다른 계층에서 수행)
        return response;
    }
}

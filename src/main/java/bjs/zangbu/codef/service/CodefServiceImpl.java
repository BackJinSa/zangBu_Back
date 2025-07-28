package bjs.zangbu.codef.service;
import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.codef.encryption.CodefEncryption;
import bjs.zangbu.codef.exception.CodefException;
import bjs.zangbu.codef.session.CodefAuthSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefMessageConstant;
import io.codef.api.EasyCodefServiceType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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

    // Redis 선언
    private final RedisTemplate<String, Object> redisTemplate;

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


        // CODEF 공개 시세조회 API 요청 URL
        String url = "/v1/kr/public/lt/real-estate-board/market-price-information";

        // CODEF Demo 환경에 API 요청 전송
        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        // 응답 JSON 문자열 그대로 반환 (파싱은 다른 계층에서 수행)
        return response;
    }

    @Override
    public String realEstateRegistrationIssuance(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException  {
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0002");
        map.put("phoneNo", );
        map.put("password", );
        map.put("inquiryType", "3");
        map.put("realtyType", "1");
        map.put("addr_sido", );
        map.put("addr_sigungu", );
        map.put("addr_roadName", );
        map.put("addr_buildingNumber", );
        map.put("dong", );
        map.put("ho", );
        map.put("ePrepayNo", );
        map.put("ePrepayPass", );
        map.put("issueType", "1");
        map.put("registerSummaryYN", "1");
        map.put("tradingYN", "1");
        map.put("jointMortgageJeonseYN", "1");

        // CODEF 공개 시세조회 API 요청 URL
        String url = "/v1/kr/public/lt/real-estate-board/market-price-information";

        // CODEF Demo 환경에 API 요청 전송
        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        return response;
    }

    @Override
    public String processSecureNo(String sessionKey, String secureNo) {
        CodefAuthSession session = (CodefAuthSession) redisTemplate.opsForValue().get(sessionKey);

        if (session == null) {
            throw new CodefException.CodefServiceException(EasyCodefMessageConstant.INVALID_SESSION);
        }

        HashMap<String, Object> param = new HashMap<>(session.getParameterMap());
        param.put("jobIndex", session.getJobIndex());
        param.put("threadIndex", session.getThreadIndex());
        param.put("jti", session.getJti());
        param.put("twoWayTimestamp", session.getTwoWayTimestamp());
        param.put("secureNo", secureNo);
        try {
            String result = codef.requestProduct(session.getProductUrl(), EasyCodefServiceType.DEMO, param);
            redisTemplate.delete(sessionKey);
            return result;
        } catch (Exception e) {
            throw new CodefException.CodefServiceException(EasyCodefMessageConstant.SERVER_PROCESSING_ERROR, e.getMessage());
        }
    }
}

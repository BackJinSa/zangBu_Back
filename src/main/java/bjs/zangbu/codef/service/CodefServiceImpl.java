package bjs.zangbu.codef.service;

import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.codef.dto.request.CodefRequest;
import bjs.zangbu.codef.dto.request.CodefRequest.AddressRequest;
import bjs.zangbu.codef.encryption.CodefEncryption;
import bjs.zangbu.codef.encryption.RSAEncryption;
import bjs.zangbu.codef.exception.CodefException;
import bjs.zangbu.codef.session.CodefAuthSession;
import bjs.zangbu.complexList.service.ComplexListService;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.deal.dto.request.EstateRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefMessageConstant;
import io.codef.api.EasyCodefServiceType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * CODEF API 연동을 위한 서비스 구현체.
 * ① 단순 1‑Way 상품 (priceInformation 등)
 * ② 2‑Way 인증 상품 (realEstateRegistrationIssuance 등)
 * ③ 3‑Way(보안문자) 상품 (processSecureNo 등)
 * 등 다양한 CODEF 상품 API 호출 및 응답 처리를 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class CodefServiceImpl implements CodefService {

    // CODEF 암호화 및 인증을 위한 유틸 클래스 (DI 주입)
    private final CodefEncryption codefEncryption;

    // CODEF SDK 객체 (실제 API 연동용)
    private EasyCodef codef;

    // 세션/인증 데이터를 임시 저장하는 Redis 연결 객체
    private final RedisTemplate<String, Object> redisTemplate;

    private final ComplexListService  complexListService;

    //RSA 암호화
    private final RSAEncryption rsaEncryption;

    // 서비스가 생성될 때 CODEF 인스턴스를 초기화
    @PostConstruct
    public void init() {
        codef = codefEncryption.getCodefInstance();
    }
    //codef 결제 관련
    @Value("${ePrepayNo}")
    private String ePrepayNo;
    @Value("${ePrepayPass}")
    private String ePrepayPass;

    /**
     * 아파트 단지(건물) 실거래 시세정보를 조회합니다.
     * 요청 DTO를 기반으로 파라미터 맵을 생성한 후, CODEF API에 요청하여 JSON 응답을 반환합니다.
     *
     * @param request 매물 상세 조회 요청 DTO
     * @return CODEF API로부터 받은 응답 JSON 문자열
     * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
     * @throws JsonProcessingException JSON 처리 중 발생하는 예외
     * @throws InterruptedException API 호출 지연 시 발생하는 예외
     */
    @Override
    public String FilterpriceInformation(BuildingRequest.ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        // 단일 건물 정보 매핑을 위한 map 구성
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0011"); // CODEF 공공부동산기관 코드
        map.put("searchGbn", "1");       // 검색 구분 ("1": 면적별 시세정보)
        map.put("complexNo", complexListService.getComplexNoByBuildingId(request.getBuildingId())); // 단지번호(필수)
        map.put("dong", request.getDong());
        map.put("dong", request.getHo()); // <-- 주의: 'dong' 키가 덮어쓰여지는 오류가 있습니다.
        // 'ho'에 대한 별도 키를 사용해야 합니다.

        // CODEF 시세조회 상품 URL
        String url = "/v1/kr/public/lt/real-estate-board/market-price-information";

        // CODEF DEMO API 호출
        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        // 응답 JSON 그대로 반환
        return response;
    }

    /**
     * 부동산 등기부 등본을 발급합니다.
     * 등본 발급에 필요한 정보를 파라미터 맵으로 생성한 후, CODEF API를 호출하여 결과를 반환합니다.
     *
     * @param request 등기부 등본 발급 요청 DTO
     * @return CODEF API로부터 받은 응답 JSON 문자열
     * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
     * @throws JsonProcessingException JSON 처리 중 발생하는 예외
     * @throws InterruptedException API 호출 지연 시 발생하는 예외
     */
    @Override
    public String realEstateRegistrationLeader(EstateRegistrationRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException  {
        String password = request.getBirth();
        password = password.substring(2);
        // RSA 암호화
        String encryptedPassword;
        try {
            encryptedPassword = rsaEncryption.encrypt(password);
        } catch (Exception e) {
            throw new RuntimeException("RSA 암호화 실패", e);
        }

        // 건물번호 로직
        String address_tmp = request.getAddress();
        String[] parts = address_tmp.split(" ");
        String bN = parts[parts.length - 1];

        // 등기부 등본 발급 파라미터 생성 (실제 값은 request에서 추출)
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0002");
        map.put("phoneNo", request.getPhone());
        map.put("password", encryptedPassword);
        map.put("inquiryType", "3");
        map.put("realtyType", "1");
        map.put("addr_sido", request.getSido());
        map.put("address", request.getAddress());
        map.put("dong", request.getDong());
        map.put("ho", request.getHo());
        map.put("addr_buildingNumber", bN);
        map.put("jointMortgageJeonseYN", "1");
        map.put("registerSummaryYN", "1");
        map.put("tradingYN", "1");
        map.put("ePrepayNo", ePrepayNo);
        map.put("ePrepayPass", ePrepayPass);
        map.put("issueType", "0");
        map.put("registerSummaryYN","1");
        map.put("addr_sigungu", request.getSigungu());
        map.put("addr_roadName", request.getRoadName());

        String url = " https://development.codef.io/v1/kr/public/ck/real-estate-register/status";

        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        return response;
    }

    /**
     * 부동산 등기부 실명 일치(소유자 인증) 검사를 수행합니다.
     * 부동산 실명 일치 여부를 확인하는 CODEF API를 호출하여 결과를 반환합니다.
     *
     * @param request 요청 데이터 객체
     * @return CODEF API로부터 받은 응답 JSON 문자열
     * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
     * @throws JsonProcessingException JSON 처리 중 발생하는 예외
     * @throws InterruptedException API 호출 지연 시 발생하는 예외
     */
    @Override
    public String RealEstateRegistrationRegister(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0002");
        map.put("uniqueNo", "");
        map.put("identity", "");

        String url = "/v1/kr/public/ck/real-estate-register/identity-matching";

        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        return response;
    }

    /**
     * 건축물대장을 발급하는 절차를 수행합니다.
     * 파라미터 맵을 구성하여 증명서 발급 CODEF API를 호출하고 JSON 응답을 반환합니다.
     *
     * @param request 건축물대장 발급 요청 DTO
     * @return CODEF API로부터 받은 응답 JSON 문자열
     * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
     * @throws JsonProcessingException JSON 처리 중 발생하는 예외
     * @throws InterruptedException API 호출 지연 시 발생하는 예외
     */
    @Override
    public String callBuildingRegister(BuildingRegisterRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        // codef api 주소
        final String url = "/v1/kr/public/mw/building-register/colligation";
        // 1차 요청 파라미터
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0001");
        map.put("loginType", "5");
        map.put("loginTypeLevel", "1");
        map.put("userName", request.getUserName());
        map.put("birthDate", request.getBirthDate());
        map.put("phoneNo", request.getPhoneNo());
        map.put("identity", request.getIdentity());
        map.put("identityEncYn", "Y");
        map.put("telecom","0");
        map.put("address", request.getAddress());
        map.put("zipCode", request.getZipCode());
        map.put("originDataYN", "1");
        map.put("secureNoTimeout", "170");

        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        return response;
    }

    /**
     * 3차 인증(보안문자 입력)을 처리합니다.
     * 프론트에서 받은 세션키로 Redis에서 세션 정보를 복구하고, 보안문자 값을 포함하여 CODEF API에 최종 요청합니다.
     * 요청 완료 후 Redis 세션 정보는 삭제됩니다.
     *
     * @param sessionKey Redis에 저장된 세션 정보의 키
     * @param secureNo 사용자가 입력한 보안문자
     * @return CODEF API로부터 받은 최종 응답 JSON 문자열
     */
    @Override
    public String processSecureNo(String sessionKey, String secureNo) {
        // Redis에서 2차 인증까지의 세션 정보 조회
        CodefAuthSession session = (CodefAuthSession) redisTemplate.opsForValue().get(sessionKey);

        // 세션이 없으면 예외 발생 (전역 Advice에서 처리)
        if (session == null) {
            throw new CodefException.CodefServiceException(EasyCodefMessageConstant.INVALID_SESSION);
        }

        // 기존 파라미터(1~2차 인증까지 모든 값)에 3차 인증용 추가값 합치기
        HashMap<String, Object> param = new HashMap<>(session.getParameterMap());
        param.put("jobIndex", session.getJobIndex());
        param.put("threadIndex", session.getThreadIndex());
        param.put("jti", session.getJti());
        param.put("twoWayTimestamp", session.getTwoWayTimestamp());
        param.put("secureNo", secureNo); // 사용자가 입력한 보안문자 값 추가

        try {
            String result = codef.requestProduct(session.getProductUrl(), EasyCodefServiceType.DEMO, param);
            // 인증 처리 후 Redis 세션 데이터 삭제 (보안/메모리 관리)
            redisTemplate.delete(sessionKey);
            return result;
        } catch (Exception e) {
            // 서버 처리 오류 발생 시 커스텀 예외로 전환 (전역 Advice에서 JSON 응답 처리)
            throw new CodefException.CodefServiceException(EasyCodefMessageConstant.SERVER_PROCESSING_ERROR, e.getMessage());
        }
    }


    /**
     * 시/군/동 주소로 건물 목록을 조회합니다.
     *
     * @param request 주소 정보를 담고 있는 {@link AddressRequest} DTO
     * @return CODEF API로부터 받은 건물 목록 JSON 문자열
     * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
     * @throws JsonProcessingException JSON 처리 중 발생하는 예외
     * @throws InterruptedException API 호출 지연 시 발생하는 예외
     */

    @Override
    public String justListInquiry(AddressRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        String url = "/v1/kr/public/lt/real-estate-board/estate-list";
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0011");
        map.put("addrSido", request.getAddrSido());
        map.put("addrSigun", request.getAddrSigun());
        map.put("addrDong", request.getAddrDong());

        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);
        return response;
    }

    /**
     * 특정 매물 ID로 단지 시세 정보를 조회합니다.
     *
     * @param buildingId 단지 시세 정보를 조회할 매물의 ID
     * @return CODEF API로부터 받은 시세 정보 JSON 문자열
     * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
     * @throws JsonProcessingException JSON 처리 중 발생하는 예외
     * @throws InterruptedException API 호출 지연 시 발생하는 예외
     */
    @Override
    public String priceInformation(Long buildingId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0011");
        map.put("searchGbn", "1");
        map.put("complexNo", complexListService.getComplexNoByBuildingId(buildingId));
        String url = "/v1/kr/public/lt/real-estate-board/market-price-information";
        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);
        return response;
    }

}
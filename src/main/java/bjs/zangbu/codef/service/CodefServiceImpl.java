package bjs.zangbu.codef.service;

import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.codef.encryption.CodefEncryption;
import bjs.zangbu.codef.encryption.RSAEncryption;
import bjs.zangbu.codef.exception.CodefException;
import bjs.zangbu.codef.session.CodefAuthSession;
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
 * ① 단순 1‑Way 상품   (priceInformation 등)
 * ② 2‑Way 인증 상품  (realEstateRegistrationIssuance 등)
 *     └ 내부적으로 CodefThread 이용, 2차 인증까지 자동 처리
 * ③ 3‑Way(보안문자)   ─> 컨트롤러 /coded/secure 엔드포인트로 연결
 *
 *  ⚠️TODO 표시 부분
 *     - map.put("phoneNo", ...);  와 같이 빈 값은
 *       실제 dto(request)에 맞춰 세팅 후 사용하세요.
 *     - productUrl 도 CODEF 가이드‑URL 로 교체 필요
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
     * 아파트 단지(건물) 실거래 시세정보 조회
     * - request 파라미터를 기반으로 파라미터 맵을 만든 후, CODEF API에 요청
     * - API 응답(JSON String) 그대로 리턴 (파싱/가공은 컨트롤러/프론트 쪽에서 별도 처리)
     */
    @Override
    public String priceInformation(BuildingRequest.ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {

        // 단일 건물 정보 매핑을 위한 map 구성
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0011"); // CODEF 공공부동산기관 코드
        map.put("searchGbn", "1");       // 검색 구분 ("1": 면적별 시세정보, etc)
        map.put("complexNo", request.getComplexNo()); // 단지번호(필수)
        map.put("dong", request.getDong());           // 동 정보
        map.put("ho", request.getHo());               // 호 정보

        // CODEF 시세조회 상품 URL
        String url = "/v1/kr/public/lt/real-estate-board/market-price-information";

        // CODEF DEMO API 호출
        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        // 응답 JSON 그대로 반환
        return response;
    }

    /**
     * 부동산 등기부 등본 발급
     * - 등본 발급에 필요한 정보(파라미터 map) 생성 후 CODEF API 호출
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
        //기관 코드 고정
        map.put("organization", "0002");
        // 휴대전화번호
        map.put("phoneNo", request.getPhone());
        // 인증서 비밀번호 , todo: yydd 암호화 로직
        map.put("password", encryptedPassword);
        // 조회 구분 (상품 별 설명 참고) -> 고정
        map.put("inquiryType", "3");
        // 집합건물 아마 고정
        map.put("realtyType", "1");
        map.put("addr_sido", request.getSido());
        map.put("address", request.getAddress());
        map.put("dong", request.getDong());
        map.put("ho", request.getHo());
        // 주소에서 마지막 숫자부분 파싱해야함
        map.put("addr_buildingNumber", bN);
        // 공동담보/전세목록 포함여부, 일단 1로 고정, 0:미포함 1:포함 (default='0')
        map.put("jointMortgageJeonseYN", "1");
        //1로 고정, 등기사항요약 출력 여부
        map.put("registerSummaryYN", "1");
        // 매매목록 포함 여부 일단 1로 고정, 0:미포함 1:포함 (default='0')
        map.put("tradingYN", "1");
        // 결제 내역, yml에 추가했음, 나중에 바꿀수 있음  todo : 확인하기
        map.put("ePrepayNo", ePrepayNo);
        map.put("ePrepayPass", ePrepayPass);
        // 발행구분 '0':발급 '1':열람 '2':고유번호조회
        //'3': 원문데이타로 결과처리, (default : '0')
        map.put("issueType", "0");
        //1로 고정, 등기사항요약 출력 여부
        map.put("registerSummaryYN","1");
        map.put("addr_sigungu", request.getSigungu());
        map.put("addr_roadName", request.getRoadName());

        // (※ 실제 이 API의 URL은 상품/가이드에 맞춰 확인 필요)
        String url = " https://development.codef.io/v1/kr/public/ck/real-estate-register/status";

        // CODEF API 요청
        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        return response;
    }

    /**
     * 부동산 등기부 실명 일치(소유자 인증) 검사
     * - 부동산 실명 일치여부 확인 API 호출
     */
    @Override
    public String RealEstateRegistrationRegister(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0002");
        map.put("uniqueNo", );  // 고유번호(부동산번호 등)
        map.put("identity", );  // 주민번호/사업자번호 등

        String url = "/v1/kr/public/ck/real-estate-register/identity-matching";

        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        return response;
    }
    /**
     * 건축물대장 발급 절차
     * - 파라미터맵을 구성하여 증명서 발급 CODEF API 요청 후, JSON 응답을 바로 반환
     */


    /**
     * 건축물대장 발급 절차
     * - 파라미터맵을 구성하여 증명서 발급 CODEF API 요청 후, JSON 응답을 바로 반환
     */
    @Override
    public String callBuildingRegister(BuildingRegisterRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        //todo : 날릴 준비
        // codef api 주소
        final String url = "/v1/kr/public/mw/building-register/colligation";
        // 1차 요청 파라미터
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0001"); // 기관 코드(고정)
        map.put("loginType", "5"); // 인증 절차(회원 간편인증 : 5 , 고정)
        map.put("loginTypeLevel", "1"); // 인증 유형(카카오 : 1 , 고정)
        map.put("userName", request.getUserName()); // 사용자 이름
        map.put("birthDate", request.getBirthDate()); // yymmdd
        map.put("phoneNo", request.getPhoneNo()); // 전화번호
        map.put("identity", request.getIdentity()); // 암호화된 주민 번호
        map.put("identityEncYn", "Y"); // 주민번호 암호화 여부
        map.put("telecom","0"); // 통신사 skt : 0, kt :1 , u+:2
        map.put("address", request.getAddress());
        map.put("zipCode", request.getZipCode());
//      map.put("dong", req.getDong());
//      map.put("ho", req.getHo());

        map.put("originDataYN", "1");
        map.put("secureNoTimeout", "170");

        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);

        return response;
    }

    /**
     * 납입증명서(세금납부 증명 등) 발급
     * - 파라미터맵을 구성하여 증명서 발급 CODEF API 요청 후, JSON 응답을 바로 반환
     */
    /*임시 비활성화*/
//    @Override
//    public String certificateOfPayment(Object request)
//            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("organization", "0001");
//        map.put("loginType", "6");
//        map.put("userName", );           // 사용자명
//        map.put("loginIdentity", );      // 로그인용 주민등록번호 등
//        map.put("loginBirthDate", );     // 생년월일
//        map.put("identityEncYn", );      // 주민번호 암호화 여부
//        map.put("loginTypeLevel", );     // 회원구분/인증단계
//        map.put("phoneNo", );            // 휴대폰 번호
//        map.put("isIdentityViewYN", );   // 실명확인 출력여부
//        map.put("isAddrViewYn", "0");    // 주소 출력여부
//        map.put("startDate", );          // 발급 대상 시작일
//        map.put("endDate", );            // 발급 대상 종료일
//
//        String url = "/v1/kr/public/nt/proof-issue/payment-proof";
//
//        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, map);
//
//        return response;
//    }

    /**
     * 3차 인증(보안문자 입력) 처리
     * - 프론트에서 받은 sessionKey로 Redis에서 세션 정보 복구
     * - secureNo(보안문자)값을 포함하여 CODEF API 최종 요청
     * - 요청 완료 후 Redis 세션 정보 삭제
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
}
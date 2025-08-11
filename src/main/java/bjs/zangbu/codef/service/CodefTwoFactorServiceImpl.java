package bjs.zangbu.codef.service;

import bjs.zangbu.addressChange.dto.request.ResRegisterCertRequest;
import bjs.zangbu.codef.encryption.CodefEncryption;
import bjs.zangbu.codef.encryption.RSAEncryption;
import bjs.zangbu.codef.thread.CodefThread;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.codef.api.EasyCodef;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;

import io.codef.api.EasyCodefServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 주민등록초본, 건축물대장, 주민등록증 진위확인 등 “2‑Way 인증” 계열 상품을 전담.
 * <p>
 * ✔️각 메서드 내부: - parameterMap 만들고 - CodefThread(codef, parameter, index, url) 로 spawn -
 * Thread.sleep(10000) 은 데모 환경에서는 필수, 운영에서는 조정 가능
 * <p>
 * ✔️CodefThread 역할 - 1차 호출(Return code == CF‑03002 && continue2Way == true) → 2차 자동 호출 - 두 응답을 각각
 * firstResponse / secondResponse 로 보관
 */
@Service
@RequiredArgsConstructor
public class CodefTwoFactorServiceImpl implements CodefTwoFactorService {

  // CODEF 암호화 및 인증을 위한 유틸 클래스 (DI)
  private final CodefEncryption codefEncryption;

  // CODEF SDK 객체 (실제 API 연동 용)
  private EasyCodef codef;
  //rsa암호화
  private final RSAEncryption rsaEncryption;

  // 서비스 인스턴스화 시 CODEF 객체 한 번만 초기화
  @PostConstruct
  public void init() {
    codef = codefEncryption.getCodefInstance();
  }

  /**
   * 주민등록초본 발급 (2차 인증 자동 포함) - 필수 파라미터 추출 및 Map 생성 후 CODEF 주민등록초본 발급 API 요청 - 각 스레드별로 2차 인증(FIDO/휴대폰
   * 등)까지의 응답값만 반환
   */
  @Override
  public String residentRegistrationCertificate(ResRegisterCertRequest request)
          throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    String productUrl = "/v1/kr/public/mw/resident-registration-abstract/issuance";

    // 1. 1차 인증 파라미터 맵 생성
    HashMap<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("organization", "0001");
    parameterMap.put("loginType", "5");
    parameterMap.put("identityEncYn", "Y");
    parameterMap.put("birthDate", request.getBirth());
    parameterMap.put("identity", request.getIdentity());
    parameterMap.put("timeout", "170"); // 2차 인증 대기 시간
    parameterMap.put("userName", request.getName());
    parameterMap.put("loginTypeLevel", "1");
    parameterMap.put("phoneNo", request.getPhone());
    parameterMap.put("personalInfoChangeYN", "0"); //개인 인적사항 변경 내용 -> 미포함
    parameterMap.put("pastAddrChangeYN", "1"); // 과거 주소 조회 -> 포함
    parameterMap.put("nameRelationYN", "0");
    parameterMap.put("militaryServiceYN", "0");
    parameterMap.put("overseasKoreansIDYN", "0");
    parameterMap.put("isIdentityViewYn", "0");
    parameterMap.put("originDataYN", "0");
    parameterMap.put("telecom", request.getTelecom());

    // 2. 1차 인증 요청
    String firstResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);
    HashMap<String, Object> responseMap = new ObjectMapper().readValue(firstResponse, HashMap.class);
    HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
    String code = (String) resultMap.get("code");

    // 3. 2차 인증이 필요한 경우 (CF-03002)
    if ("CF-03002".equals(code)) {
      System.out.println("주민등록초본 1차 인증 완료. 2차 인증을 위해 대기합니다...");
      Thread.sleep(60000);

      HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

      // 2차 인증 파라미터 맵 생성
      HashMap<String, Object> twoWayInfoParams = new HashMap<>();
      twoWayInfoParams.put("jobIndex", dataMap.get("jobIndex"));
      twoWayInfoParams.put("threadIndex", dataMap.get("threadIndex"));
      twoWayInfoParams.put("jti", dataMap.get("jti"));
      twoWayInfoParams.put("twoWayTimestamp", ((Number) dataMap.get("twoWayTimestamp")).longValue());
//      twoWayParams.put("timeout", parameterMap.get("timeout")); // 2차 인증 대기 시간

      // ⭐ 1차 파라미터 맵에 `twoWayInfo` 객체를 추가합니다.
      parameterMap.put("twoWayInfo", twoWayInfoParams);

      // ⭐ 2차 인증 요청에 필요한 파라미터만 담은 맵을 생성합니다.
      HashMap<String, Object> secondRequestParams = new HashMap<>(parameterMap);
      secondRequestParams.put("twoWayInfo", twoWayInfoParams);

      // ⭐ is2Way 파라미터도 추가
      secondRequestParams.put("simpleAuth", "1");
      secondRequestParams.put("secureNo", "");
      secondRequestParams.put("secureNoRefresh", "");
      secondRequestParams.put("singedData", "");
      secondRequestParams.put("is2Way", true);

      System.out.println("2차 인증 요청 파라미터 = " + secondRequestParams);

      // 4. 2차 인증 스레드 시작
      CodefThread t = new CodefThread(codef, secondRequestParams, productUrl);
      t.start();
      t.join(); // 스레드 종료까지 대기

      return t.getSecondResponse();
    }

    // 2차 인증이 필요 없으면 바로 결과 반환
    return firstResponse;
  }

  /**
   * 일반건축물대장 소유자/세대주 실명 일치 검사 (2차 인증 자동 포함) - codefThread 올바르게 순차 실행, 결과값만 스트링으로 묶어 반환
   */
  @Override
  public String generalBuildingLeader(BuildingRegisterRequest request)
          throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    String productUrl = "/v1/kr/public/mw/building-register/colligation";

    // 1. 1차 인증 파라미터 맵 생성
    HashMap<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("organization", "0001");
    parameterMap.put("loginType", "5");
    parameterMap.put("loginTypeLevel", "1");
    parameterMap.put("userName", request.getUserName());
    parameterMap.put("birthDate", request.getBirthDate());
    parameterMap.put("phoneNo", request.getPhoneNo());
    parameterMap.put("inquiryType", "1");
    parameterMap.put("identity", request.getIdentity());
    parameterMap.put("identityEncYn", "Y");
    parameterMap.put("telecom", request.getTelecom());
    parameterMap.put("address", request.getAddress());
    parameterMap.put("type","0");
    parameterMap.put("zipCode", request.getZipCode());
    parameterMap.put("dong", request.getDong());
    parameterMap.put("ho", request.getHo());
    parameterMap.put("originDataYN", "1");
    parameterMap.put("secureNoTimeout", "170");

    // 2. 1차 인증 요청
    String firstResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);
    HashMap<String, Object> responseMap = new ObjectMapper().readValue(firstResponse, HashMap.class);
    HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
    String code = (String) resultMap.get("code");

    // 3. 2차 인증이 필요한 경우 (CF-03002)
    if ("CF-03002".equals(code)) {
      System.out.println("건축물대장 1차 인증 완료. 2차 인증을 위해 1분간 대기합니다...");
      Thread.sleep(60000);

      HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

      // ⭐ twoWayInfo 파라미터 맵을 생성하고 2차 인증 정보를 담습니다.
      HashMap<String, Object> twoWayInfoParams = new HashMap<>();
      twoWayInfoParams.put("jobIndex", dataMap.get("jobIndex"));
      twoWayInfoParams.put("threadIndex", dataMap.get("threadIndex"));
      twoWayInfoParams.put("jti", dataMap.get("jti"));
      twoWayInfoParams.put("twoWayTimestamp", ((Number) dataMap.get("twoWayTimestamp")).longValue());

      // ⭐ 1차 파라미터 맵에 `twoWayInfo` 객체를 추가합니다.
      parameterMap.put("twoWayInfo", twoWayInfoParams);

      // ⭐ 2차 인증 요청에 필요한 파라미터만 담은 맵을 생성합니다.
      HashMap<String, Object> secondRequestParams = new HashMap<>(parameterMap);
      secondRequestParams.put("twoWayInfo", twoWayInfoParams);

      // ⭐ is2Way 파라미터도 추가
      secondRequestParams.put("is2Way", true);
      secondRequestParams.put("simpleAuth", "1");

      System.out.println("2차 인증 요청 파라미터 = " + secondRequestParams);

      // 4. 2차 인증 스레드 시작
      CodefThread t = new CodefThread(codef, secondRequestParams, productUrl);
      t.start();
      t.join(); // 스레드 종료까지 대기
      System.out.println("초본 응답 json " + t.getSecondResponse());
      return t.getSecondResponse();
    }

    // 2차 인증이 필요 없으면 바로 결과 반환
    return firstResponse;
  }
  /**
   * 주민등록증 진위확인 (2차 인증 상품) - 실명인증/진위확인 CODEF API 요청 - 각종 파라미터(주소, 발급일 등) 포함해야 인증 정상 동작
   */
  @Override
  public String residentRegistrationAuthenticityConfirmation(AuthRequest.VerifyCodefRequest request)
          throws Exception {
    String productUrl = "/v1/kr/public/mw/identity-card/check-status";

    // 1. 1차 인증 파라미터 맵 생성
    HashMap<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("organization", "0002");
    parameterMap.put("loginType", "6");
    parameterMap.put("loginTypeLevel", "1");
    parameterMap.put("phoneNo", request.getPhone());
    parameterMap.put("loginUserName", request.getName());
    parameterMap.put("loginBirthDate", request.getBirth());
    parameterMap.put("birthDate", request.getBirth());
    parameterMap.put("loginIdentity", URLEncoder.encode(rsaEncryption.encrypt(request.getIdentity()), StandardCharsets.UTF_8));
    parameterMap.put("identity", URLEncoder.encode(rsaEncryption.encrypt(request.getIdentity()), StandardCharsets.UTF_8));
    parameterMap.put("userName", request.getName());
    parameterMap.put("issueDate", request.getIssueDate());
    parameterMap.put("identityEncYn", "Y");
    parameterMap.put("timeout", "170"); // 2차 인증 대기 시간

    // 2. 1차 인증 요청
    String firstResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);
    HashMap<String, Object> responseMap = new ObjectMapper().readValue(firstResponse, HashMap.class);
    HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
    String code = (String) resultMap.get("code");

    // 3. 2차 인증이 필요한 경우 (CF-03002)
    if ("CF-03002".equals(code)) {
      System.out.println("주민등록증 진위확인 1차 인증 완료. 2차 인증을 위해 대기합니다...");
      HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

      // 2차 인증 파라미터 맵 생성
      HashMap<String, Object> twoWayParams = new HashMap<>();
      twoWayParams.put("jobIndex", dataMap.get("jobIndex"));
      twoWayParams.put("threadIndex", dataMap.get("threadIndex"));
      twoWayParams.put("jti", dataMap.get("jti"));
      twoWayParams.put("twoWayTimestamp", ((Number) dataMap.get("twoWayTimestamp")).longValue());
      twoWayParams.put("timeout", parameterMap.get("timeout"));

      // 4. 2차 인증 스레드 시작
      CodefThread t = new CodefThread(codef, twoWayParams, productUrl);
      t.start();
      t.join(); // 스레드 종료까지 대기

      return t.getSecondResponse();
    }

    // 2차 인증이 필요 없으면 바로 결과 반환
    return firstResponse;
  }

  /**
   * 지방세 납부증명서 발급
   * - 필수 파라미터로 CODEF 지방세 납세증명 API 호출
   */
  /*임시 비활성화*/
//    @Override
//    public String localTaxProof(Object request)
//            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
//        String productUrl = "/v1/kr/public/mw/localtax-payment-certificate/inquiry";
//        List<CodefThread> threadList = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            // 지방세 납부증명서 API 파라미터
//            HashMap<String, Object> parameterMap = new HashMap<>();
//            parameterMap.put("organization", "0001");
//            parameterMap.put("loginType", "6");         // 인증유형
//            parameterMap.put("userName", );             // 사용자 성명
//            parameterMap.put("identity", );             // 주민번호 등
//            parameterMap.put("identityEncYn", "Y");     // 암호화 여부
//            parameterMap.put("birthDate", );            // 생년월일
//            parameterMap.put("loginTypeLevel", );       // 인증레벨
//            parameterMap.put("phoneNo", );              // 휴대폰번호
//            parameterMap.put("address", );              // 주소(일부 인증상품 필요)
//            parameterMap.put("phoneNo1", );             // 추가 휴대폰번호
//
//            CodefThread t = new CodefThread(codef, parameterMap, i, productUrl);
//            t.start();
//            threadList.add(t);
//            Thread.sleep(10000);
//        }
//
//        StringBuilder sb = new StringBuilder();
//        for (CodefThread t : threadList) {
//            t.join();
//            if (t.getSecondResponse() != null) {
//                sb.append(t.getSecondResponse());
//            } else if (t.getFirstResponse() != null) {
//                sb.append(t.getFirstResponse());
//            }
//            sb.append("\n");
//        }
//        return sb.toString();
//    }
}
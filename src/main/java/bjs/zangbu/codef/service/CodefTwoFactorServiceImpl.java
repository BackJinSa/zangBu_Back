package bjs.zangbu.codef.service;

import bjs.zangbu.addressChange.dto.request.ResRegisterCertRequest;
import bjs.zangbu.codef.encryption.CodefEncryption;
import bjs.zangbu.codef.encryption.RSAEncryption;
import bjs.zangbu.codef.session.CodefAuthSession;
import bjs.zangbu.codef.thread.CodefThread;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.codef.api.EasyCodef;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import javax.annotation.PostConstruct;

import io.codef.api.EasyCodefServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

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
    //todo : 주민등록번호 뒷자리rsa 암호화 해야함
    // 1. 1차 인증 파라미터 맵 생성
    HashMap<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("organization", "0002");
    parameterMap.put("loginType", "6");
    parameterMap.put("loginTypeLevel", "1");
    parameterMap.put("telecom", request.getTelecom());
    parameterMap.put("phoneNo", request.getPhone());
    parameterMap.put("loginUserName", request.getName());
    parameterMap.put("loginBirthDate", request.getBirth());
    parameterMap.put("birthDate", request.getBirth());
    parameterMap.put("loginIdentity", request.getIdentity());
    parameterMap.put("identity", request.getIdentity());
    parameterMap.put("userName", request.getName());
    parameterMap.put("issueDate", request.getIssueDate());
    parameterMap.put("identityEncYn", "Y");
//    parameterMap.put("timeout", "170"); // 2차 인증 대기 시간

    // 2. 1차 인증 요청
    String firstResponse = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);
    System.out.println("firstResponse = " + firstResponse);
    HashMap<String, Object> responseMap = new ObjectMapper().readValue(firstResponse, HashMap.class);
    HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
    String code = (String) resultMap.get("code");

    // 3. 2차 인증이 필요한 경우 (CF-03002)
    if ("CF-03002".equals(code)) {
      System.out.println("주민등록증 진위확인 1차 인증 완료. 2차 인증을 위해 대기합니다...");
      Thread.sleep(60000);
      HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

      // 2차 인증 파라미터 맵 생성
      HashMap<String, Object> twoWayParams = new HashMap<>();
      twoWayParams.put("jobIndex", dataMap.get("jobIndex"));
      twoWayParams.put("threadIndex", dataMap.get("threadIndex"));
      twoWayParams.put("jti", dataMap.get("jti"));
      twoWayParams.put("twoWayTimestamp", ((Number) dataMap.get("twoWayTimestamp")).longValue());

      // ⭐ 2차 인증 요청에 필요한 파라미터만 담은 맵을 생성합니다.
      HashMap<String, Object> secondRequestParams = new HashMap<>(parameterMap);
      secondRequestParams.put("twoWayInfo", twoWayParams);
      secondRequestParams.put("is2Way", true);
      secondRequestParams.put("simpleAuth", "1");
      System.out.println("secondRequestParams = " + secondRequestParams);
      // 4. 2차 인증 스레드 시작
//      CodefThread t1 = new CodefThread(codef, secondRequestParams, productUrl);
//      t1.start();
//      t1.join(); // 스레드 종료까지 대기

      //2차 인증 이후 캡차png 프론트에 보내기
      String secondResponse = codef.requestCertification(productUrl, EasyCodefServiceType.DEMO, secondRequestParams);
      System.out.println("2차 인증 성공 , 보안 문자를 위해 기다립니다...");
      System.out.println("secondResponse = " + secondResponse);
      HashMap<String, Object> responseMap2 = new ObjectMapper().readValue(secondResponse, HashMap.class);

      HashMap<String, Object> resultMap2 = (HashMap<String, Object>) responseMap2.get("result");
      // 인증 갱신 값 파싱을 위한 해시맵
      HashMap<String, Object> data2 = (HashMap<String, Object>) responseMap2.get("data");
      // 코드 상태 번호
      String code2 = (String) resultMap2.get("code");
      System.out.println("code2 = " + code2);
      if(code2.equals("CF-00000")) {
        return objectMapper.writeValueAsString(data2);
      }
      // 여기서 CF-03002 + captcha 단계여야 함
      if (!"CF-03002".equals(code2)) {
        // 이미 최종 성공/실패로 넘어간 케이스 → 그대로 전달하거나 에러 처리
        // 필요 시 아래 라인처럼 바로 반환:
        throw new IllegalStateException("예상치 못한 2-Way 상태: " + code2);
      }

      // png를 위한 파싱을 위한 해시맵
      HashMap<String, Object> extraInfo = (HashMap<String, Object>) data2.get("extraInfo");
      // 보안문자 png
      String captchaDataUri = (String) extraInfo.get("reqSecureNo"); // "data:image/png;base64,...."
      // 보안문자
      String method = (String) data2.getOrDefault("method", "secureNo");

      // Redis에 세션 저장
      CodefAuthSession session = new CodefAuthSession();
      session.setParameterMap(secondRequestParams); // 원본 파라미터 보존
      session.setJobIndex(((Number) data2.get("jobIndex")).intValue());
      session.setThreadIndex(((Number) data2.get("threadIndex")).intValue());
      session.setJti((String) data2.get("jti"));
      session.setTwoWayTimestamp(((Number) data2.get("twoWayTimestamp")).longValue());
      session.setProductUrl(productUrl);

      // 세션키 생성 + TTL 설정(예: timeout 170초 + 버퍼 60초)
      String sessionKey = "identity:" + UUID.randomUUID();
      long ttlSeconds = 170 + 60;

      redisTemplate
              .opsForValue()
              .set(sessionKey, session, Duration.ofSeconds(ttlSeconds));

      // 프론트로 전달할 응답(JSON)
      Map<String, Object> output = new HashMap<>();
      output.put("sessionKey", sessionKey);
      output.put("captcha", captchaDataUri); // Data URI 그대로
      return objectMapper.writeValueAsString(output); // json으로 반환
    }

    // 2차 인증이 필요 없으면 바로 결과 반환
    return firstResponse;
  }
}
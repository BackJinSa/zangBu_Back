package bjs.zangbu.codef.service;

import bjs.zangbu.addressChange.dto.request.ResRegisterCertRequest;
import bjs.zangbu.codef.encryption.CodefEncryption;
import bjs.zangbu.codef.encryption.RSAEncryption;
import bjs.zangbu.codef.thread.CodefThread;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.codef.api.EasyCodef;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
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

    String identity = request.getIdentity();
    String urlEncoded = URLEncoder.encode(identity, StandardCharsets.UTF_8);

    List<CodefThread> threadList = new ArrayList<>();
    // 2번을 예시로 여러 동시 요청 처리 가능 (for문 반복수 조정 시 멀티 인증 확장 가능)
    for (int i = 0; i < 2; i++) {
      // 주민등록초본 API 파라미터 셋팅
      HashMap<String, Object> parameterMap = new HashMap<>();
      parameterMap.put("organization", "0001");      // 기관코드
      parameterMap.put("loginType", "5");            // 로그인 유형 (통합인증/휴대폰 등)
      parameterMap.put("identityEncYn", "Y"); // 주민번호 암호화 여부
      parameterMap.put("birthDate", request.getBirth());               // 생년월일
      parameterMap.put("identity", urlEncoded);                // 주민등록번호
      parameterMap.put("timeout", "170");
      parameterMap.put("userName", request.getName());                // 사용자 이름
      parameterMap.put("loginTypeLevel", "1");          // 인증 레벨
      parameterMap.put("phoneNo", request.getPhone());                 // 휴대폰 번호
//            parameterMap.put("addrSido", );                // 주소(시/도)
//            parameterMap.put("addrSiGunGu", );             // 주소(시군구)
      parameterMap.put("personalInfoChangeYN", "0"); // 개인정보 변경이력 포함 여부
      parameterMap.put("pastAddrChangeYN", "1");     // 과거 주소 포함 여부
      parameterMap.put("nameRelationYN", "0");       // 친족관계 포함 여부
      parameterMap.put("militaryServiceYN", "0");    // 병역사항 포함 여부
      parameterMap.put("overseasKoreansIDYN", "0");  // 해외교포 여부
      parameterMap.put("isIdentityViewYn", "0");     // 주민등록번호 표기 여부
      parameterMap.put("originDataYN", "0");         // 원본 데이터 표기 여부
      parameterMap.put("telecom", request.getTelecom());

      // 스레드별로 CODEF API 호출(병렬 실행)
      CodefThread t = new CodefThread(codef, parameterMap, i, productUrl);
      t.start();
      threadList.add(t);
      Thread.sleep(10000); // (각 스레드 간 간격, 실전에서는 필요시 조정)
    }

    // 모든 스레드의 인증 결과 JSON 반환(2차 응답 있으면 2차, 없으면 1차)
    StringBuilder sb = new StringBuilder();
    for (CodefThread t : threadList) {
      t.join();
      if (t.getSecondResponse() != null) {
        sb.append(t.getSecondResponse());
      } else if (t.getFirstResponse() != null) {
        sb.append(t.getFirstResponse());
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * 일반건축물대장 소유자/세대주 실명 일치 검사 (2차 인증 자동 포함) - codefThread 올바르게 순차 실행, 결과값만 스트링으로 묶어 반환
   */
  @Override
  public String generalBuildingLeader(BuildingRegisterRequest request)
      throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    String productUrl = "/v1/kr/public/mw/building-register/colligation";

    String orgIdentity = request.getIdentity();
    String urlEncoded = URLEncoder.encode(orgIdentity, StandardCharsets.UTF_8);

    List<CodefThread> threadList = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      // 건축물대장 실명 일치 확인 파라미터
      // 1차 요청 파라미터
      HashMap<String, Object> parameterMap = new HashMap<>();
      parameterMap.put("organization", "0001"); // 기관 코드(고정)
      parameterMap.put("loginType", "5"); // 인증 절차(회원 간편인증 : 5 , 고정)
      parameterMap.put("loginTypeLevel", "1"); // 인증 유형(카카오 : 1 , 고정)
      parameterMap.put("userName", request.getUserName()); // 사용자 이름
      parameterMap.put("birthDate", request.getBirthDate()); // yymmdd
      parameterMap.put("phoneNo", request.getPhoneNo()); // 전화번호
      parameterMap.put("identity", urlEncoded); // 암호화된 주민 번호
      parameterMap.put("identityEncYn", "Y"); // 주민번호 암호화 여부
      parameterMap.put("telecom", request.getTelecom()); // 통신사 skt : 0, kt :1 , u+:2
      parameterMap.put("address", request.getAddress());
      parameterMap.put("zipCode", request.getZipCode());
      parameterMap.put("dong", request.getDong());
      parameterMap.put("ho", request.getHo());

      parameterMap.put("originDataYN", "1");
      parameterMap.put("secureNoTimeout", "170");

      CodefThread t = new CodefThread(codef, parameterMap, i, productUrl);
      t.start();
      threadList.add(t);
      Thread.sleep(10000);
    }

    StringBuilder sb = new StringBuilder();
    for (CodefThread t : threadList) {
      t.join();
      if (t.getSecondResponse() != null) {
        sb.append(t.getSecondResponse());
      } else if (t.getFirstResponse() != null) {
        sb.append(t.getFirstResponse());
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * 주민등록증 진위확인 (2차 인증 상품) - 실명인증/진위확인 CODEF API 요청 - 각종 파라미터(주소, 발급일 등) 포함해야 인증 정상 동작
   */
  @Override
  public String residentRegistrationAuthenticityConfirmation(AuthRequest.VerifyCodefRequest request)
      throws Exception {
    String productUrl = "/v1/kr/public/mw/identity-card/check-status";

    String identity = request.getIdentity();
    String RSAEncoded = rsaEncryption.encrypt(identity);
    String urlEncoded = URLEncoder.encode(RSAEncoded, StandardCharsets.UTF_8);

    List<CodefThread> threadList = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      // 주민등록증 진위확인용 파라미터
      HashMap<String, Object> parameterMap = new HashMap<>();
      parameterMap.put("organization", "0002");   // 기관코드 : 고정
      parameterMap.put("loginType", "6");         // 간편인증 : 고정
      parameterMap.put("loginTypeLevel", "1");     // 인증레벨 : 고정
      parameterMap.put("phoneNo", request.getPhone());              // 휴대폰번호 member.phone
      parameterMap.put("loginUserName", request.getName());        // 로그인 사용자명 member.name
      parameterMap.put("loginBirthDate", request.getBirth());       // 로그인 생년월일 member.birth
      parameterMap.put("birthDate", request.getBirth());            // 실제 생년월일 , member.birth
      parameterMap.put("loginIdentity", urlEncoded);        // 로그인 아이디(주민번호) member.identity
      parameterMap.put("identity", urlEncoded);             // 주민등록번호 member.identity url 인코딩해야함
      parameterMap.put("userName", request.getName());             // 사용자명 member.name
      parameterMap.put("issueDate", request.getIssueDate());            // 주민등록증 발급일자
      parameterMap.put("identityEncYn", "Y");     // 암호화 여부

      CodefThread t = new CodefThread(codef, parameterMap, i, productUrl);
      t.start();
      threadList.add(t);
      Thread.sleep(10000);
    }

    StringBuilder sb = new StringBuilder();
    for (CodefThread t : threadList) {
      t.join();
      if (t.getSecondResponse() != null) {
        sb.append(t.getSecondResponse());
      } else if (t.getFirstResponse() != null) {
        sb.append(t.getFirstResponse());
      }
      sb.append("\n");
    }
    return sb.toString();
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

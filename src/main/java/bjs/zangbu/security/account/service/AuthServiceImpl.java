package bjs.zangbu.security.account.service;

import bjs.zangbu.codef.encryption.RSAEncryption;
import bjs.zangbu.codef.service.CodefTwoFactorService;
import bjs.zangbu.security.account.client.PassApiClient;
import bjs.zangbu.security.account.dto.request.AuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.EmailAuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.LoginRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.ResetPassword;
import bjs.zangbu.security.account.dto.request.AuthRequest.SignUp;
import bjs.zangbu.security.account.dto.request.AuthRequest.VerifyCodefRequest;
import bjs.zangbu.security.account.dto.response.AuthResponse.VerifyCodefResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.LoginResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.TokenResponse;
import bjs.zangbu.security.account.mapper.AuthMapper;
import bjs.zangbu.security.account.vo.Member;
import bjs.zangbu.security.util.JwtProcessor;
import io.jsonwebtoken.JwtException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {

  final PasswordEncoder passwordEncoder;
  final AuthMapper mapper;
  private final PassApiClient passApiClient; // PASS와 통신하는 클라이언트
  final JwtProcessor jwtProcessor;
  private final CodefTwoFactorService codefTwoFactorService;
  private final RSAEncryption rsaEncryption;

  private final RedisTemplate<String, Object> redisTemplate;
  private static final String REFRESH_TOKEN_PREFIX = "refresh:"; //prefix
  private static final String SIGNUP_VERIFY_PREFIX = "signup:verify:";

  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    Member member = mapper.findByEmail(loginRequest.getEmail());

    if (member == null || !passwordEncoder.matches(loginRequest.getPassword(),
        member.getPassword())) {
      throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    // 회원 찾기 성공 시 JWT 발급
    String accessToken = jwtProcessor.generateAccessToken(member.getEmail(),
        member.getRole().name());
    String refreshToken = jwtProcessor.generateRefreshToken(member.getEmail());

    //redis에 refresh 토큰 저장
    redisTemplate.opsForValue().set(
        REFRESH_TOKEN_PREFIX + member.getEmail(),   // Key
        refreshToken,                     // Value
        jwtProcessor.getRefreshTokenExpiration(), // refresh 토큰 유효시간
        TimeUnit.MILLISECONDS
    );

    // Redis에 로그인 상태 저장 (만료시간 2시간)
    redisTemplate.opsForValue().set(
        "login:" + member.getEmail(), // key
        "true",                       // value
        Duration.ofHours(2)           // TTL: 2시간
    );

    return new LoginResponse(accessToken, refreshToken, member.getRole());
  }

  //로그아웃
  @Override
  public void logout(String email) {
    try {
      //redis에 저장된 리프레시 토큰 삭제
      redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);

      //  Redis에서 로그인 상태 삭제
      redisTemplate.delete("login:" + email);
    } catch (JwtException | IllegalArgumentException e) {
      throw new JwtException("유효하지 않은 토큰입니다.");
    }
  }
  
  @Override
  public String codefAuthentication(AuthRequest.VerifyCodefRequest request) throws Exception {
    String rawResponse = codefTwoFactorService.
        residentRegistrationAuthenticityConfirmation(request);
    String decodedJson = URLDecoder.decode(rawResponse, StandardCharsets.UTF_8);
    
    //본인인증 성공 시
    //String sessionId = saveToRedis(request); --> redis에 데이터 저장

    return null; //todo : 로직 설계 해야 함
  }

  //세션 아이디 발급 및 redis에 데이터 저장
  //codef 검증 후 성공일 때만 사용하도록
  private String saveToRedis(AuthRequest.VerifyCodefRequest request) throws Exception {
    //세션 아이디 생성
    String sessionId = UUID.randomUUID().toString();
    String key = SIGNUP_VERIFY_PREFIX + sessionId;

    //주민번호 암호화해서 넣기
    String encIdentity = rsaEncryption.encrypt(request.getIdentity());

    Map<String, String> toSave = new HashMap<>();
    toSave.put("status", "Y"); // 본인인증 성공 후에만 저장하므로 Y
    toSave.put("name", request.getName());
    toSave.put("birth", request.getBirth());
    toSave.put("identity", encIdentity);
    toSave.put("phone", request.getPhone());
    toSave.put("telecom", request.getTelecom());
    toSave.put("issueDate", request.getIssueDate());

    redisTemplate.opsForHash().putAll(key, toSave);
    redisTemplate.expire(key, Duration.ofMinutes(10L)); //10분

    //세션 아이디 반환
    return sessionId;
  }

  //회원가입
  @Override
  @Transactional
  public void signUp(SignUp signUpRequest) throws Exception {
    if (signUpRequest.getSessionId() == null || signUpRequest.getSessionId().isBlank())
      throw new IllegalArgumentException("본인인증 세션 ID가 없습니다.");

    //저장해둔 세션 키로 redis에서 값 가져오기
    String key = SIGNUP_VERIFY_PREFIX + signUpRequest.getSessionId();
    Map<Object, Object> saved = redisTemplate.opsForHash().entries(key);
    if (saved == null || saved.isEmpty())
      throw new IllegalStateException("본인인증 정보가 만료되었거나 존재하지 않습니다.");

    //Y일 때가 본인인증 성공 상태
    String status = (String) saved.get("status");
    if (!"Y".equalsIgnoreCase(status))
      throw new IllegalStateException("본인인증이 완료되지 않았습니다.");

    if (isEmailDuplicated(signUpRequest.getEmail())) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    if (isNicknameDuplicated(signUpRequest.getNickname())) {
      throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
    }

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
    signUpRequest.setPassword(encodedPassword);

    //redis에 저장된 값 가져와서 세팅
    signUpRequest.setName((String) saved.get("name"));
    signUpRequest.setBirth((String) saved.get("birth"));
    signUpRequest.setIdentity((String) saved.get("identity")); //암호화 된 값
    signUpRequest.setTelecom((String) saved.get("telecom"));
    signUpRequest.setPhone((String) saved.get("phone"));

    //todo : identity 암호화
//    String identity = signUpRequest.getIdentity();
//    String RSAEncoded = rsaEncryption.encrypt(identity);
//    signUpRequest.setIdentity(RSAEncoded);

    // Member 생성/저장
    String memberId = UUID.randomUUID().toString();
    Member member = SignUp.toVo(signUpRequest, encodedPassword, memberId);

    int result = mapper.insertMember(member);
    if (result == 0) {
      throw new IllegalStateException("회원가입에 실패하였습니다.");
    }

    // 일회성 인증 정보 삭제
    redisTemplate.delete(key);
  }

  //이메일 찾기(이름, 휴대폰 번호로)
  @Override
  public EmailAuthResponse findEmail(EmailAuthRequest request) {
    String email = mapper.findEmailByNameAndPhone(request.getName(), request.getPhone());

    if (email == null) {
      throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
    }

    return new EmailAuthResponse(email);
  }


  //이메일 중복 확인
  @Override
  public boolean isEmailDuplicated(String email) {
    int count = mapper.countByEmail(email);
    return count > 0;
  }

  //닉네임 중복 확인
  @Override
  public boolean isNicknameDuplicated(String nickname) {
    int count = mapper.countByNickname(nickname);
    return count > 0;
  }

  //본인인증
//  @Override
//  public VerifyCodefResponse verifyAuthenticity(VerifyCodefRequest request) {
//    //PassAPI 호출하고 반환하는 역할 -> 호출 실패나 응답에 대한 예외 처리
//
//    try {
//      // PASS API에 전달할 값들로 JSON 생성
//      Map<String, String> payload = new HashMap<>();
//      payload.put("name", request.getName()); //이름
//      payload.put("identity", request.getIdentity()); //주민번호
//      payload.put("phone", request.getPhone()); //전화번호
//
//      // 실제 PASS API 호출 (RestTemplate, WebClient 등 사용)해서 응답 받음
//      ResponseEntity<VerifyCodefResponse> response = passApiClient.sendVerification(payload);
//
//      //응답 body가 null인 경우
//      //500
//      if (response.getBody() == null) {
//        throw new IllegalStateException("PASS API에서 유효한 응답을 받지 못했습니다.");
//      }
//      // 성공 시 결과 반환
//      return response.getBody();
//      // "Y"/"N" 여부 판단은 Controller에서 하고, y일때만 세션에 저장하도록 처리
//
//    } catch (Exception e) {
//      throw new RuntimeException("PASS 본인인증 중 오류가 발생했습니다.");
//    }
//  }

  //비밀번호 재설정
  @Override
  public void resetPassword(ResetPassword request, HttpSession session) {
    // 1. 세션에서 이메일 조회 -> 인증 되었을 때만 변경할 수 있음
    String verifiedEmail = (String) session.getAttribute("verifiedEmail");

    if (verifiedEmail == null) {
      throw new IllegalStateException("본인인증이 필요한 상태입니다.");
    }

    // 2. 회원 존재 확인
    Member member = mapper.findByEmail(verifiedEmail);

    if (member == null) {
      throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
    }

    // 3. 비밀번호 인코딩 후 새 비밀번호로 업데이트
    String encodedPassword = passwordEncoder.encode(request.getNewPassword());

    int result = mapper.updatePassword(member.getEmail(), encodedPassword);
    if (result == 0) {
      throw new IllegalStateException("비밀번호를 변경하는데 실패했습니다.");
    }

    // 4. 인증 상태 세션에서 제거 --일회성 인증
    session.removeAttribute("verifiedEmail");
  }

  //토큰 재발급
  public TokenResponse reissue(String refreshToken) {
    //refresh 토큰 유효성 검사
    if (!jwtProcessor.validateToken(refreshToken)) {
      throw new JwtException("유효하지 않은 refresh 토큰입니다.");
    }

    //refresh 토큰 비교할 때 필요한 email 추출
    String email = jwtProcessor.getEmail(refreshToken);

    //redis에 저장된 refresh 토큰과 일치 여부 확인 todo: object로 타입변환했으니까 로그 찍어봐야할듯 0805
    String storedRefreshToken = (String) redisTemplate.opsForValue()
        .get(REFRESH_TOKEN_PREFIX + email);

    //저장된 refresh 토큰 없으면
    if (storedRefreshToken == null) {
      throw new IllegalStateException("refresh 토큰이 서버에 존재하지 않습니다.");
    }

    //있으면, 일치하는지 확인
    if (!storedRefreshToken.equals(refreshToken)) {
      throw new JwtException("서버에 저장된 refresh 토큰과 일치하지 않습니다.");
    }

    Member member = mapper.findByEmail(email);
    if (member == null) {
      throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
    }

    //새로운 access, refresh 토큰 발급 후 반환(refresh 토큰도 함께 갱신)
    String newAccessToken = jwtProcessor.generateAccessToken(email, member.getRole().name());
    String newRefreshToken = jwtProcessor.generateRefreshToken(email);

    //redis에 새 refresh 토큰 세팅
    redisTemplate.opsForValue().set(
        REFRESH_TOKEN_PREFIX + email,
        newRefreshToken,
        jwtProcessor.getRefreshTokenExpiration(), // refresh 토큰 유효시간
        TimeUnit.MILLISECONDS
    );

    return new TokenResponse(newAccessToken, newRefreshToken);
  }
}

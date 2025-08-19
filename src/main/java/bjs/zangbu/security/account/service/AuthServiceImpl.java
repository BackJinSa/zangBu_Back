package bjs.zangbu.security.account.service;

import bjs.zangbu.addressChange.service.AddressChangeService;
import bjs.zangbu.codef.encryption.RSAEncryption;
import bjs.zangbu.codef.service.CodefTwoFactorService;
import bjs.zangbu.security.account.dto.request.AuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.EmailAuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.SignUp;
import bjs.zangbu.security.account.dto.response.AuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.TokenResponse;
import bjs.zangbu.security.account.mapper.AuthMapper;
import bjs.zangbu.security.account.vo.Member;
import bjs.zangbu.security.util.JwtProcessor;
import io.jsonwebtoken.JwtException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {

  final AddressChangeService addressChangeService;
  final PasswordEncoder passwordEncoder;
  final AuthMapper mapper;
  final JwtProcessor jwtProcessor;
  private final CodefTwoFactorService codefTwoFactorService;

  private final RedisTemplate<String, Object> redisTemplate;
  private static final String REFRESH_TOKEN_PREFIX = "refresh:"; //prefix
  private static final String SIGNUP_VERIFY_PREFIX = "signup:verify:";
  private static final String LOGIN_TOKEN_PREFIX = "login:";

  private static final String RESET_TOKEN_PREFIX = "reset:token:";
  private static final Duration RESET_TTL = Duration.ofMinutes(15);


  //로그아웃
  @Override
  public void logout(String email) {
    try {
      //redis에 저장된 리프레시 토큰 삭제
      redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);

      //  Redis에서 로그인 상태 삭제
      redisTemplate.delete(LOGIN_TOKEN_PREFIX + email);
    } catch (JwtException | IllegalArgumentException e) {
      throw new JwtException("유효하지 않은 토큰입니다.");
    }
  }
  
  @Override
  public String codefAuthentication(AuthRequest.VerifyCodefRequest request) throws Exception {
    String rawResponse = codefTwoFactorService.
        residentRegistrationAuthenticityConfirmation(request);
    String decodedJson = URLDecoder.decode(rawResponse, StandardCharsets.UTF_8);


    return null; //todo : 로직 설계 해야 함
  }

  public String cacheVerification(AuthRequest.VerifyCodefRequest request) throws Exception {
    //세션 아이디 생성
    String sessionId = UUID.randomUUID().toString();
    String key = SIGNUP_VERIFY_PREFIX + sessionId;

    Map<String, String> toSave = new HashMap<>();
    toSave.put("status", "Y");
    toSave.put("name", request.getName());
    toSave.put("birth", request.getBirth());
    toSave.put("identity", request.getIdentity());
    toSave.put("phone", request.getPhone());
    toSave.put("telecom", request.getTelecom());
    toSave.put("issueDate", request.getIssueDate());

    // Redis 저장 및 TTL
    redisTemplate.opsForHash().putAll(key, toSave);
    redisTemplate.expire(key, Duration.ofMinutes(10L)); //10분

    log.info("[VERIFY SAVED] key={}", key);

    //세션 아이디 반환
    return sessionId;
  }

  //회원가입
  @Override
  @Transactional
  public void signUp(SignUp signUpRequest) throws Exception {
    if (signUpRequest.getSessionId() == null || signUpRequest.getSessionId().isBlank())
      throw new IllegalArgumentException("본인인증 세션 ID가 없습니다.");

    log.info("[SIGNUP] sessionId={}", signUpRequest.getSessionId());
    //저장해둔 세션 키로 redis에서 값 가져오기
    String key = SIGNUP_VERIFY_PREFIX + signUpRequest.getSessionId();
    Map<Object, Object> saved = redisTemplate.opsForHash().entries(key);
    log.info("[SIGNUP] redis loaded size={}, keys={}", saved.size(), saved.keySet());
    if (saved == null || saved.isEmpty())
      throw new IllegalStateException("본인인증 정보가 만료되었거나 존재하지 않습니다.");

    //Y일 때가 본인인증 성공 상태
    String status = (String) saved.get("status");
    log.info("[SIGNUP] status={}", status);
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
    String normalizedPhone = request.getPhone().replaceAll("\\D", "");
    request.setPhone(normalizedPhone);

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

  @Override
  public AuthResponse.PasswordVerifyResponse verifyPasswordFlow(String sessionId) {
    String key = SIGNUP_VERIFY_PREFIX + sessionId;
    Map<Object, Object> saved = redisTemplate.opsForHash().entries(key);
    if (saved == null || saved.isEmpty()) {
      throw new IllegalArgumentException("인증 세션이 만료되었거나 존재하지 않습니다.");
    }

    String name  = (String) saved.get("name");
    String phone = (String) saved.get("phone");

    String email = mapper.findEmailByNameAndPhone(name, phone);
    boolean exists = (email != null);

    String resetToken = null;
    if (exists) {
      resetToken = Base64.getUrlEncoder().withoutPadding()
              .encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
      String tKey = RESET_TOKEN_PREFIX + resetToken;
      redisTemplate.opsForValue().set(tKey, email, RESET_TTL);
    }

    redisTemplate.delete(key); // 일회성 삭제
    return new AuthResponse.PasswordVerifyResponse(exists, sessionId, resetToken);
  }


  // 새로 추가: 토큰 기반 비밀번호 재설정(세션 안 씀)
  @Override
  public void resetPasswordByToken(String resetToken, String newPassword) {
    String tKey = RESET_TOKEN_PREFIX + resetToken;
    String email = (String) redisTemplate.opsForValue().get(tKey);
    if (email == null) {
      throw new IllegalStateException("재설정 토큰이 만료되었거나 유효하지 않습니다.");
    }

    Member member = mapper.findByEmail(email);
    if (member == null) {
      redisTemplate.delete(tKey);
      throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
    }

    String encoded = passwordEncoder.encode(newPassword);
    int updated = mapper.updatePassword(email, encoded);
    if (updated == 0) {
      throw new IllegalStateException("비밀번호를 변경하는데 실패했습니다.");
    }

    // 일회성 토큰 삭제
    redisTemplate.delete(tKey);
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

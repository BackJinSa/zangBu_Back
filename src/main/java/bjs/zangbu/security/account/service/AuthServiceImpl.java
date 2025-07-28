package bjs.zangbu.security.account.service;

import bjs.zangbu.security.account.client.PassApiClient;
import bjs.zangbu.security.account.dto.request.AuthRequest.EmailAuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.LoginRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.SignUp;
import bjs.zangbu.security.account.dto.request.AuthRequest.VerifyRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.ResetPassword;
import bjs.zangbu.security.account.dto.response.AuthResponse.TokenResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.LoginResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.AuthVerify;
import bjs.zangbu.security.account.mapper.AuthMapper;
import bjs.zangbu.security.account.vo.Member;
import bjs.zangbu.security.util.JwtProcessor;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    final PasswordEncoder passwordEncoder;
    final AuthMapper mapper;
    private final PassApiClient passApiClient; // PASS와 통신하는 클라이언트
    final JwtProcessor jwtProcessor;

    //redis 설정 있다고 가정 --- 추후 수정
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        var member = mapper.findByEmail(loginRequest.getEmail());
        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 발급
        String accessToken = jwtProcessor.generateAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtProcessor.generateRefreshToken(member.getEmail());

        //redis에 refresh 토큰 저장
        redisTemplate.opsForValue().set(
                "refresh:" + member.getEmail(),   // Key
                refreshToken,                     // Value
                jwtProcessor.getRefreshTokenExpiration(), // refresh 토큰 유효시간
                TimeUnit.MILLISECONDS
        );

        return new LoginResponse(accessToken, refreshToken, member.getRole());
    }

    //로그아웃
    @Override
    public void logout(String accessToken) {
        //accesstoken으로 사용자 증명
        String email = jwtProcessor.getEmail(accessToken);
        //redis에 저장된 리프레시 토큰 삭제
        redisTemplate.delete("refresh:" + email);
    }

    @Override
    public void signUp(SignUp signUpRequest) {
        if (isEmailDuplicated(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (isNicknameDuplicated(signUpRequest.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        signUpRequest.setPassword(encodedPassword);

        Member member = SignUp.toVo(signUpRequest, encodedPassword);

        mapper.insertMember(member);
    }

    @Override
    public EmailAuthResponse findEmail(EmailAuthRequest request) {
        String email = mapper.findEmailByNameAndPhone(request.getName(), request.getPhone());

        if (email == null) {
            throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
        }

        return new EmailAuthResponse(email);
    }


    @Override
    public boolean isEmailDuplicated(String email) {
        int count = mapper.countByEmail(email);
        return count > 0;
    }

    @Override
    public boolean isNicknameDuplicated(String nickname) {
        int count = mapper.countByNickname(nickname);
        return count > 0;
    }

    @Override
    public AuthVerify verifyAuthenticity(VerifyRequest request) {
        // PASS API에 전달할 값들로 JSON 생성
        Map<String, String> payload = new HashMap<>();
        payload.put("name", request.getName()); //이름
        payload.put("identity", request.getIdentity()); //주민번호
        payload.put("phone", request.getPhone()); //전화번호

        // 실제 PASS API 호출 (RestTemplate, WebClient 등 사용)
        ResponseEntity<AuthVerify> response = passApiClient.sendVerification(payload);

        // 결과 반환
        return response.getBody();  // "Y"/"N" 여부 판단은 Controller에서
    }

    @Override
    public void resetPassword(ResetPassword request, HttpSession session) {
        // 1. 세션에서 인증된 이메일 조회
        String verifiedEmail = (String) session.getAttribute("verifiedEmail");

        if (verifiedEmail == null) {
            throw new IllegalStateException("인증이 필요한 상태입니다.");
        }

        // 2. 회원 존재 확인
        Member member = mapper.findByEmail(verifiedEmail);

        if (member == null) {
            throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
        }

        // 3. 비밀번호 인코딩 후 업데이트
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        mapper.updatePassword(member.getEmail(), encodedPassword);

        // 4. 인증 상태 세션에서 제거 (1회용)
        session.removeAttribute("verifiedEmail");
    }

    //토큰 재발급
    public TokenResponse reissue(String refreshToken){
        //refresh 토큰 유효성 검사
        if(!jwtProcessor.validateToken(refreshToken)){
            throw new JwtException("유효하지 않은 refresh 토큰입니다.");
        }

        //refresh 토큰 비교할 때 필요한 email 추출
        String email = jwtProcessor.getEmail(refreshToken);

        //redis에 저장된 refresh 토큰과 일치 여부 확인
        String storedRefreshToken = redisTemplate.opsForValue.get("refresh:"+email);
        //저장된 refresh 토큰 없으면
        if(storedRefreshToken == null){
            throw new IllegalStateException("refresh 토큰이 서버에 존재하지 않습니다.");
        }

        //있으면, 일치하는지 확인
        if(!storedRefreshToken.equals(refreshToken)){
            throw new JwtException("서버에 저장된 refresh 토큰과 일치하지 않습니다.");
        }

        Member member = mapper.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
        }

        //새로운 access, refresh 토큰 발급 후 반환
        String newAccessToken = jwtProcessor.generateAccessToken(email, member.getRole().name());
        String newRefreshToken = jwtProcessor.generateRefreshToken(email);

        //redis에 새 refresh 토큰 세팅
        redisTemplate.opsForValue().set("refresh:"+email, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}

package bjs.zangbu.security.account.controller;

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
import bjs.zangbu.security.account.service.AuthService;
import bjs.zangbu.security.account.vo.CustomUser;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 인증 관련 HTTP 요청을 처리하는 REST 컨트롤러.
 * 로그인, 로그아웃, 아이디(이메일) 찾기, 비밀번호 재설정, 본인인증, 회원가입, 이메일/닉네임 중복 확인, 토큰 재발급 기능을 제공합니다.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = "Auth API", value = "인증 관련 기능 API")
public class AuthController {

  private final AuthService authService;

  /**
   * 로그인 요청 처리.
   * {@code POST /auth/login} 엔드포인트를 통해 사용자의 로그인을 처리하고, 성공 시 JWT 토큰(Access Token, Refresh Token)을 발급합니다.
   * Refresh Token은 HTTP Only 쿠키로, Access Token은 응답 본문으로 전달됩니다.
   *
   * @param request 로그인 정보를 담고 있는 {@link LoginRequest} DTO (이메일, 비밀번호)
   * @param response HTTP 응답 객체 (Refresh Token 쿠키 설정을 위해 사용)
   * @return 로그인 성공 시 {@link LoginResponse}와 함께 200 OK 응답, 실패 시 400 Bad Request 또는 500 Internal Server Error 응답
   */
//  @ApiOperation(
//          value = "로그인",
//          notes = "로그인 성공 시 토큰을 발급하여 반환합니다.",
//          response = LoginResponse.class
//  )
//  @ApiResponses({
//          @ApiResponse(code = 200, message = "로그인 성공했습니다."),
//          @ApiResponse(code = 400, message = "아이디 또는 비밀번호가 일치하지 않습니다"),
//          @ApiResponse(code = 500, message = "서버에서 로그인을 처리하는데 오류가 발생했습니다.")
//  })
//  @PostMapping("/login")
//  public ResponseEntity<?> login(
//          @ApiParam(value = "로그인 요청 DTO (이메일, 비밀번호 입력)", required = true)
//          @RequestBody LoginRequest request,
//          HttpServletResponse response) {
//    try {
//      LoginResponse loginResponse = authService.login(request);
//
//      //쿠키에 refresh 토큰 담기
//      Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
//      refreshCookie.setHttpOnly(true); //JS 접근 방지 - 장기 인증 수단이므로 클라이언트 측 js에서 접근 못하게 해야 함
//      refreshCookie.setSecure(true); //Https에서만 전송 가능하도록
//      refreshCookie.setPath("/"); //전체 경로에 대해 유효함
//      refreshCookie.setMaxAge(7 * 24 * 60 * 60); //7일간 유효(초단위)
//
//      response.addCookie(refreshCookie);
//
//      //access 토큰은 클라이언트에 바디로 전달
//      //refresh 토큰은 쿠키로 숨겨서 클라이언트에 저장
//      return ResponseEntity.ok(
//              new LoginResponse(loginResponse.getAccessToken(), null, loginResponse.getRole()));
//
//    } catch (IllegalArgumentException e) {
//      //400 에러 - 아이디/비밀번호 일치하지 않음
//      return ResponseEntity.badRequest().body(e.getMessage());
//    } catch (Exception e) {
//      // 500 에러
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//              .body("서버에서 로그인을 처리하는데 오류가 발생했습니다.");
//    }
//  }

  /**
   * 로그아웃 요청 처리.
   * {@code POST /auth/logout} 엔드포인트를 통해 현재 로그인된 사용자를 로그아웃 처리하고, Refresh Token 쿠키를 삭제합니다.
   *
   * @param customUser 인증된 사용자 정보 (Spring Security Principal)
   * @param response HTTP 응답 객체 (Refresh Token 쿠키 삭제를 위해 사용)
   * @return 로그아웃 성공 시 200 OK 응답, 실패 시 400 Bad Request 또는 500 Internal Server Error 응답
   */
  @ApiOperation(
          value = "로그아웃",
          notes = "로그아웃 성공 시 토큰을 삭제합니다."
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "로그아웃에 성공했습니다."),
          @ApiResponse(code = 400, message = "로그아웃에 실패했습니다."),
          @ApiResponse(code = 500, message = "서버에서 로그아웃을 처리하는데 오류가 발생했습니다.")
  })
  @PostMapping("/logout")
  public ResponseEntity<?> logout(
          @RequestHeader(value = "Authorization", required = false) String authHeader,
          @ApiIgnore
          @AuthenticationPrincipal CustomUser customUser,
          HttpServletResponse response
  ) {
    log.info("[LOGOUT] raw Authorization={}", authHeader);
    log.info("[LOGOUT] principal={}", customUser);
    try {
      String email = customUser.getUsername();

      authService.logout(email);

      //refresh 토큰 쿠키 삭제 - js에서 접근 못하므로, 서버에서 삭제 응답 필요
      Cookie deleteCookie = new Cookie("refreshToken", null); // null로 설정
      deleteCookie.setMaxAge(0);             // 쿠키 즉시 만료
      deleteCookie.setPath("/");             // 경로 일치
      deleteCookie.setHttpOnly(true);        // 보안 설정 유지(js 접근 방지)
      deleteCookie.setSecure(true);          // https 환경에서만
      response.addCookie(deleteCookie);      // 삭제 쿠키 클라이언트에 전달

      return ResponseEntity.ok().build();
    } catch (JwtException | IllegalArgumentException e) {
      //400
      return ResponseEntity.badRequest().body("로그아웃에 실패했습니다.");
    } catch (Exception e) {
      //500
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("서버에서 로그아웃을 처리하는데 오류가 발생했습니다.");
    }
  }

  /**
   * 아이디(이메일) 찾기 요청 처리.
   * {@code POST /auth/email} 엔드포인트를 통해 이름과 휴대폰 번호를 기반으로 이메일을 찾습니다.
   *
   * @param request 이메일 찾기 정보를 담고 있는 {@link EmailAuthRequest} DTO (이름, 전화번호)
   * @param customUser 인증된 사용자 정보 (현재 사용되지 않지만, 필요 시 확장 가능)
   * @return 이메일 찾기 성공 시 {@link EmailAuthResponse}와 함께 200 OK 응답, 실패 시 400 Bad Request 또는 500 Internal Server Error 응답
   */
  @ApiOperation(
          value = "이메일 찾기",
          notes = "이름과 휴대폰 번호 기반으로 이메일을 찾습니다.",
          response = EmailAuthResponse.class
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "이메일을 찾는데 성공했습니다."),
          @ApiResponse(code = 400, message = "이메일을 찾는데 실패했습니다."),
          @ApiResponse(code = 500, message = "서버에서 이메일을 찾는데 오류가 발생했습니다.")
  })
  @PostMapping("/email")
  public ResponseEntity<?> findEmail(
          @ApiParam(value = "이메일 찾기 요청 DTO (이름, 전화번호 입력)", required = true)
          @RequestBody EmailAuthRequest request,
          @ApiIgnore
          @AuthenticationPrincipal CustomUser customUser) {

    try {
      EmailAuthResponse response = authService.findEmail(request);
      return ResponseEntity.ok(response); //200 성공
    } catch (IllegalArgumentException e) {
      //400
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일을 찾는데 실패했습니다.");
    } catch (Exception e) {
      //500
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("서버에서 이메일을 찾는데 오류가 발생했습니다.");
    }
  }

  /**
   * 비밀번호 재설정 요청 처리.
   * {@code POST /auth/password} 엔드포인트를 통해 본인인증을 마친 후 새로운 비밀번호로 재설정합니다.
   * 세션에 저장된 인증 상태를 확인하여 비밀번호 변경을 허용합니다.
   *
   * @param customUser 인증된 사용자 정보 (현재 사용되지 않지만, 필요 시 확장 가능)
   * @param request 비밀번호 재설정 정보를 담고 있는 {@link ResetPassword} DTO (새 비밀번호)
   * @param session HTTP 세션 객체 (본인인증 상태 확인을 위해 사용)
   * @return 비밀번호 변경 성공 시 200 OK 응답, 실패 시 400 Bad Request 또는 500 Internal Server Error 응답
   */
  @ApiOperation(
          value = "비밀번호 재설정",
          notes = "본인인증을 마친 후 비밀번호를 재설정할 수 있게 합니다."
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "비밀번호를 변경하는데 성공했습니다."),
          @ApiResponse(code = 400, message = "비밀번호를 변경하는데 실패했습니다."),
          @ApiResponse(code = 500, message = "서버에서 비밀번호 변경을 처리하는데 오류가 발생했습니다.")
  })
  @PostMapping("/password")
  public ResponseEntity<?> resetPassword(
          @ApiIgnore
          @AuthenticationPrincipal CustomUser customUser,
          @ApiParam(value = "비밀번호 재설정 요청 DTO (새 비밀번호 입력)", required = true)
          @RequestBody ResetPassword request,
          HttpSession session) {

    try {
      authService.resetPassword(request, session);
      return ResponseEntity.ok().build(); //200
    } catch (IllegalStateException | IllegalArgumentException e) {
      //400
      return ResponseEntity.badRequest().body("비밀번호를 변경하는데 실패했습니다.");
    } catch (Exception e) {
      //500
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("서버에서 비밀번호 변경을 처리하는데 오류가 발생했습니다.");
    }
  }

  /**
   * 본인인증 요청 처리.
   * {@code POST /auth/verify} 엔드포인트를 통해 본인인증을 수행하고, 성공 시 세션에 인증 상태를 저장합니다.
   *
   * @param customUser 인증된 사용자 정보 (현재 사용되지 않지만, 필요 시 확장 가능)
   * @param request 본인인증 정보를 담고 있는 {@link VerifyRequest} DTO
   * @param session HTTP 세션 객체 (인증 상태 저장을 위해 사용)
   * @return 본인인증 성공 시 {@link VerifyCodefResponse}와 함께 200 OK 응답, 실패 시 400 Bad Request 또는 500 Internal Server Error 응답
   */
//  @ApiOperation(
//          value = "본인인증 요청",
//          notes = "본인인증 수행 후 성공 시 세션에 인증 상태를 저장합니다.",
//          response = VerifyCodefResponse.class
//  )
//  @ApiResponses({
//          @ApiResponse(code = 200, message = "본인인증에 성공하였습니다."),
//          @ApiResponse(code = 400, message = "본인인증에 실패하였습니다."),
//          @ApiResponse(code = 500, message = "서버에서 본인인증을 처리하는데 오류가 발생했습니다.")
//  })
//  @PostMapping("/verify")
//  public ResponseEntity<?> verifyAuthenticity(
//          @ApiIgnore
//          @AuthenticationPrincipal CustomUser customUser,
//          @ApiParam(value = "본인인증 요청 DTO", required = true)
//          @RequestBody VerifyCodefRequest request,
//          HttpSession session) {
//
//    try {
//      //본인인증 수행
//      VerifyCodefResponse result = authService.verifyAuthenticity(request);
//
//      // 진위 확인 성공 시 인증 상태 세션에 저장 --비밀번호 재설정 시 상태 사용
//      if ("Y".equalsIgnoreCase(result.getResAuthenticity())) {
//        //세션에 이메일 저장
//        session.setAttribute("verifiedEmail", request.getEmail());
//      }
//      return ResponseEntity.ok(result); //200
//
//    } catch (IllegalArgumentException e) {
//      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인인증에 실패하였습니다.");
//    } catch (Exception e) { //500
//      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//              .body("서버에서 본인인증을 처리하는데 오류가 발생했습니다.");
//    }
//  }

  /**
   * 본인인증 CODEF 진위인증 사용 (임시).
   * {@code POST /auth/verify/authentication} 엔드포인트를 통해 CODEF를 이용한 진위인증을 처리합니다.
   * TODO: 로직 설계 필요.
   *
   * @param request CODEF 진위인증 요청 DTO
   * @return 현재는 null 반환 (로직 미구현)
   */
  @ApiOperation(
          value = "본인인증 CODEF 진위인증 사용",
          notes = "CODEF를 이용한 진위인증을 처리합니다. (현재 임시 구현)"
  )
  @PostMapping("/verify/authentication")
  public String verifyAuthentication(@RequestBody AuthRequest.VerifyCodefRequest request) {


    return null; //todo: 로직 설계해야함
  }

  /**
   * 회원가입 요청 처리.
   * {@code POST /auth/signup} 엔드포인트를 통해 이메일과 닉네임 중복 확인 후 새로운 사용자 계정을 생성합니다.
   *
   * @param request 회원가입 정보를 담고 있는 {@link SignUp} DTO
   * @return 회원가입 성공 시 200 OK 응답, 실패 시 409 Conflict (중복) 또는 500 Internal Server Error 응답
   */
  @ApiOperation(
          value = "회원가입",
          notes = "이메일과 닉네임 중복 확인 후 회원가입할 수 있게 합니다."
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "회원가입에 성공하였습니다."),
          @ApiResponse(code = 409, message = "이미 사용 중인 이메일 또는 닉네임입니다."),
          @ApiResponse(code = 500, message = "서버에서 회원가입을 처리하는데 오류가 발생했습니다.")
  })
  @PostMapping("/signup")
  public ResponseEntity<?> signUp(
          @ApiParam(value = "회원가입 요청 DTO", required = true)
          @RequestBody SignUp request
  ) {
    try {
      authService.signUp(request);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    } catch (Exception e) { //500
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("서버에서 회원가입을 처리하는데 오류가 발생했습니다.");
    }
  }

  /**
   * 이메일 중복 확인 요청 처리.
   * {@code Post /auth/check/email} 엔드포인트를 통해 입력한 이메일이 이미 사용 중인지 여부를 확인합니다.
   *
   * @param request 중복 확인을 요청할 이메일 주소
   * @return 사용 가능한 이메일일 경우 200 OK 응답, 이미 사용 중일 경우 409 Conflict 응답
   */
  @ApiOperation(
          value = "이메일 중복 확인",
          notes = "입력한 이메일이 사용 중인지 여부를 확인합니다."
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "사용 가능한 이메일입니다."),
          @ApiResponse(code = 409, message = "이미 사용 중인 이메일입니다."),
          @ApiResponse(code = 500, message = "서버에서 이메일 중복 확인을 처리하는데 오류가 발생했습니다.")
  })
  @PostMapping("/check/email")
  public ResponseEntity<?> checkEmail(
          @ApiParam(value = "이메일", example = "example@zangbu.com", required = true)
          @RequestBody AuthRequest.EmailCheck request
  ) {
    boolean isDuplicated = authService.isEmailDuplicated(request.getEmail());
    if (isDuplicated) { //409
      return ResponseEntity.status(HttpStatus.CONFLICT)
              .body("이미 사용 중인 이메일입니다.");
    }
    return ResponseEntity.ok().build(); //200
  }

  /**
   * 닉네임 중복 확인 요청 처리.
   * {@code GET /auth/check/nickname} 엔드포인트를 통해 입력한 닉네임이 이미 사용 중인지 여부를 확인합니다.
   *
   * @param request 중복 확인을 요청할 닉네임
   * @return 사용 가능한 닉네임일 경우 200 OK 응답, 이미 사용 중일 경우 409 Conflict 응답
   */
  @ApiOperation(
          value = "닉네임 중복 확인",
          notes = "입력한 닉네임이 사용 중인지 여부를 확인합니다."
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "사용 가능한 닉네임입니다."),
          @ApiResponse(code = 409, message = "이미 사용 중인 닉네임입니다."),
          @ApiResponse(code = 500, message = "서버에서 닉네임 중복 확인을 처리하는데 오류가 발생했습니다.")
  })
  @PostMapping("/check/nickname")
  public ResponseEntity<?> checkNickname(
          @ApiParam(value = "닉네임", example = "김철수123", required = true)
          @RequestBody AuthRequest.NicknameCheck request
  ) {
    boolean isDuplicated = authService.isNicknameDuplicated(request.getNickname());
    if (isDuplicated) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
              .body("이미 사용 중인 닉네임입니다.");
    }
    return ResponseEntity.ok().build(); //200
  }

  /**
   * 토큰 재발급 요청 처리.
   * {@code POST /auth/reissue} 엔드포인트를 통해 Access Token이 만료되었을 때 Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.
   * Refresh Token은 쿠키로 전달되고 갱신됩니다.
   *
   * @param refreshToken 클라이언트로부터 전달된 Refresh Token (쿠키 값)
   * @param customUser 인증된 사용자 정보 (현재 사용되지 않지만, 필요 시 확장 가능)
   * @param response HTTP 응답 객체 (Refresh Token 쿠키 설정을 위해 사용)
   * @return 토큰 재발급 성공 시 {@link TokenResponse}와 함께 200 OK 응답, 실패 시 400 Bad Request (토큰 만료/유효하지 않음) 또는 409 Conflict (토큰 없음) 응답
   */
  @ApiOperation(
          value = "토큰 재발급",
          notes = "refresh 토큰이 유효한 경우, 새로운 access 토큰을 발급합니다.",
          response = TokenResponse.class
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "토큰이 재발급되었습니다"),
          @ApiResponse(code = 400, message = "refresh 토큰이 만료되었거나 유효하지 않습니다."),
          @ApiResponse(code = 409, message = "refresh 토큰이 존재하지 않습니다.")
  })
  @PostMapping("/reissue")
  public ResponseEntity<?> reissue(
          @CookieValue(value = "refreshToken", required = false) String refreshToken,
          @ApiIgnore
          @AuthenticationPrincipal CustomUser customUser,
          HttpServletResponse response
  ) {
    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("쿠키가 존재하지 않습니다");
    }

    try {
      //access token 만료된 상태 가정하므로 토큰 유효성 검사 제거

      //토큰 재발급 한거 가져오기
      TokenResponse newTokens = authService.reissue(refreshToken);

      //쿠키 새로 설정 (refresh 토큰 갱신)
      Cookie refreshCookie = new Cookie("refreshToken", newTokens.getRefreshToken());
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(true);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(7 * 24 * 60 * 60);

      response.addCookie(refreshCookie);

      //재발급 성공하면 리턴 - refresh 토큰은 쿠키에 숨기고, access 토큰은 body에
      return ResponseEntity.ok(new TokenResponse(newTokens.getAccessToken(),
              null));

    } catch (ExpiredJwtException e) {
      //토큰 유효기간 만료 400 -> 쿠키도 삭제
      Cookie deleteCookie = new Cookie("refreshToken", null);
      deleteCookie.setMaxAge(0);
      deleteCookie.setPath("/");
      deleteCookie.setHttpOnly(true);
      deleteCookie.setSecure(true);
      response.addCookie(deleteCookie);

      return ResponseEntity.badRequest().body("refresh 토큰이 만료되었습니다.");

    } catch (JwtException | ResponseStatusException e) {
      //access/refresh 토큰 손상된 경우(위조/서명 검증 실패) 400
      return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");

    } catch (IllegalStateException e) {
      //Redis에 저장된 refreshToken이 없음 409
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }
}

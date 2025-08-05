package bjs.zangbu.security.account.controller;

import bjs.zangbu.security.account.dto.request.AuthRequest;
import bjs.zangbu.security.account.dto.request.AuthRequest.*;
import bjs.zangbu.security.account.dto.response.AuthResponse.TokenResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.EmailAuthResponse;
import bjs.zangbu.security.account.dto.response.AuthResponse.AuthVerify;
import bjs.zangbu.security.account.dto.response.AuthResponse.LoginResponse;

import bjs.zangbu.security.account.service.AuthService;
import bjs.zangbu.security.account.vo.CustomUser;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth API", description = "인증 관련 기능 API")
public class AuthController {

    private final AuthService authService;

    // 1. 로그인
    @Operation(
            summary = "로그인",
            description = "로그인 성공 시 토큰을 발급하여 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공했습니다." ),
            @ApiResponse(responseCode = "400", description = "아이디 또는 비밀번호가 일치하지 않습니다"),
            @ApiResponse(responseCode = "500", description = "서버에서 로그인을 처리하는데 오류가 발생했습니다.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 DTO (이메일, 비밀번호 입력)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class))
            )
            @RequestBody LoginRequest request,
            HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(request);

            //쿠키에 refresh 토큰 담기
            Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
            refreshCookie.setHttpOnly(true); //JS 접근 방지 - 장기 인증 수단이므로 클라이언트 측 js에서 접근 못하게 해야 함
            refreshCookie.setSecure(true); //Https에서만 전송 가능하도록
            refreshCookie.setPath("/"); //전체 경로에 대해 유효함
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); //7일간 유효(초단위)

            response.addCookie(refreshCookie);

            //access 토큰은 클라이언트에 바디로 전달
            //refresh 토큰은 쿠키로 숨겨서 클라이언트에 저장
            return ResponseEntity.ok(new LoginResponse(loginResponse.getAccessToken(), null, loginResponse.getRole()));

        } catch (IllegalArgumentException e){
            //400 에러 - 아이디/비밀번호 일치하지 않음
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 500 에러
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버에서 로그인을 처리하는데 오류가 발생했습니다.");
        }
    }

    // 2. 로그아웃
    @Operation(
            summary = "로그아웃",
            description = "로그아웃 성공 시 토큰을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "로그아웃에 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 로그아웃을 처리하는데 오류가 발생했습니다.")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal CustomUser customUser,
            HttpServletResponse response
    ){
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
        } catch (JwtException | IllegalArgumentException e){
            //400
            return ResponseEntity.badRequest().body("로그아웃에 실패했습니다.");
        } catch (Exception e){
            //500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 로그아웃을 처리하는데 오류가 발생했습니다.");
        }
    }

    // 3. 아이디(이메일) 찾기
    @Operation(
            summary = "이메일 찾기",
            description = "이름과 휴대폰 번호 기반으로 이메일을 찾습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일을 찾는데 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "이메일을 찾는데 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 이메일을 찾는데 오류가 발생했습니다.")
    })
    @PostMapping("/email")
    public ResponseEntity<?> findEmail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이메일 찾기 요청 DTO (이메일, 전화번호 입력)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EmailAuthRequest.class)
                    )
            )
            @RequestBody EmailAuthRequest request,
            @AuthenticationPrincipal CustomUser customUser) {

        try{
            EmailAuthResponse response = authService.findEmail(request);
            return ResponseEntity.ok(response); //200 성공
        } catch (IllegalArgumentException e){
            //400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일을 찾는데 실패했습니다.");
        } catch (Exception e){
            //500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 이메일을 찾는데 오류가 발생했습니다.");
        }
    }

    //4. 비밀번호 재설정 --로그인하지 않은 상태
    @Operation(
            summary = "비밀번호 재설정",
            description = "본인인증을 마친 후 비밀번호를 재설정할 수 있게 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "비밀번호를 변경하는데 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "비밀번호를 변경하는데 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 비밀번호 변경을 처리하는데 오류가 발생했습니다.")
    })
    @PostMapping("/password")
    public ResponseEntity<?> resetPassword(
            @AuthenticationPrincipal CustomUser customUser,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비밀번호 재설정 요청 DTO (새 비밀번호 입력)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ResetPassword.class)
                    )
            )
            @RequestBody ResetPassword request,
            HttpSession session) {

        try{
            authService.resetPassword(request, session);
            return ResponseEntity.ok().build(); //200
        } catch (IllegalStateException | IllegalArgumentException e){
            //400
            return ResponseEntity.badRequest().body("비밀번호를 변경하는데 실패했습니다.");
        } catch (Exception e){
            //500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 비밀번호 변경을 처리하는데 오류가 발생했습니다.");
        }
    }

    //5. 본인인증 요청
    @Operation(
            summary = "본인인증 요청",
            description = "본인인증 수행 후 성공 시 세션에 인증 상태를 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "본인인증에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "본인인증에 실패하였습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 본인인증을 처리하는데 오류가 발생했습니다.")
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAuthenticity(
            @AuthenticationPrincipal CustomUser customUser,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "본인인증 요청 DTO",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = VerifyRequest.class)
                    )
            )
            @RequestBody VerifyRequest request,
            HttpSession session) {

        try {
            //본인인증 수행
            AuthVerify result = authService.verifyAuthenticity(request);

            // 진위 확인 성공 시 인증 상태 세션에 저장 --비밀번호 재설정 시 상태 사용
            if ("Y".equalsIgnoreCase(result.getResAuthenticity())) {
                //세션에 이메일 저장
                session.setAttribute("verifiedEmail", request.getEmail());
            }
            return ResponseEntity.ok(result); //200

        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인인증에 실패하였습니다.");
        } catch (Exception e){ //500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 본인인증을 처리하는데 오류가 발생했습니다.");
        }
    }
    //5-1 본인인증 codef 진위인증 사용
    // todo : 임시로 주소명 설정 , 추후 바꾸든가 하는게 좋음
    @PostMapping("/verify/authentication")
    public String verifyAuthentication(@RequestBody AuthRequest.VerifyCodefRequest request) {
        return null; //todo: 로직 설계해야함
    }
    // 6. 회원가입
    @Operation(
            summary = "회원가입",
            description = "이메일과 닉네임 중복 확인 후 회원가입할 수 있게 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "회원가입에 실패하였습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 회원가입을 처리하는데 오류가 발생했습니다.")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 DTO",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignUp.class)
                    )
            )
            @RequestBody SignUp request
    ) {
        try {
            authService.signUp(request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e){ //500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 회원가입을 처리하는데 오류가 발생했습니다.");
        }
    }

    // 7. 이메일 중복 확인
    @Operation(
            summary = "이메일 중복 확인",
            description = "입력한 이메일이 사용 중인지 여부를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능한 이메일입니다."),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일입니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 이메일 인증을 처리하는데 오류가 발생했습니다.")
    })
    @GetMapping("/check/email")
    public ResponseEntity<?> checkEmail(
            @Parameter(description = "이메일", example = "example@zangbu.com")
            @RequestParam String email
    ) {
        boolean isDuplicated = authService.isEmailDuplicated(email);
        if(isDuplicated){ //409
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 사용 중인 이메일입니다.");
        }
        return ResponseEntity.ok().build(); //200
    }

    // 8. 닉네임 중복 확인
    @Operation(
            summary = "닉네임 중복 확인",
            description = "입력한 닉네임이 사용 중인지 여부를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임입니다."),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임입니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 닉네임 인증을 처리하는데 오류가 발생했습니다.")
    })
    @GetMapping("/check/nickname")
    public ResponseEntity<?> checkNickname(
            @Parameter(description = "닉네임", example = "김철수123")
            @RequestParam String nickname
    ) {
        boolean isDuplicated = authService.isNicknameDuplicated(nickname);
        if(isDuplicated){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 사용 중인 닉네임입니다.");
        }
        return ResponseEntity.ok().build(); //200
    }

    // 9. 토큰 재발급 요청 --access 토큰 만료 시 클라이언트가 refresh 토큰 전송
    // -> redis에 저장된 refresh 토큰과 비교해서 유효 시 새로운 access 토큰 발급
    // + refresh 토큰도 함께 재발급해서 갱신
    @Operation(
            summary = "토큰 재발급",
            description = "refresh 토큰이 유효한 경우, 새로운 access 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰이 재발급되었습니다"),
            @ApiResponse(responseCode = "400", description = "refresh 토큰이 만료되었습니다"),
            @ApiResponse(responseCode = "409", description = "토큰이 존재하지 않습니다")
    })
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @AuthenticationPrincipal CustomUser customUser,
            HttpServletResponse response
            ){
        if(refreshToken == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("쿠키가 존재하지 않습니다");
        }

        try{
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

        } catch (ExpiredJwtException e){
            //토큰 유효기간 만료 400 -> 쿠키도 삭제
            Cookie deleteCookie = new Cookie("refreshToken", null);
            deleteCookie.setMaxAge(0);
            deleteCookie.setPath("/");
            deleteCookie.setHttpOnly(true);
            deleteCookie.setSecure(true);
            response.addCookie(deleteCookie);

            return ResponseEntity.badRequest().body("refresh 토큰이 만료되었습니다.");

        } catch (JwtException | ResponseStatusException e){
            //access/refresh 토큰 손상된 경우(위조/서명 검증 실패) 400
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");

        } catch (IllegalStateException e){
            //Redis에 저장된 refreshToken이 없음 409
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}

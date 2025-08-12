package bjs.zangbu.fcm.controller;

import bjs.zangbu.fcm.dto.request.FcmRequest;
import bjs.zangbu.fcm.dto.request.FcmRequest.*;
import bjs.zangbu.fcm.service.FcmService;
import bjs.zangbu.security.account.vo.CustomUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
// @Tag(name = "FCM API", description = "Firebase Cloud Messaging 디바이스 토큰 관리 API")
//@SecurityRequirement(name = "Authorization") // Swagger JWT 인증 적용
public class FcmController {

  private final FcmService fcmService;

  /* -------------------------------------------------
   * 디바이스 토큰 등록
   * ------------------------------------------------- */
  @ApiOperation(value = "FCM 디바이스 토큰 등록", notes = "현재 로그인한 사용자의 디바이스 FCM 토큰을 등록합니다.")
  @ApiResponses({
          @ApiResponse(code = 200, message = "토큰 등록 성공"),
          @ApiResponse(code = 400, message = "잘못된 요청 (등록 실패)"),
          @ApiResponse(code = 500, message = "서버 오류")
  })
  @PostMapping("/register")
  public ResponseEntity<?> registerToken(
            @ApiIgnore
            @AuthenticationPrincipal CustomUser userDetails,
            @RequestBody FcmRegisterRequest request) {
    try {
      // 유저 ID를 받아온다.
      String memberId = userDetails.getMember().getMemberId();

      fcmService.registerToken(memberId, request);
      return ResponseEntity.status(HttpStatus.OK).body("토큰 등록 성공.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰 등록 실패.");
    }
  }

  /* -------------------------------------------------
   * 현재 기기의 디바이스 토큰 삭제
   * ------------------------------------------------- */
  @ApiOperation(value = "현재 기기의 FCM 디바이스 토큰 삭제", notes = "현재 로그인한 사용자의 현재 기기의 디바이스 FCM 토큰을 삭제합니다.")
  @ApiResponses({
          @ApiResponse(code = 200, message = "토큰 삭제 성공"),
          @ApiResponse(code = 400, message = "잘못된 요청 (삭제 실패)"),
          @ApiResponse(code = 500, message = "서버 오류")
  })
  @DeleteMapping("/remove")
  public ResponseEntity<?> deleteToken(
          @ApiIgnore
          @AuthenticationPrincipal CustomUser userDetails,
          @RequestBody FcmRemoveRequest request) {
    try {
      // 유저 ID를 받아온다.
      String memberId = userDetails.getMember().getMemberId();

      fcmService.deleteTokenByMemberIdAndToken(memberId, request);
      return ResponseEntity.status(HttpStatus.OK).body("토큰 삭제 성공");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰 삭제 실패.");
    }
  }

  /* -------------------------------------------------
   * 모든 디바이스 토큰 삭제
   * ------------------------------------------------- */
  @ApiOperation(value = "모든 FCM 디바이스 토큰 삭제", notes = "현재 로그인한 사용자의 모든 디바이스 FCM 토큰을 삭제합니다.")
  @ApiResponses({
          @ApiResponse(code = 200, message = "토큰 삭제 성공"),
          @ApiResponse(code = 400, message = "잘못된 요청 (삭제 실패)"),
          @ApiResponse(code = 500, message = "서버 오류")
  })
  @DeleteMapping("/remove/all")
  public ResponseEntity<?> deleteTokens(
          @ApiIgnore
          @AuthenticationPrincipal CustomUser userDetails
          ) {
    try {
      // 유저 ID를 받아온다.
      String memberId = userDetails.getMember().getMemberId();

      fcmService.deleteAllTokensByMemberId(memberId);
      return ResponseEntity.status(HttpStatus.OK).body("토큰 삭제 성공");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰 삭제 실패.");
    }
  }
}

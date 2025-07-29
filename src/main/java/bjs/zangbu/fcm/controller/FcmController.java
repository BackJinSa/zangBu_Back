package bjs.zangbu.fcm.controller;

import bjs.zangbu.fcm.dto.request.FcmRequest.FcmTokenRequest;
import bjs.zangbu.fcm.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
@Tag(name = "FCM API", description = "Firebase Cloud Messaging 디바이스 토큰 관리 API")
@SecurityRequirement(name = "Authorization") // Swagger JWT 인증 적용
public class FcmController {

    private final FcmService fcmService;

    /* -------------------------------------------------
     * 디바이스 토큰 등록
     *
     * ------------------------------------------------- */
    @Operation(
            summary = "FCM 디바이스 토큰 등록",
            description = "현재 로그인한 사용자의 디바이스 FCM 토큰을 등록합니다."
    )
    @PostMapping("/register")
    public ResponseEntity<?> registerToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FcmTokenRequest request) {
        try {
            // 유저 ID를 받아온다.
            String memberId = userDetails.getUsername();

            fcmService.registerToken(memberId, request.getToken());
            return ResponseEntity.status(HttpStatus.OK).body("토큰 등록 성공.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰 등록 실패.");
        }
    }

    /* -------------------------------------------------
     * 디바이스 토큰 삭제
     *
     * ------------------------------------------------- */
    @Operation(
            summary = "FCM 디바이스 토큰 삭제",
            description = "현재 로그인한 사용자의 모든 디바이스 FCM 토큰을 삭제합니다."
    )
    @DeleteMapping("/remove")
    public ResponseEntity<?> deleteTokens(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FcmTokenRequest request) {
        try {
            // 유저 ID를 받아온다.
            String memberId = userDetails.getUsername();

            fcmService.deleteAllTokensByMemberId(memberId);
            return ResponseEntity.status(HttpStatus.OK).body("토큰 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰 삭제 실패.");
        }
    }
}

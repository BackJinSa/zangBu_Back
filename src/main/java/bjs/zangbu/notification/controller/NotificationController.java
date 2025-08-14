package bjs.zangbu.notification.controller;

import bjs.zangbu.notification.dto.response.NotificationResponse.MarkAllReadResult;
import bjs.zangbu.notification.dto.response.NotificationResponse.NotificationAll;
import bjs.zangbu.notification.service.NotificationService;
import bjs.zangbu.security.account.vo.CustomUser;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
// @Tag(name = "Notification API", description = "FCM 및 트리거 기반 알림 관련 기능을 제공합니다.")
//@SecurityRequirement(name = "Authorization") // Swagger JWT 인증 적용
@Api(tags = "Notification API", description = "FCM 및 트리거 기반 알림 관련 기능을 제공합니다.")
public class NotificationController {

  private final NotificationService notificationService;

  /* -------------------------------------------------
   * 전체 알림 조회
   *
   * ------------------------------------------------- */
  @ApiOperation(value = "전체 알림 조회", notes = "현재 로그인한 사용자의 전체 알림 목록을 페이지 단위로 조회합니다.")
  @ApiResponses({
          @ApiResponse(code = 200, message = "알림 목록 조회 성공"),
          @ApiResponse(code = 400, message = "잘못된 요청"),
          @ApiResponse(code = 500, message = "서버 오류")
  })
  @GetMapping("/all")
  public ResponseEntity<?> getAllNotifications(
          @ApiIgnore
          @AuthenticationPrincipal CustomUser userDetails,
          @RequestParam(defaultValue = "1") int page,         // 요청 페이지 (1부터 시작)
          @RequestParam(defaultValue = "10") int size,         // 페이지당 항목 수
          @RequestParam(required = false) String type     // BUILDING | TRADE | REVIEW | null(전체)
  ) {
    try {
      // 유저 ID를 받아온다.
      String memberId = userDetails.getMember().getMemberId();

      // PageHelper 페이지네이션 시작
      PageHelper.startPage(page, size);

      // 3. 서비스 호출 (PagedResponse<NotificationElement> 포함된 DTO 반환)
      NotificationAll response = notificationService.getAllNotifications(memberId, type);

      // 성공 : 전체 알림 리스트/페이지네이션 정보 반환
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전체 알림을 불러오는데 실패했습니다.");
    }
  }

  /* -------------------------------------------------
   * 하나의 알림 읽음 처리
   *
   * ------------------------------------------------- */
  @ApiOperation(value = "알림 읽음 처리", notes = "특정 알림 1개를 읽음 처리합니다. 이미 처리된 경우 실패 응답을 반환합니다.")
  @ApiResponses({
          @ApiResponse(code = 200, message = "읽음 처리 성공 (알림 ID 반환)"),
          @ApiResponse(code = 400, message = "알림 없음 또는 이미 처리됨"),
          @ApiResponse(code = 500, message = "서버 오류")
  })
  @PatchMapping("/read/{notificationId}")
  public ResponseEntity<?> notificationRead(
          @ApiIgnore
          @AuthenticationPrincipal CustomUser userDetails,
          @PathVariable Long notificationId
  ) {
    // 유저 ID를 받아온다.
    String memberId = userDetails.getMember().getMemberId();

    log.info("memberId ============================================ : {}", memberId);

    try {
      boolean result = notificationService.markAsRead(memberId, notificationId);

      if (result) {
        // 성공 : 읽음 처리 된 알림의 id 반환
        return ResponseEntity.status(HttpStatus.OK).body(notificationId);
      } else {
        // 알림이 없거나 이미 처리된 경우
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("해당 알림을 찾을 수 없거나 이미 처리되었습니다.");
      }
    } catch (RuntimeException e) {
      // 서비스 계층에서 발생한 예외 처리
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    } catch (Exception e) {
      // 그 외 알 수 없는 예외 처리
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버 오류가 발생했습니다.");
    }
  }

  /* -------------------------------------------------
   * 알림 전체 읽음 처리
   *
   * ------------------------------------------------- */
  @ApiOperation(value = "전체 알림 읽음 처리", notes = "현재 로그인한 사용자의 모든 안 읽은 알림을 읽음 처리합니다.")
  @ApiResponses({
          @ApiResponse(code = 200, message = "읽음 처리 성공 (읽은 알림 개수 반환)"),
          @ApiResponse(code = 400, message = "읽음 처리된 알림 없음"),
          @ApiResponse(code = 500, message = "서버 오류")
  })
  @PatchMapping("/read/all")
  public ResponseEntity<?> notificationAllRead(
          @ApiIgnore
          @AuthenticationPrincipal CustomUser userDetails
  ) {
    // 유저 ID를 받아온다.
    String memberId = userDetails.getMember().getMemberId();

    try {
      MarkAllReadResult result = notificationService.markAllAsRead(memberId);

      if (result.getProcessedCount() > 0) {
        // 성공: 읽음 처리된 개수 반환
        return ResponseEntity.status(HttpStatus.OK).body(result.getProcessedCount());
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("읽음 처리된 알림이 없습니다.");
      }
    } catch (RuntimeException e) {
      // 서비스 계층에서 발생한 예외 처리
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    } catch (Exception e) {
      // 그 외 알 수 없는 예외 처리
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버 오류가 발생했습니다.");
    }
  }

  /* -------------------------------------------------
   * 알림 삭제
   *
   * ------------------------------------------------- */
  @ApiOperation(value = "알림 삭제", notes = "특정 알림을 삭제합니다. 이미 삭제되었거나 존재하지 않는 경우 실패 응답을 반환합니다.")
  @ApiResponses({
          @ApiResponse(code = 200, message = "알림 삭제 성공 (알림 ID 반환)"),
          @ApiResponse(code = 400, message = "삭제할 알림이 없음"),
          @ApiResponse(code = 500, message = "서버 오류")
  })
  @DeleteMapping("/remove/{notificationId}")
  public ResponseEntity<?> removeNotification(
          @ApiIgnore
          @AuthenticationPrincipal CustomUser userDetails,
          @PathVariable Long notificationId
  ) {
    // 유저 ID를 받아온다.
    String memberId = userDetails.getMember().getMemberId();

    try {
      boolean removed = notificationService.removeNotification(memberId, notificationId);

      if (removed) {
        // 성공 : 삭제된 알림 id 반환
        return ResponseEntity.status(HttpStatus.OK).body(notificationId);
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("삭제할 알림이 존재하지 않습니다.");
      }
    } catch (RuntimeException e) {
      // 서비스 계층에서 발생한 예외 처리
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(e.getMessage());
    } catch (Exception e) {
      // 그 외 알 수 없는 예외 처리
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("알림 삭제 중 서버 오류가 발생했습니다.");
    }
  }
}

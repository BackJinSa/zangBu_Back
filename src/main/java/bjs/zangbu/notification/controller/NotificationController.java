package bjs.zangbu.notification.controller;

import bjs.zangbu.notification.dto.response.NotificationResponse.MarkAllReadResult;
import bjs.zangbu.notification.dto.response.NotificationResponse.NotificationAll;
import bjs.zangbu.notification.service.NotificationService;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/notification")
@Tag(name = "Notification API", description = "FCM 및 트리거 기반 알림 관련 기능을 제공합니다.")
@SecurityRequirement(name = "Authorization") // Swagger JWT 인증 적용
public class NotificationController {

    private final NotificationService notificationService;

    /* -------------------------------------------------
    * 전체 알림 조회
    *
    * ------------------------------------------------- */
    @Operation(
            summary = "전체 알림 조회",
            description = "현재 로그인한 사용자의 전체 알림 목록을 페이지 단위로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = NotificationAll.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/all")
    public ResponseEntity<?> getAllNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,         // 요청 페이지 (1부터 시작)
            @RequestParam(defaultValue = "10") int size         // 페이지당 항목 수
    ) {
        try {
            // 유저 ID를 받아온다.
            String memberId = userDetails.getUsername();

            // PageHelper 페이지네이션 시작
            PageHelper.startPage(page, size);

            // 3. 서비스 호출 (PagedResponse<NotificationElement> 포함된 DTO 반환)
            NotificationAll response = notificationService.getAllNotifications(memberId);

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
    @Operation(
            summary = "알림 읽음 처리",
            description = "특정 알림 1개를 읽음 처리합니다. 이미 읽음 처리된 경우 실패 응답을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽음 처리 성공 (알림 ID 반환)",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "알림 없음 or 이미 읽음 처리됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<?> notificationRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId
            ) {
        // 유저 ID를 받아온다.
        String memberId = userDetails.getUsername();

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
    @Operation(
            summary = "전체 알림 읽음 처리",
            description = "현재 로그인한 사용자의 모든 안 읽은 알림을 읽음 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "읽음 처리 성공 (읽은 알림 개수 반환)",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "읽음 처리된 알림 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/read/all")
    public ResponseEntity<?> notificationAllRead(
        @AuthenticationPrincipal UserDetails userDetails
        ) {
        // 유저 ID를 받아온다.
        String memberId = userDetails.getUsername();

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
    @Operation(
            summary = "알림 삭제",
            description = "특정 알림을 삭제합니다. 이미 삭제되었거나 존재하지 않는 경우 실패 응답을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 삭제 성공 (알림 ID 반환)",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "삭제할 알림이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/remove/{notificationId}")
    public ResponseEntity<?> removeNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId
    ) {
        // 유저 ID를 받아온다.
        String memberId = userDetails.getUsername();

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

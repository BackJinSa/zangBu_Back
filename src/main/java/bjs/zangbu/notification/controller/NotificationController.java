package bjs.zangbu.notification.controller;

import bjs.zangbu.notification.dto.response.NotificationResponse.MarkAllReadResult;
import bjs.zangbu.notification.dto.response.NotificationResponse.NotificationAll;
import bjs.zangbu.notification.service.NotificationService;
import com.github.pagehelper.PageHelper;
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
public class NotificationController {

    private final NotificationService notificationService;

    /* -------------------------------------------------
    * 전체 알림 조회
    *
    * ------------------------------------------------- */
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

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전체 알림을 불러오는데 실패했습니다.");
        }
    }

    /* -------------------------------------------------
     * 하나의 알림 읽음 처리
     *
     * ------------------------------------------------- */
    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<?> notificationRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId
            ) {
        // 유저 ID를 받아온다.
        String memberId = userDetails.getUsername();

        if(notificationService.markAsRead(memberId, notificationId)) {
            return ResponseEntity.status(HttpStatus.OK).body(notificationId);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("알림 읽음 처리 실패.");
        }
    }

    /* -------------------------------------------------
     * 알림 전체 읽음 처리
     *
     * ------------------------------------------------- */
    @PatchMapping("/read/all")
    public ResponseEntity<?> notificationAllRead(
        @AuthenticationPrincipal UserDetails userDetails
        ) {
        // 유저 ID를 받아온다.
        String memberId = userDetails.getUsername();
        MarkAllReadResult result = notificationService.markAllAsRead(memberId);
        if(result.getProcessedCount()>0) {
            return ResponseEntity.status(HttpStatus.OK).body(result.getProcessedCount());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("알림 읽음 처리 실패.");
        }
    }



    /* -------------------------------------------------
     * 알림 삭제
     *
     * ------------------------------------------------- */
    @PatchMapping("/remove/{notificationId}")
    public ResponseEntity<?> removeNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId
    ) {
        // 유저 ID를 받아온다.
        String memberId = userDetails.getUsername();

        if(notificationService.removeNotification(memberId, notificationId)) {
            return ResponseEntity.status(HttpStatus.OK).body(notificationId);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("알림 삭제 실패.");
        }
    }
}

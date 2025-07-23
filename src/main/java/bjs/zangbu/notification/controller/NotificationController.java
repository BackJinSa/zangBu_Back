package bjs.zangbu.notification.controller;

import bjs.zangbu.notification.dto.request.NotificationRequest;
import bjs.zangbu.notification.service.NotificationService;
import com.google.api.gax.paging.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Page<NotificationRequest>> getAllNotifications() {

        return ResponseEntity.ok().build();
    }


    /* -------------------------------------------------
     * 알림 읽음 처리
     *
     * ------------------------------------------------- */
    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<?> notificationRead() {
        return ResponseEntity.ok().build();
    }


    /* -------------------------------------------------
     * 알림 전체 읽음 처리
     *
     * ------------------------------------------------- */
    @PatchMapping("/read/all")
    public ResponseEntity<?> notificationAllRead() {
        return ResponseEntity.ok().build();
    }


    /* -------------------------------------------------
     * 알림 삭제
     *
     * ------------------------------------------------- */
    @PatchMapping("/remove/{notificationId}")
    public ResponseEntity<?> removeNotification() {
        return ResponseEntity.ok().build();
    }
}

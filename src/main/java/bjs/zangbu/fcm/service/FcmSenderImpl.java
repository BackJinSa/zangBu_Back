package bjs.zangbu.fcm.service;

import com.google.firebase.messaging.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class FcmSenderImpl implements FcmSender {

    // =================== FCM 전송 구현체 ===================
    // 단일 알림 전송
    @Override
    public void send(String token, String title, String body, String url) {
        // 알림 클릭 이벤트를 위해 Notification 대신 Data 메시지 사용
        Message message = Message.builder()
                .setToken(token)
                .putData("title", title)
                .putData("body", body)
                .putData("url", url)  // 🔗 클릭 시 이동할 주소 포함
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 전송 성공: {}", response);
        } catch (Exception e) {
            log.warn("FCM 전송 실패: {}", e.getMessage(), e);
        }
    }

    // 여러 사용자에게 알림 전송
    @Override
    public void sendToMany(List<String> tokens, String title, String body, String url) {
        for (String token : tokens) {
            send(token, title, body, url);  // 수정된 send() 호출
        }
    }
}

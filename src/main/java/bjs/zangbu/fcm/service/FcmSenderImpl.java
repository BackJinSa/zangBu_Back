package bjs.zangbu.fcm.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FcmSenderImpl implements FcmSender {

    @Override
    public void send(String token, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM 전송 성공: " + response);
        } catch (Exception e) {
            System.err.println("FCM 전송 실패: " + e.getMessage());
        }
    }

    @Override
    public void sendToMany(List<String> tokens, String title, String body) {
        for (String token : tokens) {
            send(token, title, body); // 단일 전송 반복
        }
    }
}

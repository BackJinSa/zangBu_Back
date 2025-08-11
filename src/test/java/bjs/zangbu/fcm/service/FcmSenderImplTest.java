package bjs.zangbu.fcm.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FcmSenderImplTest {

    @BeforeEach
    void setUp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/firebase-key.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    @Test
    void send() {
        // 1. FcmSenderImpl 수동 생성 (Spring 없이)
        FcmSenderImpl sender = new FcmSenderImpl();

        // 2. 실제 수신 가능한 디바이스 FCM 토큰 입력
        String token = "dp50yXn_wTDuFLWE0ORoPE:APA91bH0CF44UE552qPkzNeKYA5Y-XqAMnrZkmEuQVCxlpPyEO5UIvCtNU_kz5NUHYNccHQOBvFW3IN_6vcZ-wI3FCXLXyxdsB88rIQfe_LpxTIssqKHFTU"; // 웹, 안드로이드, iOS 등
        String title = "[FCM 테스트]";
        String body = "이 알림이 디바이스로 수신되면 FCM 연동 성공!";
        String url = "";

        // 3. 단일 테스트
        sender.send(token, title, body, url);

        // 또는 다중 테스트 (선택)
        /*
        sender.sendToMany(
            List.of(token),
            title,
            body
        );
        */
    }



}
package bjs.zangbu.fcm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FcmSenderImplTest {

    @Test
    void send() {
        // 1. FcmSenderImpl 수동 생성 (Spring 없이)
        FcmSenderImpl sender = new FcmSenderImpl();

        // 2. 실제 수신 가능한 디바이스 FCM 토큰 입력
        String token = "dMWnxMeAb1S5PnXZ3WHgtG:APA91bF2yk4995VIdn4NpjI0YS5hz7S6-QUpknmULvg5cYWcTNwZR2kNaUJkybYwHniR3UnMWc3rMRjgKa3gJquLihWkKlipA7jpM39YRsqpDVMFSUJtav8"; // 웹, 안드로이드, iOS 등
        String title = "[FCM 테스트]";
        String body = "이 알림이 디바이스로 수신되면 FCM 연동 성공!";

        // 3. 단일 테스트
        sender.send(token, title, body);

        // 또는 다중 테스트 (선택)
        /*
        sender.sendToMany(
            List.of(token),
            title,
            body
        );
        */
    }

    @Test
    void a() {
        System.out.println("테스트");
    }
}
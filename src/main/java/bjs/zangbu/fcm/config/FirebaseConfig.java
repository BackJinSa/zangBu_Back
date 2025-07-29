package bjs.zangbu.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;


/*
* 이 설정 클래스는 Firebase Admin SDK를 초기화하는 설정을 하고,
* @Bean으로 FirebaseApp 객체를 Spring 빈으로 등록하는 역할을 합니다.
* Firebase의 기능 (예: FCM 푸시 전송)을 사용하려면, 반드시 초기화된 FirebaseApp 객체가 필요합니다.
* */
@Configuration // Firebase 설정을 위한 Spring 설정 클래스
public class FirebaseConfig {

    /**
     * Firebase SDK 초기화 및 FirebaseApp 등록
     *
     * @return 초기화된 FirebaseApp 인스턴스 (전역 싱글톤)
     * @throws Exception 키 파일이 없거나 초기화 실패 시 예외 발생
     */
    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        // 🔹 Firebase 서비스 계정 키(JSON)를 classpath에서 읽어옴
        InputStream serviceAccount = new ClassPathResource("firebase-key.json").getInputStream();

        // 🔹 Firebase 초기화 옵션 설정 (인증 정보 주입)
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount)) // 서비스 계정 인증
                .build();

        // 🔹 FirebaseApp 전역 초기화 및 등록
        return FirebaseApp.initializeApp(options);
    }
}

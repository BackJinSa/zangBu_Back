package bjs.zangbu.codef.service;

import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.security.account.dto.request.AuthRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
public class CodefE2ETest {

    private final ObjectMapper om = new ObjectMapper();
    @Autowired
    private CodefService codefService;
    @Autowired
    private CodefTwoFactorService codefTwoFactorService;

    // 주의: 사람 개입이 필요한 테스트이므로 CI에선 비활성화 권장

    @Test
    @DisplayName("주민등록 진위확인 E2E (수동)")
    public void e2e_manual_secureNo() throws Exception {
        // 1) 1차 호출
        AuthRequest.VerifyCodefRequest req = AuthRequest.VerifyCodefRequest.builder()
                .name("전경환")
                .birth("981207")
                .identity("bQvUpPc1lO+khOzXaUXUwIZXddmE+dSpOT7JErdq11yUgpSoqte9/lG+HQZk7G1KPL5CTuywcUqPfHLHo7KmmPW47Rf7fUXWjbojl5ax1K7JTYYIq0dv0RAfRfNLVqR5EPYAbMXjOVN3zwLFdbELKEfs2c7BzFWyxt4mxXe3O8Srtjo0HgHmrzwuhcrfZIeAa/gH5FUyOoILyG7SfvvvipQqtLzCPwoIRUGUIscEZI78c8o9GUvdBEliPVapKzHZTgiEYYia45IL2Lq5giG0qrgmSthXU/HlO/eFjATE7dqzxEIbb85tScMyDiMC5oUqfB/c3RFAlV4gE3snl6I9Tg==")
                .phone("01093687950")
                .telecom("0")
                .issueDate("20161207")
                .build();

        String step1 = codefTwoFactorService.residentRegistrationAuthenticityConfirmation(req);
        Map<String, Object> m = om.readValue(step1, new TypeReference<>() {});
        String sessionKey = (String) m.get("sessionKey");
        String captchaDataUri = (String) m.get("captcha");
        assertNotNull(sessionKey);
        assertNotNull(captchaDataUri);

        // 2) 캡차 표시 (HTML로 띄우기 - 가장 간단)
        Path html = Files.createTempFile("captcha-", ".html");
        String htmlContent = "<!doctype html><meta charset='utf-8'>"
                + "<h3>아래 캡차 6자를 콘솔에 입력하세요</h3>"
                + "<img style='image-rendering:pixelated;border:1px solid #ccc' src='" + captchaDataUri + "' />";
        Files.writeString(html, htmlContent, StandardCharsets.UTF_8);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(html.toUri());
        } else {
            System.out.println("열 수 없는 환경입니다. 파일 경로: " + html.toAbsolutePath());
        }

        // (대안) PNG로 저장해서 열고 싶다면:
        // String b64 = captchaDataUri.substring(captchaDataUri.indexOf(',') + 1);
        // byte[] png = Base64.getDecoder().decode(b64);
        // Path pngFile = Files.createTempFile("captcha-", ".png");
        // Files.write(pngFile, png);
        // Desktop.getDesktop().open(pngFile.toFile());

        // 3) 콘솔로 secureno 입력 받기
        System.out.print("보안문자(6자) 입력: ");
//        Thread.sleep(60000);
        String secureNo = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
        assertTrue(secureNo.matches("^[A-Za-z0-9]{6}$"), "보안문자는 영숫자 6자여야 합니다.");

        // 4) 최종 호출
        String step2 = codefService.processSecureNo(sessionKey, secureNo);
        System.out.println("최종 응답:\n" + step2);

        // 간단 검증 (케이스에 따라 조정)
        Map<String, Object> finalMap = om.readValue(step2, new TypeReference<>() {});
        Map<String, Object> result = (Map<String, Object>) finalMap.get("result");
        assertNotNull(result);
        String code = (String) result.get("code");
        assertNotNull(code);
        // 성공 코드면 통과, 아니라면 캡차 재시도 흐름(CF-03002)일 수 있으니 출력 확인
        System.out.println("CODEF result.code = " + code);
    }
}

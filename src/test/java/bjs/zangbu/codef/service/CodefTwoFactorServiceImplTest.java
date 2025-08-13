package bjs.zangbu.codef.service;

import bjs.zangbu.addressChange.dto.request.ResRegisterCertRequest;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.security.account.dto.request.AuthRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
public class CodefTwoFactorServiceImplTest {

    @Autowired
    private CodefTwoFactorService codefTwoFactorService;
    @Autowired
    private CodefService codefService;
    @Autowired
    private ObjectMapper om;


    @BeforeEach
    public void setUp() {
    }
    @Disabled("테스트 남용 방지")
    @Test
    public void generalBuildingLeader_shouldReturnValidResponse_whenGivenValidRequest() throws Exception {
        BuildingRegisterRequest request = BuildingRegisterRequest.builder()
                .userName("백현빈")
                .identity("FY1GqIOaqzlBwjl/+BJD6fnRm/TtlrR+BzT+VgVqFkBUrSEsVKByQSboZ987sAnoCisosPVyuyPENt6pgd24CFpW6NqQx84bj21QnBbZ+fEnRJ1DC7DtR7twOFMyEhucDcl5eGKFi7SZapjH9w2gL0rsY78aG1G+mBDyjwzelj7YzVV7KzLTSEUzOBmsH+kcX0Snqg5ByTPS8BNvwfC4ypwyF+0JHY8k7q4mSsjEILao2XwwEu7GCTUPzK8AUVev8ZmWPIJSm2+xntGOJDPGfyoZ/8kxR9Cl8aIYVr47ExUpF4YSkPsrloWpqxthy2JiVjGGi41eBSqcVAyIdZgzwg==")
                .birthDate("011020")
                .phoneNo("01075117975")
                .address("서울특별시 동대문구 왕산로 23길 89")
                .dong("101동")
                .ho("402호")
                .telecom("2")
                .zipCode("02575")
                .build();
        String result = codefTwoFactorService.generalBuildingLeader(request);
        assertNotNull(result, "API 응답은 null이 아니어야 합니다.");
        assertTrue(result != null && !result.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(result.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");
        System.out.println("통합 테스트 성공! Codef API 응답:\n" + result);
    }
    @Disabled("테스트 남용 방지")
    @Test
    @DisplayName("주민등록 초본 응답 테스트")
    public void residentRegistrationCertificateTest() throws Exception {
        ResRegisterCertRequest request = ResRegisterCertRequest.builder()
                .birth("981207")
                .identity("bQvUpPc1lO+khOzXaUXUwIZXddmE+dSpOT7JErdq11yUgpSoqte9/lG+HQZk7G1KPL5CTuywcUqPfHLHo7KmmPW47Rf7fUXWjbojl5ax1K7JTYYIq0dv0RAfRfNLVqR5EPYAbMXjOVN3zwLFdbELKEfs2c7BzFWyxt4mxXe3O8Srtjo0HgHmrzwuhcrfZIeAa/gH5FUyOoILyG7SfvvvipQqtLzCPwoIRUGUIscEZI78c8o9GUvdBEliPVapKzHZTgiEYYia45IL2Lq5giG0qrgmSthXU/HlO/eFjATE7dqzxEIbb85tScMyDiMC5oUqfB/c3RFAlV4gE3snl6I9Tg==")
                .phone("01093687950")
                .name("전경환")
                .telecom("0")
                .memberId("")
                .build();


        String result = codefTwoFactorService.residentRegistrationCertificate(request);
        assertNotNull(result, "API 응답은 null이 아니어야 합니다.");
        assertTrue(result != null && !result.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(result.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");
        System.out.println("통합 테스트 성공! Codef API 응답:\n" + result);

    }

    @Test
    @DisplayName("진위 인증 캡차 띄우기")
    void residentRegistrationAuthenticityConfirmation() throws Exception {
        AuthRequest.VerifyCodefRequest request = AuthRequest.VerifyCodefRequest.builder()
                .name("전경환")
                .birth("981207")
                .identity("bQvUpPc1lO+khOzXaUXUwIZXddmE+dSpOT7JErdq11yUgpSoqte9/lG+HQZk7G1KPL5CTuywcUqPfHLHo7KmmPW47Rf7fUXWjbojl5ax1K7JTYYIq0dv0RAfRfNLVqR5EPYAbMXjOVN3zwLFdbELKEfs2c7BzFWyxt4mxXe3O8Srtjo0HgHmrzwuhcrfZIeAa/gH5FUyOoILyG7SfvvvipQqtLzCPwoIRUGUIscEZI78c8o9GUvdBEliPVapKzHZTgiEYYia45IL2Lq5giG0qrgmSthXU/HlO/eFjATE7dqzxEIbb85tScMyDiMC5oUqfB/c3RFAlV4gE3snl6I9Tg==")
                .phone("01093687950")
                .telecom("0")
                .issueDate("20161207")
                .build();

        String result = codefTwoFactorService.residentRegistrationAuthenticityConfirmation(request);

        System.out.println("result = " + result);

        Map<String, Object> m = om.readValue(result, new TypeReference<>() {});
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
    }

    @Test
    @DisplayName("캡차 인증")
    void processSecureNo() throws Exception {
        String SessionKey = "identity:eb06546e-aa59-4d57-81b3-cad36caf6b53";
        String SecureNo = "601221";

        String result = codefService.processSecureNo(SessionKey, SecureNo);

        System.out.println("result = " + result);

    }
}
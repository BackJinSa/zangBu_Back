package bjs.zangbu.documentReport.service;

import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.documentReport.gemini.GeminiClientSdk;
import bjs.zangbu.documentReport.gemini.PromptProvider;
import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.mongo.Dao.ReportDocumentDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;




@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
@Log4j2 // 필요 시
class CombinedSourceServiceImplTest {

    @Autowired
    private DocumentToMongoService documentToMongoService;
    @Autowired
    private ReportDocumentDao reportDocumentDao;
    @Autowired
    private CombinedSourceService combinedSourceService;
    @Autowired
    private PromptProvider promptProvider;

    @Autowired
    private GeminiClientSdk geminiClientSdk;
    private static final ObjectMapper OM = new ObjectMapper();

    private String read(String name) throws Exception {
        var res = new ClassPathResource(name);
        return new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Mongo 원본 → Combined → Gemini 리포트 생성")
    void generateReport_fromMongo_success() throws Exception {
        // --- 준비: 테스트 식별자
        final String memberId  = "it-member-001";
        final Long   buildingId = 112233L;

        // --- 1) 리소스 JSON 로드 → DTO 파싱
        String regJson = read("등기부등본-원본포함-0811.json");
        String brJson  = read("건축물대장2.json");

        var regDto = CodefConverter.parseDataToDto(regJson, EstateRegistrationResponse.class);
        var brDto  = CodefConverter.parseDataToDto(brJson,  BuildingRegisterResponse.class);
        assertNotNull(regDto);
        assertNotNull(brDto);

        // --- 2) Mongo 업서트 (resOriGinalData 제거는 서비스 내부에서 처리됨)
        documentToMongoService.saveJson(memberId, buildingId, DocumentType.ESTATE,            regDto);
        documentToMongoService.saveJson(memberId, buildingId, DocumentType.BUILDING_REGISTER, brDto);

        // --- 3) Mongo 저장 확인(필수는 아님, sanity check)
        Document d1 = reportDocumentDao.findOne(memberId, buildingId, DocumentType.ESTATE.name());
        Document d2 = reportDocumentDao.findOne(memberId, buildingId, DocumentType.BUILDING_REGISTER.name());
        assertNotNull(d1);
        assertNotNull(d2);
        assertNotNull(d1.get("parsed"));
        assertNotNull(d2.get("parsed"));

        // --- 4) inputs 구성 (프롬프트 규칙에서 요구하는 값)
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("market_price",        700_000_000L); // 시세
        inputs.put("my_deposit",          100_000_000L); // 내 보증금
        inputs.put("other_senior_claims", 0L);           // 기타 선순위

        // --- 5) (선택) Combined JSON 확인
        Map<String, Object> combined = combinedSourceService.buildCombined(memberId, buildingId, inputs);
        assertNotNull(combined);
        System.out.println("combined = " + combined);

        // --- 6) Gemini 호출 준비
        String combinedJson = OM.writeValueAsString(combined);
        System.out.println("combinedJson = " + combinedJson);
        String prompt = promptProvider.loadOrThrow();
        System.out.println("prompt = " + prompt);

//// 테스트 환경에서 키가 없으면 스킵(실행낭비 방지)
//        org.junit.jupiter.api.Assumptions.assumeTrue(
//                env.getProperty("gemini.api-key") != null && !env.getProperty("gemini.api-key").isBlank(),
//                "gemini.api-key 가 설정되어 있지 않습니다."
//        );

// --- 7) Gemini 호출 & 로그
        log.info("제미나이 응답을 기다리는 중...");
        String resultJson = geminiClientSdk.generate(prompt, combinedJson);

        System.out.println("resultJson = " + resultJson);
/*
        // Markdown -> HTML
        Parser parser = Parser.builder().build();
        Node doc = parser.parse(resultJson);
//        HtmlRenderer renderer = HtmlRenderer.builder()
//                .escapeHtml(true)          // 원본 HTML은 이스케이프
//                .percentEncodeUrls(true)   // URL 안전 처리
//                .build();
        HtmlRenderer renderer = HtmlRenderer.builder()
                .escapeHtml(false)         // 모델의 <br> 허용
                .percentEncodeUrls(true)
                .softBreak("<br />\n")     // 단일 개행도 <br>로
                .build();
        String html = renderer.render(doc);
*/

// (권장) HTML Sanitizer로 정화
        PolicyFactory policy = Sanitizers.FORMATTING
                .and(Sanitizers.LINKS)
                .and(Sanitizers.BLOCKS)   // <h3>, <h4>, <ul>, <ol>, <li>, <p> 등을 허용
                .and(Sanitizers.IMAGES)   // 이미지가 필요 없다면 제거 가능
                .and(Sanitizers.TABLES);  // 표를 쓴다면 유지, 아니면 빼도 됨
//        String safeHtml = policy.sanitize(html);
        String safeHtml = policy.sanitize(resultJson);

        System.out.println("safeHtml = " + safeHtml);

        // --- 10) 파일로 저장(수동 확인 용이)
        Path out = Paths.get("build/reports/genai-report.html");
        Files.createDirectories(out.getParent());// 상위 디렉토리(build/reports) 없으면 생성
        Files.write(out, safeHtml.getBytes(StandardCharsets.UTF_8)); // 파일에 UTF-8로 쓰기(없으면 생성, 있으면 덮어씀)
        System.out.println("Saved HTML -> " + out.toAbsolutePath());// 절대경로 문자열 확인용
    }

}
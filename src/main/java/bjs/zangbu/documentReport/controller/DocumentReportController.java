// src/main/java/bjs/zangbu/documentReport/web/DocumentReportController.java
package bjs.zangbu.documentReport.controller;

import bjs.zangbu.documentReport.dto.request.BuildingPriceDeposit;
import bjs.zangbu.documentReport.gemini.GeminiClientSdk;
import bjs.zangbu.documentReport.gemini.PromptProvider;
import bjs.zangbu.documentReport.mapper.DocumentReportMapper;
import bjs.zangbu.documentReport.service.CombinedSourceService;
import bjs.zangbu.security.account.vo.CustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.util.*;

@RestController
@RequestMapping("/ai/reports")
@RequiredArgsConstructor
@Api(tags = "분석 리포트")
public class DocumentReportController {

    private final CombinedSourceService combinedSourceService;
    private final PromptProvider promptProvider;
    private final GeminiClientSdk geminiClientSdk;
    private final DocumentReportMapper documentReportMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 분석 리포트 상세 조회
     */
    @ApiOperation(
            value = "분석 리포트 상세 조회",
            notes = "리포트 ID 대신 memberId/buildingId로 리포트를 생성/조회합니다.",
            response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "리포트 조회 성공"),
            @ApiResponse(code = 400, message = "리포트 요청 실패"),
            @ApiResponse(code = 404, message = "리포트를 찾을 수 없음")
    })
    @GetMapping("/{memberId}/{buildingId}")
    public ResponseEntity<?> getReport(
            @ApiIgnore @AuthenticationPrincipal CustomUser user,
            @PathVariable long buildingId) {

        final String memberId = user.getMember().getMemberId();

        // 1) building.price, building.deposit 조회
        BuildingPriceDeposit pd = documentReportMapper.selectPriceDeposit(buildingId);
        if (pd == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "BUILDING_NOT_FOUND",
                            "message", "해당 buildingId의 시세/보증금을 찾을 수 없습니다.",
                            "buildingId", buildingId));
        }

        // 2) inputs 맵 구성 (키 이름은 CombinedSourceService/프롬프트에서 기대하는 명칭으로)
        Map<String, Object> inputs = new HashMap<>();
        if (pd.getPrice() != null)   inputs.put("price",   pd.getPrice());   // 시세
        if (pd.getDeposit() != null) inputs.put("deposit", pd.getDeposit()); // 내 보증금

        try {
            // 3) 합쳐진 JSON 생성 (Mongo에서 가공한 등기부/건축물 + inputs)
            Map<String, Object> combined = combinedSourceService.buildCombined(memberId, buildingId, inputs);
            String combinedJson = objectMapper.writeValueAsString(combined);

            // 4) txt 프롬프트 로딩 + Gemini 호출
            String prompt = promptProvider.loadOrThrow();
            // Gemini 호출
            String html = geminiClientSdk.generate(prompt, combinedJson);

            // Markdown -> HTML
//            Parser parser = Parser.builder().build();
//            Node doc = parser.parse(markdown);
//            HtmlRenderer renderer = HtmlRenderer.builder()
//                    .escapeHtml(true)          // 원본 HTML은 이스케이프
//                    .percentEncodeUrls(true)   // URL 안전 처리
////                    .build();
//            HtmlRenderer renderer = HtmlRenderer.builder()
//                    .escapeHtml(false)         // 모델의 <br> 허용
//                    .percentEncodeUrls(true)
//                    .softBreak("<br />\n")     // 단일 개행도 <br>로
//                    .build();
//            String html = renderer.render(doc);

// (권장) HTML Sanitizer로 정화
            PolicyFactory policy = Sanitizers.FORMATTING
                    .and(Sanitizers.LINKS)
                    .and(Sanitizers.BLOCKS)   // <h3>, <h4>, <ul>, <ol>, <li>, <p> 등을 허용
                    .and(Sanitizers.IMAGES)   // 이미지가 필요 없다면 제거 가능
                    .and(Sanitizers.TABLES);  // 표를 쓴다면 유지, 아니면 빼도 됨
            String safeHtml = policy.sanitize(html);

// HTML로 응답
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/html; charset=UTF-8"))
                    .body(safeHtml);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "LLM_CALL_FAILED", "message", e.getMessage()));
        }
    }
}

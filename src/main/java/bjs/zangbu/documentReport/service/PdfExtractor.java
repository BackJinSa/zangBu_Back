package bjs.zangbu.documentReport.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class PdfExtractor {

    /**
     * @param jsonResponse API 응답 전체(JSON 문자열)
     * @param outPath      디코드한 PDF를 저장할 경로
     */
    public static void savePdfFromResponse(String jsonResponse, Path outPath) throws IOException {

        // 1) JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);

        // 2) resOriGinalData 필드 추출 (없으면 예외)
        String raw = root.path("data").path("resOriGinalData").asText();
        if (raw == null || raw.isEmpty()) {
            throw new IllegalArgumentException("resOriGinalData 가 비어 있습니다.");
        }

        // 3) CLI 와 동일한 전처리 -----------------------------------------
        //    3‑1) 문자열 안의 '\n' 두 글자(백슬래시+n) 제거
        String step1 = raw.replace("\\n", "");

        //    3‑2) 실제 개행·TAB·스페이스 등 화이트스페이스 제거
        String cleaned = step1.replaceAll("\\s+", "");
        // ---------------------------------------------------------------

        // 4) Base‑64 디코딩
        byte[] pdfBytes = Base64.getDecoder().decode(cleaned);

        // 5) 파일로 저장
        Files.write(outPath, pdfBytes);
        System.out.println("✅ PDF 저장 완료 → " + outPath.toAbsolutePath());
    }

// 테스트용 main --------------------------------------------------------
//    public static void main(String[] args) throws IOException {
//      // (1) 응답 JSON 을 문자열로 읽어온 뒤
//      String json = Files.readString(Path.of("result_test.txt")); // 예: 파일에서 읽기
//
//      // (2) PDF 저장
//      savePdfFromResponse(json, Path.of("output.pdf"));
}
//  }
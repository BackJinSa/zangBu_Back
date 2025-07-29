package bjs.zangbu.deal.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public final class PdfUtil {

    private PdfUtil() {}

    /** Base‑64 인코딩 문자열을 PDF 바이트로 변환 */
    public static byte[] decodePdfBytes(String base64Raw) {
        String cleaned = base64Raw
                .replace("\\n", "")   // 리터럴 '\n'
                .replaceAll("\\s", ""); // 공백류 전부
        return Base64.getDecoder().decode(cleaned);
    }

    /** (개발용) 임시 디렉터리에 파일까지 만들어보고 싶을 때만 사용 */
    public static Path saveTempPdf(byte[] pdfBytes,
                                   String baseFileName,
                                   long dealId) throws IOException {

        String fileName = baseFileName.replace(".pdf", "")
                + "-" + dealId + ".pdf";

        Path dir  = Files.createTempDirectory("building-register");
        Path path = dir.resolve(fileName);

        Files.write(path, pdfBytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        return path;   // 필요 없으면 호출 안 해도 OK
    }
}

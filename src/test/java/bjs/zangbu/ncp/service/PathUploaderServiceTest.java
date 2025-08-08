package bjs.zangbu.ncp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bjs.zangbu.global.config.RootConfig;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * {@link PathUploaderService}의 파일 업로드/다운로드 기능을 검증하는 단위 테스트 클래스
 *
 * <p>다음 기능을 검증함:
 * <ul>
 *   <li>PDF 파일 업로드</li>
 *   <li>PDF 파일 다운로드</li>
 *   <li>오브젝트 목록 조회</li>
 *   <li>유효하지 않은 파일 업로드 시 예외 처리</li>
 * </ul>
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
class PathUploaderServiceTest {

  @Autowired
  PathUploaderService pathUploaderService;

  final String bucketName = "bjs-bucket";
  final String objectName = "test-folder/test.pdf";
  final String localFilePath = "src/test/resources/test.pdf";
  final String downloadPath = "src/test/resources/downloaded.pdf";

  /**
   * 유효한 PDF 파일 경로를 업로드하면 public URL이 반환되는지 테스트
   */
  @Test
  @DisplayName("유효한 PDF 파일 업로드 - public URL 반환")
  void putObject() throws Exception {
    File file = new File(localFilePath);
    assertTrue(file.exists(), "테스트용 PDF 파일이 존재하지 않음");

    String publicUrl = pathUploaderService.putObject(bucketName, objectName, localFilePath);

    assertNotNull(publicUrl, "업로드된 URL이 null임");
    assertTrue(publicUrl.contains(bucketName), "URL에 버킷 이름이 포함되지 않음");
    assertTrue(publicUrl.contains(objectName), "URL에 오브젝트 이름이 포함되지 않음");

    log.info("업로드된 공개 URL: {}", publicUrl);
  }

  /**
   * 지정된 경로로 오브젝트 다운로드가 정상적으로 수행되는지 테스트
   */
  @Test
  @DisplayName("오브젝트 다운로드 - PDF 형식 확인")
  void getObject() throws Exception {
    pathUploaderService.getObject(bucketName, objectName, downloadPath);

    File downloadedFile = new File(downloadPath);
    assertTrue(downloadedFile.exists(), "다운로드된 파일이 존재하지 않음");

    String contentType = Files.probeContentType(Paths.get(downloadPath));
    assertEquals("application/pdf", contentType, "다운로드된 파일이 PDF가 아님");

    downloadedFile.delete(); // 정리
  }

  /**
   * 특정 prefix 조건으로 오브젝트 목록을 조회하면 <Key> 태그가 포함되는지 테스트
   */
  @Test
  @DisplayName("오브젝트 목록 조회 - <Key> 태그 포함 확인")
  void listObjects() throws Exception {
    String result = pathUploaderService.listObjects(bucketName, "prefix=test-folder/&delimiter=/");

    assertNotNull(result);
    assertTrue(result.contains("<Key>"), "오브젝트 목록에 <Key> 태그가 있어야 함");

    log.info("조회 결과:\n{}", result);
  }

  /**
   * PDF 파일이 아닌 경우 업로드 시 예외가 발생하는지 테스트
   */
  @Test
  @DisplayName("비 PDF 파일 업로드 - IllegalArgumentException 발생")
  void putObject_throwsException_ifNotPdf() {
    String nonPdfPath = "src/test/resources/not-a-pdf.txt";
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      pathUploaderService.putObject(bucketName, "test-folder/test.pdf.base64", nonPdfPath);
    });

    assertTrue(exception.getMessage().contains("PDF"), "예외 메시지에 'PDF'가 포함되어야 함");
  }

  /**
   * 파일 확장자는 PDF지만 내용이 PDF가 아닌 경우 예외 발생 여부 테스트
   */
  @Test
  @DisplayName("PDF 시그니처 불일치 파일 업로드 - IllegalArgumentException 발생")
  void putObject_throwsException_ifNotPdf_signatureMismatch() throws Exception {
    // JPEG 시그니처 바이트 생성
    byte[] jpegBytes = new byte[]{
        (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
        0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01
    };

    // 임시 JPEG 파일 생성
    String tempImagePath = "src/test/resources/temp-test-upload.jpeg";
    Files.write(Paths.get(tempImagePath), jpegBytes);
    assertTrue(new File(tempImagePath).exists(), "임시 JPEG 파일 생성 실패");

    // 업로드 시도 → 예외 검증
    String jpegObjectKey = "test-folder/test-upload.jpeg";
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      pathUploaderService.putObject(bucketName, jpegObjectKey, tempImagePath);
    });

    assertTrue(exception.getMessage().contains("PDF"), "예외 메시지에 'PDF'가 포함되어야 함");

    // 정리
    new File(tempImagePath).delete();
  }
}

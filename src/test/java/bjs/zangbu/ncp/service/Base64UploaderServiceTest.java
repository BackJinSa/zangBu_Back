package bjs.zangbu.ncp.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bjs.zangbu.global.config.RootConfig;
import java.util.Base64;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * {@link Base64UploaderService}의 PDF 업로드 기능을 검증하는 단위 테스트 클래스
 *
 * <p>
 * - 유효한 base64 PDF → 정상 업로드<br> - data URI prefix 포함 → 정상 업로드<br> - 유효하지 않은 PDF → 예외 발생
 * </p>
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
class Base64UploaderServiceTest {

  @Autowired
  Base64UploaderService base64UploaderService;

  final String bucketName = "bjs-bucket";

  /**
   * 유효한 base64 PDF 문자열이 주어졌을 때 public URL이 반환되는지 테스트
   */
  @Test
  @DisplayName("유효한 base64 PDF 업로드 - public URL 반환")
  void uploadBase64Pdf_validPdf_shouldReturnPublicUrl() throws Exception {
    // given
    byte[] pdfBytes = new byte[]{
        0x25, 0x50, 0x44, 0x46, 0x2D // %PDF-
    };
    String base64 = Base64.getEncoder().encodeToString(pdfBytes);
    String objectName = "test-folder/base64-valid.pdf";

    // when
    String url = base64UploaderService.uploadBase64Pdf(base64, bucketName, objectName);

    // then
    assertNotNull(url, "URL이 null이면 안 됨");
    assertTrue(url.contains(bucketName), "URL에 버킷 이름이 포함되어야 함");
    assertTrue(url.contains(objectName), "URL에 오브젝트 키가 포함되어야 함");
  }

  /**
   * data URI prefix가 포함된 base64 PDF 문자열이 주어졌을 때 정상적으로 업로드되는지 테스트
   */
  @Test
  @DisplayName("data URI prefix 포함된 base64 PDF 업로드 - public URL 반환")
  void uploadBase64Pdf_withDataUriPrefix_shouldReturnPublicUrl() throws Exception {
    // given
    byte[] pdfBytes = new byte[]{
        0x25, 0x50, 0x44, 0x46, 0x2D // %PDF-
    };
    String base64 = Base64.getEncoder().encodeToString(pdfBytes);
    String dataUri = "data:application/pdf;base64," + base64;
    String objectName = "test-folder/base64-valid-prefixed.pdf";

    // when
    String url = base64UploaderService.uploadBase64Pdf(dataUri, bucketName, objectName);

    // then
    assertNotNull(url);
    assertTrue(url.contains(bucketName));
    assertTrue(url.contains(objectName));
  }

  /**
   * 유효하지 않은 base64 PDF 문자열이 주어졌을 때 {@link IllegalArgumentException} 발생 여부 테스트
   */
  @Test
  @DisplayName("잘못된 base64 PDF 업로드 - IllegalArgumentException 발생")
  void uploadBase64Pdf_invalidPdf_shouldThrowIllegalArgumentException() {
    // given
    byte[] nonPdfBytes = "hello world".getBytes();
    String base64 = Base64.getEncoder().encodeToString(nonPdfBytes);
    String objectName = "test-folder/base64-invalid.pdf";

    // when & then
    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      base64UploaderService.uploadBase64Pdf(base64, bucketName, objectName);
    });

    assertTrue(e.getMessage().toLowerCase().contains("pdf"), "예외 메시지에 'pdf'가 포함되어야 함");
  }
}

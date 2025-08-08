package bjs.zangbu.ncp.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bjs.zangbu.global.config.RootConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * {@link BinaryUploaderService}의 PDF 바이너리 업로드 기능을 검증하는 단위 테스트 클래스
 *
 * <p>
 * - 유효한 PDF → 정상 업로드<br> - 유효하지 않은 PDF → 예외 발생
 * </p>
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
class BinaryUploaderServiceTest {

  @Autowired
  BinaryUploaderService binaryUploaderService;

  final String bucketName = "bjs-bucket";

  /**
   * 유효한 PDF 시그니처가 포함된 바이트 배열을 업로드했을 때 public URL이 반환되는지 테스트
   */
  @Test
  @DisplayName("유효한 PDF 바이너리 업로드 - public URL 반환")
  void putPdfObject_validPdf_shouldReturnPublicUrl() throws Exception {
    // given
    byte[] validPdfBytes = new byte[]{
        0x25, 0x50, 0x44, 0x46, 0x2D  // %PDF-
    };
    String objectName = "test-folder/binary-upload-valid.pdf";

    // when
    String url = binaryUploaderService.putPdfObject(bucketName, objectName, validPdfBytes);

    // then
    assertNotNull(url, "업로드된 URL은 null이 아니어야 함");
    assertTrue(url.contains(bucketName), "URL에 버킷 이름이 포함되어야 함");
    assertTrue(url.contains(objectName), "URL에 오브젝트 이름이 포함되어야 함");
  }

  /**
   * 유효하지 않은 바이트 배열을 업로드할 경우 IllegalArgumentException이 발생하는지 테스트
   */
  @Test
  @DisplayName("잘못된 PDF 바이너리 업로드 - IllegalArgumentException 발생")
  void putPdfObject_invalidPdf_shouldThrowIllegalArgumentException() {
    // given
    byte[] invalidBytes = "this is not a pdf".getBytes();
    String objectName = "test-folder/binary-upload-invalid.pdf";

    // when & then
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      binaryUploaderService.putPdfObject(bucketName, objectName, invalidBytes);
    });

    assertTrue(exception.getMessage().toLowerCase().contains("pdf"), "예외 메시지에 'pdf'가 포함되어야 함");
  }
}

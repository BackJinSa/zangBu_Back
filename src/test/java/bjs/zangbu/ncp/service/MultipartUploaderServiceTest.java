package bjs.zangbu.ncp.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bjs.zangbu.global.config.RootConfig;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * {@link MultipartUploaderServiceImpl}의 기능을 검증하는 단위 테스트 클래스
 *
 * <p>다음 시나리오를 검증함:
 * <ul>
 *   <li>올바른 JPEG 파일 업로드 시 public URL 반환</li>
 *   <li>허용되지 않은 파일 형식 업로드 시 예외 발생</li>
 *   <li>미완료 업로드 목록 조회 시 정상 반환</li>
 *   <li>업로드 중단 요청 시 예외 없이 동작</li>
 * </ul>
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
class MultipartUploaderServiceTest {

  @Autowired
  MultipartUploaderService multipartUploaderService;

  final String bucket = "bjs-bucket";
  final String objectKey = "test-folder/sample.jpg";

  /**
   * 유효한 JPEG 파일 업로드 시 public URL 반환되는지 테스트
   */
  @Test
  @DisplayName("유효한 JPEG 파일 업로드 - public URL 반환")
  void multipartUpload_validJpegFile_shouldReturnUrl() throws Exception {
    byte[] jpegBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // JPEG 헤더
    MockMultipartFile file = new MockMultipartFile(
        "file", "sample.jpeg", "image/jpeg", jpegBytes
    );

    String url = multipartUploaderService.multipartUpload(bucket, objectKey, file);

    assertNotNull(url, "업로드된 파일의 URL은 null이 아니어야 함");
    assertTrue(url.contains(bucket), "URL에 버킷 이름이 포함되어야 함");
    assertTrue(url.contains(objectKey), "URL에 오브젝트 키가 포함되어야 함");
  }

  /**
   * 잘못된 파일 형식 업로드 시 IllegalArgumentException 발생하는지 테스트
   */
  @Test
  @DisplayName("잘못된 파일 형식 업로드 - IllegalArgumentException 발생")
  void multipartUpload_invalidFileType_shouldThrowIllegalArgumentException() {
    byte[] txtBytes = "hello".getBytes();
    MockMultipartFile file = new MockMultipartFile(
        "file", "not-image.txt", "text/plain", txtBytes
    );

    Exception e = assertThrows(IllegalArgumentException.class, () -> {
      multipartUploaderService.multipartUpload(bucket, "test-folder/invalid.txt", file);
    });

    assertTrue(e.getMessage().toLowerCase().contains("jpeg") || e.getMessage().toLowerCase()
        .contains("jpg"));
  }

  /**
   * 미완료된 업로드 목록 조회 시 objectKey와 uploadId가 정상적으로 포함되는지 테스트
   */
  @Test
  @DisplayName("미완료 업로드 목록 조회 - objectKey, uploadId 포함")
  void listIncompleteUploads_shouldReturnList() throws Exception {
    List<Pair<String, String>> result = multipartUploaderService.listIncompleteUploads(bucket);

    assertNotNull(result, "결과 리스트는 null이 아니어야 함");
    for (Pair<String, String> pair : result) {
      assertNotNull(pair.getLeft(), "objectKey는 null이 아니어야 함");
      assertNotNull(pair.getRight(), "uploadId는 null이 아니어야 함");
    }
  }

  /**
   * 미완료된 업로드 세션이 존재할 경우 중단 요청이 정상 동작하는지 테스트
   */
  @Test
  @DisplayName("업로드 중단 - 세션 존재 시 정상 동작")
  void abortMultipartUpload_shouldSucceedIfUploadIdExists() throws Exception {
    List<Pair<String, String>> uploads = multipartUploaderService.listIncompleteUploads(bucket);

    if (!uploads.isEmpty()) {
      Pair<String, String> upload = uploads.get(0);
      String objectKey = upload.getLeft();
      String uploadId = upload.getRight();

      assertDoesNotThrow(() -> {
        multipartUploaderService.abortMultipartUpload(bucket, objectKey, uploadId);
      });
    } else {
      System.out.println("중단할 업로드 세션이 없음 (테스트 스킵)");
    }
  }
}

package bjs.zangbu.ncp.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class UploadUtil {

  /**
   * HTTP 응답 상태 코드가 성공(2xx)인지 확인
   *
   * @param response 응답 객체
   * @param action   수행한 작업 이름 (로깅 및 예외 메시지용)
   * @throws IllegalStateException 응답 코드가 실패(2xx 외)인 경우
   */
  public static void isSuccess(HttpResponse response, String action) throws Exception {
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode < 200 || statusCode >= 300) {
      throw new IllegalStateException(action + " 실패. 응답 코드: " + statusCode);
    } else {
      log.info("{} 성공. 응답 코드: {}", action, statusCode);
    }
  }

  /**
   * 파일의 MIME 타입과 확장자가 유효한 타입인지 확인
   *
   * @param allowedMimeType 허용할 MIME 타입 (예: "image/jpeg")
   * @param multipartFile   검사할 파일
   * @return 조건을 만족하면 true, 그렇지 않으면 false
   */
  public static boolean isAllowedMultipartFileType(String allowedMimeType,
      MultipartFile multipartFile) {
    // TODO: mime type 확인 필요
//    String contentType = multipartFile.getContentType();
//    log.info("contentType: {}", contentType);
    String originalFilename = multipartFile.getOriginalFilename();
    log.info("originalFilename: {}", originalFilename);

//    return allowedMimeType.equals(contentType)
//        && (originalFilename == null || originalFilename.toLowerCase().endsWith(".jpg"));
    return originalFilename.toLowerCase().endsWith(".jpg") || originalFilename.toLowerCase()
        .endsWith(".jpeg");
  }

  /**
   * 주어진 byte 배열이 PDF 파일인지 판단
   *
   * @param bytes 파일 바이너리
   * @return PDF이면 true, 아니면 false
   */
  public static boolean isPdfBytes(byte[] bytes) {
    if (bytes == null || bytes.length < 5) {
      return false;
    }
    // PDF 파일은 항상 "%PDF-"로 시작함
    return new String(bytes, 0, 5).startsWith("%PDF-");
  }

  /**
   * 로컬 파일이 PDF 파일인지 검사 <br> 아래 두 조건을 모두 만족해야 PDF로 판단됨
   * <ul>
   *   <li>파일 시그니처가 "%PDF-"로 시작하는지</li>
   *   <li>파일의 MIME 타입이 "application/pdf"인지</li>
   * </ul>
   *
   * @param file 검사할 파일
   * @return 유효한 PDF 파일이면 {@code true}, 아니면 {@code false}
   */
  public static boolean isPdfFile(File file) {
    try {
      if (file == null || !file.exists() || file.length() < 5) {
        return false;
      }

      // 시그니처 검사
      try (FileInputStream fis = new FileInputStream(file)) {
        byte[] header = new byte[5];
        if (fis.read(header) != 5) {
          return false;
        }
        if (!new String(header).startsWith("%PDF-")) {
          return false;
        }
      }

      // MIME 검사
      String mimeType = Files.probeContentType(file.toPath());
      return "application/pdf".equalsIgnoreCase(mimeType);
    } catch (Exception e) {
      return false;
    }
  }

}

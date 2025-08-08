package bjs.zangbu.ncp.service;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Base64로 인코딩된 PDF 데이터를 디코딩하여 Object Storage에 업로드하는 서비스
 */
@Service
@RequiredArgsConstructor
public class Base64UploaderServiceImpl implements Base64UploaderService {

  private final BinaryUploaderService binaryUploaderService;

  /**
   * base64 인코딩된 PDF 문자열을 디코딩하여 Object Storage에 업로드하고, 성공 시 해당 파일의 public URL을 반환합니다.
   *
   * @param base64Pdf  base64 인코딩된 PDF 문자열 (data URI prefix 포함 가능)
   * @param bucketName 업로드 대상 버킷 이름
   * @param objectName 저장할 오브젝트 키 (예: "folder/my.pdf")
   * @return 업로드된 파일의 public URL
   * @throws Exception 업로드 실패 또는 유효하지 않은 PDF일 경우 예외 발생
   */
  public String uploadBase64Pdf(String base64Pdf, String bucketName, String objectName)
      throws Exception {

    // base64Pdf 문자열 앞에 data:application/pdf;base64,... 가 포함되어 있다면 제거
    if (base64Pdf.contains(",")) {
      base64Pdf = base64Pdf.substring(base64Pdf.indexOf(",") + 1);
    }

    String cleaned = base64Pdf.replaceAll("\\\\n", "")   // 문자열 '\n' 제거
        .replaceAll("\\\\r", "")
        .replaceAll("\\\\", "")    // 역슬래시 자체 제거
        .replaceAll("\"", "")      // 따옴표 제거
        .replaceAll("\\s+", "");   // 모든 공백 문자 제거

    byte[] pdfBytes = Base64.getDecoder().decode(cleaned);
    return binaryUploaderService.putPdfObject(bucketName, objectName, pdfBytes);
  }

}

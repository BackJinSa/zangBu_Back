package bjs.zangbu.ncp.service;

/**
 * PDF 바이너리 데이터를 NCP Object Storage에 업로드하는 서비스
 *
 * <p>업로드 전 PDF 시그니처 유효성을 검사하며, 업로드 성공 시 공개 URL을 반환함.
 *
 * @see bjs.zangbu.ncp.service.BinaryUploaderServiceTest
 */
public interface BinaryUploaderService {

  /**
   * PDF 바이너리 데이터를 NCP Object Storage에 업로드
   *
   * <ul>
   *   <li>업로드 전 PDF 파일 시그니처가 유효한지 검사</li>
   *   <li>업로드 성공 시 공개 접근 가능한 파일 URL을 반환</li>
   * </ul>
   *
   * @param bucketName 업로드 대상 버킷 이름 (예: "bjs-bucket")
   * @param objectName 업로드될 오브젝트 키 (예: "folder/sample.pdf")
   * @param pdfBytes   업로드할 PDF 파일의 바이너리 데이터
   * @return 업로드된 PDF 파일의 Object Storage 공개 URL
   * @throws IllegalArgumentException PDF 시그니처가 유효하지 않을 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception                업로드 요청 또는 응답 처리 중 오류가 발생할 경우
   * @see bjs.zangbu.ncp.service.BinaryUploaderServiceTest#putPdfObject_validPdf_shouldReturnPublicUrl()
   * @see bjs.zangbu.ncp.service.BinaryUploaderServiceTest#putPdfObject_invalidPdf_shouldThrowIllegalArgumentException()
   */
  String putPdfObject(String bucketName, String objectName, byte[] pdfBytes) throws Exception;
}

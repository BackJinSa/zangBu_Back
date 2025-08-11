package bjs.zangbu.ncp.service;

/**
 * base64 인코딩된 PDF 파일을 NCP Object Storage에 업로드하는 서비스
 *
 * <p>업로드 대상은 PDF 형식이어야 하며, 시그니처 검증 및 업로드 성공 여부는 단위 테스트를 통해 검증함.
 */
public interface Base64UploaderService {

  /**
   * base64 인코딩된 PDF 문자열을 디코딩하여 Object Storage에 업로드하고, 성공 시 해당 파일의 public URL을 반환함
   *
   * @param base64Pdf  base64 인코딩된 PDF 문자열 (data URI prefix 포함 가능)
   * @param bucketName 업로드 대상 버킷 이름
   * @param objectName 저장할 오브젝트 키 (예: "folder/my.pdf")
   * @return 업로드된 파일의 public URL
   * @throws IllegalArgumentException PDF 시그니처가 유효하지 않을 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception                업로드 실패 또는 유효하지 않은 PDF일 경우 예외 발생
   */
  String uploadBase64Pdf(String base64Pdf, String bucketName, String objectName)
      throws Exception;
}
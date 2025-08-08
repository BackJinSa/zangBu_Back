package bjs.zangbu.ncp.service;

/**
 * 로컬 PDF 파일을 Object Storage에 업로드, 다운로드, 목록 조회하는 기능을 제공하는 서비스
 *
 * @see bjs.zangbu.ncp.service.PathUploaderServiceTest
 */
public interface PathUploaderService {

  /**
   * 로컬 PDF 파일을 Object Storage에 업로드하고 public URL 반환
   *
   * @param bucketName    업로드 대상 버킷 이름
   * @param objectName    업로드될 오브젝트 키 (예: "documents/sample.pdf")
   * @param localFilePath 업로드할 로컬 파일 경로
   * @return 업로드된 PDF 파일의 Object Storage 공개 URL
   * @throws IllegalArgumentException PDF가 아닌 파일일 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우) 발생
   * @throws Exception                업로드 과정 중 오류 발생 시
   * @see bjs.zangbu.ncp.service.PathUploaderServiceTest#putObject()
   * @see bjs.zangbu.ncp.service.PathUploaderServiceTest#putObject_throwsException_ifNotPdf()
   * @see bjs.zangbu.ncp.service.PathUploaderServiceTest#putObject_throwsException_ifNotPdf_signatureMismatch()
   */
  String putObject(String bucketName, String objectName, String localFilePath) throws Exception;

  /**
   * Object Storage에 있는 오브젝트를 다운로드하여 로컬에 저장
   *
   * <ul>
   *   <li>HTTP 응답 성공 여부 확인</li>
   *   <li>다운로드한 파일이 PDF 형식이 아닌 경우 삭제 및 예외 발생</li>
   * </ul>
   *
   * @param bucketName    다운로드 대상 버킷 이름
   * @param objectName    다운로드할 오브젝트 키
   * @param localFilePath 저장될 로컬 파일 경로
   * @throws IllegalArgumentException 다운로드된 파일이 PDF가 아닐 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우) 발생
   * @throws Exception                다운로드 중 오류 발생 시
   * @see bjs.zangbu.ncp.service.PathUploaderServiceTest#getObject()
   */
  void getObject(String bucketName, String objectName, String localFilePath) throws Exception;

  /**
   * Object Storage의 버킷 내 오브젝트 목록을 조회
   *
   * <ul>
   *   <li>prefix, delimiter 등 쿼리스트링으로 조건 조회 가능</li>
   * </ul>
   *
   * @param bucketName  조회 대상 버킷 이름
   * @param queryString 조회 조건 (예: "prefix=documents/&delimiter=/")
   * @return 조회 결과 XML 문자열
   * @throws IllegalStateException 응답이 실패(2xx가 아닌 경우) 발생
   * @throws Exception             목록 조회 중 오류 발생 시
   * @see bjs.zangbu.ncp.service.PathUploaderServiceTest#listObjects()
   */
  String listObjects(String bucketName, String queryString) throws Exception;
}

package bjs.zangbu.ncp.service;

/**
 * 로컬 파일 업로드, 조회, 목록 조회
 */
public interface ObjectStorageService {

  /**
   * 로컬 파일을 Object Storage 에 업로드함.
   *
   * @param bucketName    버킷 이름
   * @param objectName    오브젝트 키
   * @param localFilePath 로컬 파일 경로
   */
  void putObject(String bucketName, String objectName, String localFilePath) throws Exception;

  /**
   * Object Storage 에 있는 오브젝트 다운로드해서 로컬에 저장함.
   *
   * @param bucketName    버킷 이름
   * @param objectName    오브젝트 키
   * @param localFilePath 저장할 로컬 경로
   */
  void getObject(String bucketName, String objectName, String localFilePath) throws Exception;

  /**
   * Object Storage 버킷 내 오브젝트 목록 조회함.
   *
   * @param bucketName  버킷 이름
   * @param queryString 쿼리 스트링 (prefix, delimiter 등)
   */
  void listObjects(String bucketName, String queryString) throws Exception;
}

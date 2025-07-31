package bjs.zangbu.ncp.service;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.multipart.MultipartFile;

/**
 * Multipart 업로드
 */
public interface MultipartUploaderService {

  /**
   * 파일을 멀티파트 업로드 방식으로 업로드함
   *
   * @param bucket        버킷 이름임
   * @param objectKey     저장할 경로+파일명임
   * @param multipartFile 업로드할 파일임
   * @return 공개 접근 가능한 URL 리턴함
   * @throws Exception 중간에 실패하면 예외 던짐
   */
  String multipartUpload(String bucket, String objectKey, MultipartFile multipartFile)
      throws Exception;

  /**
   * 아직 완료되지 않은 멀티파트 업로드 목록 조회함
   *
   * @param bucket 버킷 이름
   * @return (objectKey, uploadId) 쌍 리스트로 리턴함
   * @throws Exception 실패 시 예외 발생함
   */
  List<Pair<String, String>> listIncompleteUploads(String bucket) throws Exception;

  /**
   * 특정 업로드 세션을 중단하고 파트 삭제함
   *
   * @param bucket    버킷 이름
   * @param objectKey 오브젝트 키
   * @param uploadId  업로드 식별자
   * @throws Exception 실패 시 예외 발생함
   */
  void abortMultipartUpload(String bucket, String objectKey, String uploadId)
      throws Exception;
}

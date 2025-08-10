package bjs.zangbu.ncp.service;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.multipart.MultipartFile;

/**
 * Multipart 방식으로 Object Storage에 JPEG 파일을 업로드하거나, 멀티파트 업로드 상태를 제어하는 기능을 제공하는 서비스
 */
public interface MultipartUploaderService {

  /**
   * 멀티파트 방식으로 JPEG 파일을 업로드함
   *
   * <p>업로드 성공 시 해당 파일에 접근할 수 있는 공개 URL을 반환함
   *
   * @param bucket        업로드할 버킷 이름
   * @param objectKey     저장될 오브젝트 키 (경로 포함)
   * @param multipartFile 업로드할 Multipart 파일
   * @return 업로드된 오브젝트의 public URL
   * @throws IllegalArgumentException JPEG 파일이 아닌 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception                업로드 도중 실패한 경우
   */
  String multipartUpload(String bucket, String objectKey, MultipartFile multipartFile)
      throws Exception;

  /**
   * 아직 완료되지 않은 멀티파트 업로드 목록 조회
   *
   * @param bucket 버킷 이름
   * @return (objectKey, uploadId) 쌍 리스트 반환
   * @throws IllegalStateException 응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception             실패 시 예외 발생
   */
  List<Pair<String, String>> listIncompleteUploads(String bucket) throws Exception;

  /**
   * 특정 업로드 세션을 중단하고 파트를 삭제함
   *
   * @param bucket    버킷 이름
   * @param objectKey 오브젝트 키
   * @param uploadId  업로드 식별자
   * @throws IllegalStateException 업로드 중단 요청이 실패한 경우 (예: 4xx, 5xx 응답)
   * @throws Exception             실패 시 예외 발생
   */
  void abortMultipartUpload(String bucket, String objectKey, String uploadId)
      throws Exception;
}

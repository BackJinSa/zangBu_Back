package bjs.zangbu.ncp.service;

public interface HtmlUploaderService {

  /**
   * HTML 문자열을 Object Storage에 업로드
   *
   * @param bucketName 업로드할 버킷 이름
   * @param objectName 업로드될 오브젝트 키 (예: "pages/sample.html")
   * @param htmlString 업로드할 HTML 문자열
   * @return 업로드된 HTML 파일의 공개 URL
   * @throws Exception 업로드 과정에서 오류 발생 시
   */
  String putHtmlObject(String bucketName, String objectName, String htmlString)
      throws Exception;
}

package bjs.zangbu.ncp.service;

import static bjs.zangbu.ncp.auth.HeaderCreation.authorization;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ACCESS_KEY;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ENDPOINT;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.REGION_NAME;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.SECRET_KEY;
import static bjs.zangbu.ncp.util.UploadUtil.isAllowedMultipartFileType;
import static bjs.zangbu.ncp.util.UploadUtil.isSuccess;

import bjs.zangbu.ncp.tag.PartETag;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * NCP Object Storage에 멀티파트 업로드를 수행하는 서비스 구현체
 *
 * <p>기본 동작 순서:
 * <ul>
 *   <li>1단계: initiateUpload() - 업로드 세션 생성</li>
 *   <li>2단계: uploadPart() - 파일을 파트 단위로 업로드</li>
 *   <li>3단계: completeUpload() - 업로드 완료 요청</li>
 * </ul>
 *
 * <p>※ 업로드된 파일을 public-read로 설정하는 기능은 선택적으로 사용 가능합니다.
 * {@link #makeObjectPublic(String, String)} 참고
 */
@Service
@Log4j2
//@Slf4j
public class MultipartUploaderServiceImpl implements MultipartUploaderService {

  private static final int PART_SIZE = 5 * 1024 * 1024; // 5MB

  /**
   * 멀티파트 방식으로 JPEG 파일을 업로드함
   *
   * <p>업로드 성공 시 해당 파일에 접근할 수 있는 공개 URL을 반환
   *
   * @param bucket        업로드할 버킷 이름
   * @param objectKey     저장될 오브젝트 키 (경로 포함)
   * @param multipartFile 업로드할 Multipart 파일
   * @return 업로드된 오브젝트의 public URL
   * @throws IllegalArgumentException JPEG 파일이 아닌 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception                업로드 도중 실패한 경우
   */
  @Override
  public String multipartUpload(String bucket, String objectKey, MultipartFile multipartFile)
      throws Exception {

    // JPEG MIME 타입 또는 확장자 검사
    if (!isAllowedMultipartFileType("image/jpeg", multipartFile)) {
      log.error("jpeg 확장자만 허용 됩니다.");
      throw new IllegalArgumentException("jpeg 확장자만 허용 됩니다.");

    }

    // MultipartFile → File 변환 (임시 파일로)
    File tempFile = File.createTempFile("ncp-upload-", ".tmp");
    multipartFile.transferTo(tempFile);

    try {
      HttpClient httpClient = HttpClientBuilder.create().build();

      // 업로드 세션 시작
      String uploadId = initiateUpload(httpClient, bucket, objectKey);

      // 파트별 업로드
      List<PartETag> partETags = new ArrayList<>();
      FileInputStream fis = new FileInputStream(tempFile);
      byte[] buffer = new byte[PART_SIZE];
      int bytesRead, partNumber = 1;
      while ((bytesRead = fis.read(buffer)) != -1) {
        InputStream partStream = new java.io.ByteArrayInputStream(buffer, 0, bytesRead);
        String eTag = uploadPart(httpClient, bucket, objectKey, uploadId, partNumber, partStream,
            bytesRead);
        partETags.add(new PartETag(partNumber, eTag));
        partNumber++;
      }
      fis.close();

      // 업로드 완료 요청
      HttpResponse response = completeUpload(httpClient, bucket, objectKey, uploadId, partETags);
      isSuccess(response, "completeUpload");
      // 업로드된 오브젝트를 public-read 로 설정
//      makeObjectPublic(bucket, objectKey);

      // 업로드된 파일의 공개 접근 URL 반환
      return ENDPOINT + "/" + bucket + "/" + objectKey;

    } finally {
      // 임시 파일 삭제
      if (tempFile.exists()) {
        tempFile.delete();
      }

    }
  }

  // 파일 공개 설정
  private void makeObjectPublic(String bucket, String objectKey) throws Exception {
    HttpClient client = HttpClientBuilder.create().build();
    HttpPut request = new HttpPut(ENDPOINT + "/" + bucket + "/" + objectKey + "?acl");

    request.addHeader("Host", request.getURI().getHost());
    request.addHeader("x-amz-acl", "public-read");

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    client.execute(request);
  }

  /**
   * 멀티파트 업로드 세션 시작함
   *
   * @param client HttpClient 객체
   * @param bucket 버킷 이름
   * @param key    오브젝트 키
   * @return 업로드 식별자 UploadId
   * @throws IllegalStateException 응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception             실패 시 예외 발생함
   */
  private String initiateUpload(HttpClient client, String bucket, String key) throws Exception {
    String url = ENDPOINT + "/" + bucket + "/" + key + "?uploads";
    HttpPost request = new HttpPost(url);
    request.addHeader("Host", request.getURI().getHost());
    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);
    HttpResponse response = client.execute(request);
    isSuccess(response, "initiateUpload");

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(response.getEntity().getContent());
    return doc.getElementsByTagName("UploadId").item(0).getTextContent();
  }

  /**
   * 업로드 세션에 파트 추가함
   *
   * @param client     HttpClient
   * @param bucket     버킷명
   * @param key        오브젝트 키
   * @param uploadId   업로드 식별자
   * @param partNumber 파트 번호
   * @param partData   업로드할 파트 데이터
   * @param size       바이트 크기
   * @return 업로드 후 ETag 값 반환함
   * @throws IllegalStateException 응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception             실패 시 예외 발생
   */
  public String uploadPart(HttpClient client, String bucket, String key, String uploadId,
      int partNumber, InputStream partData, int size) throws Exception {
    String url = String.format("%s/%s/%s?partNumber=%d&uploadId=%s",
        ENDPOINT, bucket, key, partNumber, uploadId);

    HttpPut request = new HttpPut(url);
    request.addHeader("Host", request.getURI().getHost());
    request.setEntity(new InputStreamEntity(partData, size));
    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);
    HttpResponse response = client.execute(request);
    isSuccess(response, "uploadPart " + partNumber);

    String eTag = response.getFirstHeader("ETag").getValue();
    return eTag;
  }

  /**
   * 모든 파트를 업로드한 후 최종 완료 요청을 전송
   *
   * @param client   HttpClient 인스턴스
   * @param bucket   버킷 이름
   * @param key      오브젝트 키
   * @param uploadId 업로드 식별자
   * @param parts    업로드한 각 파트의 번호와 ETag 목록
   * @return NCP Object Storage로부터 받은 응답 객체 {@link HttpResponse}
   * @throws IllegalStateException 응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception             요청 실패 시 예외 발생
   */
  public HttpResponse completeUpload(HttpClient client, String bucket, String key, String uploadId,
      List<PartETag> parts) throws Exception {
    String url = String.format("%s/%s/%s?uploadId=%s", ENDPOINT, bucket, key, uploadId);
    HttpPost request = new HttpPost(url);
    request.addHeader("Host", request.getURI().getHost());

    StringBuilder body = new StringBuilder();
    body.append("<CompleteMultipartUpload>");
    for (PartETag part : parts) {
      body.append("<Part>")
          .append("<PartNumber>").append(part.getPartNumber()).append("</PartNumber>")
          .append("<ETag>").append(part.getETag()).append("</ETag>")
          .append("</Part>");
    }
    body.append("</CompleteMultipartUpload>");

    request.setEntity(new InputStreamEntity(
        new java.io.ByteArrayInputStream(body.toString().getBytes()), body.length()));
    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);
    HttpResponse response = client.execute(request);
    isSuccess(response, "completeUpload");
    return response;
  }

  /**
   * 아직 완료되지 않은 멀티파트 업로드 목록 조회함
   *
   * @param bucket 버킷 이름
   * @return (objectKey, uploadId) 쌍 리스트로 리턴함
   * @throws IllegalStateException 응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception             실패 시 예외 발생함
   */
  public List<Pair<String, String>> listIncompleteUploads(String bucket) throws Exception {
    List<Pair<String, String>> list = new ArrayList<>();

    String url = ENDPOINT + "/" + bucket + "?uploads";
    HttpGet request = new HttpGet(url);
    request.addHeader("Host", request.getURI().getHost());
    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    HttpClient client = HttpClientBuilder.create().build();
    HttpResponse response = client.execute(request);
    isSuccess(response, "listIncompleteUploads");

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(response.getEntity().getContent());
    NodeList uploads = doc.getElementsByTagName("Upload");

    for (int i = 0; i < uploads.getLength(); i++) {
      Element upload = (Element) uploads.item(i);
      String key = upload.getElementsByTagName("Key").item(0).getTextContent();
      String uploadId = upload.getElementsByTagName("UploadId").item(0).getTextContent();
      String initiated = upload.getElementsByTagName("Initiated").item(0).getTextContent();
      list.add(Pair.of(key, uploadId));

    }
    return list;
  }

  /**
   * 특정 업로드 세션을 중단하고 파트 삭제함
   *
   * @param bucket    버킷 이름
   * @param objectKey 오브젝트 키
   * @param uploadId  업로드 식별자
   * @throws IllegalStateException 업로드 중단 요청이 실패한 경우 (예: 4xx, 5xx 응답)
   * @throws Exception             실패 시 예외 발생함
   */
  public void abortMultipartUpload(String bucket, String objectKey, String uploadId)
      throws Exception {
    String url = String.format("%s/%s/%s?uploadId=%s", ENDPOINT, bucket, objectKey, uploadId);
    HttpDelete request = new HttpDelete(url);
    request.addHeader("Host", request.getURI().getHost());
    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    HttpClient client = HttpClientBuilder.create().build();
    HttpResponse response = client.execute(request);
    isSuccess(response, "abortMultipartUpload");

  }

}

package bjs.zangbu.ncp.service;

import static bjs.zangbu.ncp.auth.HeaderCreation.authorization;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ACCESS_KEY;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ENDPOINT;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.REGION_NAME;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.SECRET_KEY;
import static bjs.zangbu.ncp.util.UploadUtil.isPdfFile;
import static bjs.zangbu.ncp.util.UploadUtil.isSuccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

/**
 * 로컬 파일 업로드, 다운로드, 목록 조회를 수행하는 구현체
 */
@Log4j2
@Service
public class PathUploaderServiceImpl implements PathUploaderService {

  /**
   * 로컬 PDF 파일을 Object Storage에 업로드하고, public URL을 반환
   *
   * @param bucketName    업로드 대상 버킷 이름
   * @param objectName    업로드될 오브젝트 키 (예: "documents/sample.pdf")
   * @param localFilePath 업로드할 로컬 파일 경로
   * @return 업로드된 PDF 파일의 Object Storage 공개 URL
   * @throws IllegalArgumentException PDF가 아닌 파일일 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception                업로드 과정 중 오류 발생 시
   */
  public String putObject(String bucketName, String objectName, String localFilePath)
      throws Exception {
    File file = new File(localFilePath);

    // PDF 파일 여부 확인
    if (!isPdfFile(file)) {
      throw new IllegalArgumentException("PDF 파일만 업로드할 수 있습니다.");
    }

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPut request = new HttpPut(ENDPOINT + "/" + bucketName + "/" + objectName);
    request.addHeader("Host", request.getURI().getHost());
    request.setEntity(new FileEntity(new File(localFilePath)));
    request.setEntity(new FileEntity(file));

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    HttpResponse response = httpClient.execute(request);
    isSuccess(response, "putObject");

    return ENDPOINT + "/" + bucketName + "/" + objectName;

  }

  /**
   * Object Storage 에 있는 오브젝트 다운로드해서 로컬에 저장함.
   * <ul>
   *   <li>HTTP 응답이 성공인지 확인</li>
   *   <li>로컬로 저장된 파일이 PDF 형식이 아닌 경우 파일을 삭제하고 예외를 발생</li>
   * </ul>
   *
   * @param bucketName    다운로드 대상 버킷 이름
   * @param objectName    다운로드할 오브젝트 키
   * @param localFilePath 저장될 로컬 파일 경로
   * @throws IllegalArgumentException 다운로드된 파일이 PDF가 아닐 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception                다운로드 과정 중 오류 발생 시
   */
  public void getObject(String bucketName, String objectName, String localFilePath)
      throws Exception {
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(ENDPOINT + "/" + bucketName + "/" + objectName);
    request.addHeader("Host", request.getURI().getHost());

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    HttpResponse response = httpClient.execute(request);
    isSuccess(response, "getObject");

    InputStream is = response.getEntity().getContent();
    File targetFile = new File(localFilePath);
    OutputStream os = new FileOutputStream(targetFile);

    byte[] buffer = new byte[8 * 1024];
    int bytesRead;
    while ((bytesRead = is.read(buffer)) != -1) {
      os.write(buffer, 0, bytesRead);
    }

    if (!isPdfFile(targetFile)) {
      boolean deleted = targetFile.delete();
      if (!deleted) {
        log.warn("PDF 형식이 아닌데도 삭제 실패: {}", localFilePath);
      }
      throw new IllegalArgumentException("다운로드된 파일이 PDF 형식이 아닙니다.");
    }
    is.close();
    os.close();
  }

  /**
   * Object Storage의 버킷 내 오브젝트 목록을 조회
   * <ul>
   *   <li>prefix, delimiter 등의 쿼리스트링을 조합해 조건부 조회가 가능</li>
   * </ul>
   *
   * @param bucketName  조회 대상 버킷 이름
   * @param queryString 조회 조건 (예: "prefix=documents/&delimiter=/")
   * @return 조회 결과 XML 문자열
   * @throws IllegalStateException 응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception             목록 조회 중 오류 발생 시
   */
  public String listObjects(String bucketName, String queryString) throws Exception {
    HttpClient httpClient = HttpClientBuilder.create().build();
    URI uri = new URI(ENDPOINT + "/" + bucketName + "?" + queryString);
    HttpGet request = new HttpGet(uri);
    request.addHeader("Host", request.getURI().getHost());

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    HttpResponse response = httpClient.execute(request);
    isSuccess(response, "listObjects");

    int i;
    InputStream is = response.getEntity().getContent();
    StringBuffer buffer = new StringBuffer();
    byte[] b = new byte[4096];
    while ((i = is.read(b)) != -1) {
      buffer.append(new String(b, 0, i));
    }
    return buffer.toString();
  }

}

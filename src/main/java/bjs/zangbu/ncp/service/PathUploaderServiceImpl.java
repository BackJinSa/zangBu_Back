package bjs.zangbu.ncp.service;

import static bjs.zangbu.ncp.auth.HeaderCreation.authorization;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ACCESS_KEY;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ENDPOINT;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.REGION_NAME;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.SECRET_KEY;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * 로컬 파일 업로드, 조회, 목록 조회
 */
public class PathUploaderServiceImpl implements PathUploaderService {

  /**
   * 로컬 파일을 Object Storage 에 업로드함.
   *
   * @param bucketName    버킷 이름
   * @param objectName    오브젝트 키
   * @param localFilePath 로컬 파일 경로
   */
  public void putObject(String bucketName, String objectName, String localFilePath)
      throws Exception {
    HttpClient httpClient = HttpClientBuilder.create().build();

    HttpPut request = new HttpPut(ENDPOINT + "/" + bucketName + "/" + objectName);
    request.addHeader("Host", request.getURI().getHost());
    request.setEntity(new FileEntity(new File(localFilePath)));

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    httpClient.execute(request);
  }

  /**
   * Object Storage 에 있는 오브젝트 다운로드해서 로컬에 저장함.
   *
   * @param bucketName    버킷 이름
   * @param objectName    오브젝트 키
   * @param localFilePath 저장할 로컬 경로
   */
  public void getObject(String bucketName, String objectName, String localFilePath)
      throws Exception {
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(ENDPOINT + "/" + bucketName + "/" + objectName);
    request.addHeader("Host", request.getURI().getHost());

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    HttpResponse response = httpClient.execute(request);
    System.out.println("Response : " + response.getStatusLine());

    InputStream is = response.getEntity().getContent();
    File targetFile = new File(localFilePath);
    OutputStream os = new FileOutputStream(targetFile);

    byte[] buffer = new byte[8 * 1024];
    int bytesRead;
    while ((bytesRead = is.read(buffer)) != -1) {
      os.write(buffer, 0, bytesRead);
    }

    is.close();
    os.close();
  }

  /**
   * Object Storage 버킷 내 오브젝트 목록 조회함.
   *
   * @param bucketName  버킷 이름
   * @param queryString 쿼리 스트링 (prefix, delimiter 등)
   */
  public void listObjects(String bucketName, String queryString) throws Exception {
    HttpClient httpClient = HttpClientBuilder.create().build();
    URI uri = new URI(ENDPOINT + "/" + bucketName + "?" + queryString);
    HttpGet request = new HttpGet(uri);
    request.addHeader("Host", request.getURI().getHost());

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    HttpResponse response = httpClient.execute(request);
    System.out.println("> Response : " + response.getStatusLine());
    int i;
    InputStream is = response.getEntity().getContent();
    StringBuffer buffer = new StringBuffer();
    byte[] b = new byte[4096];
    while ((i = is.read(b)) != -1) {
      buffer.append(new String(b, 0, i));
    }
  }

}

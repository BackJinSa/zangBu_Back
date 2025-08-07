package bjs.zangbu.ncp.service;

import static bjs.zangbu.ncp.auth.HeaderCreation.authorization;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ACCESS_KEY;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ENDPOINT;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.REGION_NAME;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.SECRET_KEY;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

@Service
public class BinaryUploaderServiceImpl implements BinaryUploaderService {

  /**
   * PDF 바이너리를 Object Storage 에 업로드함.
   *
   * @param bucketName 버킷 이름
   * @param objectName 저장될 객체 이름 (예: "example.pdf")
   * @param pdfBytes   PDF 바이너리 데이터
   */
  public void putPdfObject(String bucketName, String objectName, byte[] pdfBytes) throws Exception {
    HttpClient httpClient = HttpClientBuilder.create().build();

    HttpPut request = new HttpPut(ENDPOINT + "/" + bucketName + "/" + objectName);
    request.addHeader("Host", request.getURI().getHost());
    request.addHeader("Content-Type", "application/pdf"); // MIME 타입 지정

    ByteArrayEntity entity = new ByteArrayEntity(pdfBytes);
    request.setEntity(entity);

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);
    HttpResponse response = httpClient.execute(request);

    System.out.println("PDF Upload Response: " + response.getStatusLine());
  }
}

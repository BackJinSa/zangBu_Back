package bjs.zangbu.ncp.service;

import static bjs.zangbu.ncp.auth.HeaderCreation.authorization;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ACCESS_KEY;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ENDPOINT;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.REGION_NAME;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.SECRET_KEY;
import static bjs.zangbu.ncp.util.UploadUtil.isSuccess;

import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class HtmlUploaderServiceImpl implements HtmlUploaderService {

  /**
   * HTML 문자열을 Object Storage에 업로드
   *
   * @param bucketName 업로드할 버킷 이름
   * @param objectName 업로드될 오브젝트 키 (예: "pages/sample.html")
   * @param htmlString 업로드할 HTML 문자열
   * @return 업로드된 HTML 파일의 공개 URL
   * @throws Exception 업로드 과정에서 오류 발생 시
   */
  public String putHtmlObject(String bucketName, String objectName, String htmlString)
      throws Exception {

    byte[] htmlBytes = htmlString.getBytes(StandardCharsets.UTF_8);

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPut request = new HttpPut(ENDPOINT + "/" + bucketName + "/" + objectName);
    request.addHeader("Host", request.getURI().getHost());
    request.addHeader("Content-Type", "text/html; charset=UTF-8");

    request.setEntity(new ByteArrayEntity(htmlBytes));

    // 인증 헤더 추가
    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);

    HttpResponse response = httpClient.execute(request);
    isSuccess(response, "putHtmlObject");

    return ENDPOINT + "/" + bucketName + "/" + objectName;
  }
}

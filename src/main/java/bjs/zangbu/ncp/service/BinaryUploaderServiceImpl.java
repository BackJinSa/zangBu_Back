package bjs.zangbu.ncp.service;

import static bjs.zangbu.ncp.auth.HeaderCreation.authorization;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ACCESS_KEY;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.ENDPOINT;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.REGION_NAME;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.SECRET_KEY;
import static bjs.zangbu.ncp.util.UploadUtil.isPdfBytes;
import static bjs.zangbu.ncp.util.UploadUtil.isSuccess;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

/**
 * 주어진 PDF 바이너리 데이터를 NCP Object Storage에 업로드하는 서비스 구현체입니다.
 */
@Service
@Log4j2
public class BinaryUploaderServiceImpl implements BinaryUploaderService {

  /**
   * PDF 바이너리 데이터를 NCP Object Storage에 업로드
   * <ul>
   *   <li>업로드 전 PDF 파일 시그니처가 유효한지 검사</li>
   *   <li>업로드 성공 시 공개 접근 가능한 파일 URL을 반환</li>
   * </ul>
   *
   * @param bucketName 업로드 대상 버킷 이름 (예: "bjs-bucket")
   * @param objectName 업로드될 오브젝트 키 (예: "folder/sample.pdf")
   * @param pdfBytes   업로드할 PDF 파일의 바이너리 데이터
   * @return 업로드된 PDF 파일의 Object Storage 공개 URL
   * @throws IllegalArgumentException PDF 시그니처가 유효하지 않을 경우
   * @throws IllegalStateException    응답이 실패(2xx가 아닌 경우)했을 때 발생
   * @throws Exception                업로드 요청 또는 응답 처리 중 오류가 발생할 경우
   */
  public String putPdfObject(String bucketName, String objectName, byte[] pdfBytes)
      throws Exception {
    // pdf 시그니처 확인
    if (!isPdfBytes(pdfBytes)) {
      throw new IllegalArgumentException("유효한 PDF 파일이 아닙니다.");
    }

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPut request = new HttpPut(ENDPOINT + "/" + bucketName + "/" + objectName);
    request.addHeader("Host", request.getURI().getHost());
    request.addHeader("Content-Type", "application/pdf"); // MIME 타입 지정

    ByteArrayEntity entity = new ByteArrayEntity(pdfBytes);
    request.setEntity(entity);

    authorization(request, REGION_NAME, ACCESS_KEY, SECRET_KEY);
    HttpResponse response = httpClient.execute(request);

    isSuccess(response, "putPdfObject");

    return ENDPOINT + "/" + bucketName + "/" + objectName;

  }
}

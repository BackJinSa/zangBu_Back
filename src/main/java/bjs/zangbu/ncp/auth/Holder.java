package bjs.zangbu.ncp.auth;

import javax.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * NCP 관련 설정값을 읽어 static 변수로 할당하는 컴포넌트
 */
@Component
@NoArgsConstructor
public class Holder {

  @Value("${ncp.hmacAlgorithm}")
  private String hmacAlgorithm;

  @Value("${ncp.hashAlgorithm}")
  private String hashAlgorithm;

  @Value("${ncp.awsAlgorithm}")
  private String awsAlgorithm;

  @Value("${ncp.regionName}")
  private String regionName;

  @Value("${ncp.endpoint}")
  private String endpoint;

  @Value("${ncp.accessKey}")
  private String accessKey;

  @Value("${ncp.secretKey}")
  private String secretKey;

  @Value("${ncp.bucketName}")
  private String bucketName;

  /**
   * 필드 주입 후 static 변수에 복사함 <br> - 의존성 없는 클래스에서도 접근 가능하게 하기 위함
   */
  @PostConstruct
  public void initStaticFields() {
    HeaderCreationHolder.HMAC_ALGORITHM = hmacAlgorithm;
    HeaderCreationHolder.HASH_ALGORITHM = hashAlgorithm;
    HeaderCreationHolder.AWS_ALGORITHM = awsAlgorithm;
    HeaderCreationHolder.REGION_NAME = regionName;
    HeaderCreationHolder.ENDPOINT = endpoint;
    HeaderCreationHolder.ACCESS_KEY = accessKey;
    HeaderCreationHolder.SECRET_KEY = secretKey;
    HeaderCreationHolder.BUCKET_NAME = bucketName;
  }

  /**
   * 헤더 생성 시 사용할 static 상수들 모아둔 클래스
   */
  public static class HeaderCreationHolder {

    static String HMAC_ALGORITHM;
    static String HASH_ALGORITHM;
    static String AWS_ALGORITHM;
    public static String REGION_NAME;
    public static String ENDPOINT;
    public static String ACCESS_KEY;
    public static String SECRET_KEY;
    public static String BUCKET_NAME;
  }
}
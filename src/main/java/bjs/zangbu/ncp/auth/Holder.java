package bjs.zangbu.ncp.auth;

import javax.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * NCP 관련 설정값을 주입받아 static 필드로 전달하는 컴포넌트
 *
 * <p>스프링 컨테이너에 의해 주입된 설정 값을 정적 필드에 할당함으로써,
 * DI를 지원하지 않는 클래스에서도 설정 값을 사용할 수 있도록 함
 *
 * @see Holder.HeaderCreationHolder
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
   * 주입된 인스턴스 필드 값을 정적(static) 필드로 복사
   *
   * <p>정적 필드를 사용하면 의존성 주입 없이도 설정 값을 참조할 수 있음
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
   * Object Storage 헤더 생성 등에 사용되는 NCP 설정 상수들을 담는 static 클래스
   *
   * <p>이 클래스의 필드는 {@link Holder}가 초기화 시 자동으로 채워지며,
   * 정적 접근이 필요한 곳에서 직접 참조 가능
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

package bjs.zangbu.ncp.auth;

import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.AWS_ALGORITHM;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.HASH_ALGORITHM;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.HMAC_ALGORITHM;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.stereotype.Component;


/**
 * - NCP Object Storage 연동을 위한 AWS Signature V4 기반 인증 헤더 생성 및 파일 처리 클래스 <br> - 사용 전
 * Holder.HeaderCreationHolder 에서 설정값 static 필드로 주입 필요
 */
@RequiredArgsConstructor
@Component
public class HeaderCreation {

  /**
   * 문자 인코딩 방식. UTF-8 고정임.
   */
  private static final String CHARSET_NAME = "UTF-8";

  /**
   * 페이로드 해시 생략 시 사용되는 고정값.
   */
  private static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";

  /**
   * 서비스 이름 (s3 고정)
   */
  private static final String SERVICE_NAME = "s3";

  /**
   * AWS 요청 타입 고정값
   */
  private static final String REQUEST_TYPE = "aws4_request";

  /**
   * 날짜 포맷터 (yyyyMMdd)
   */
  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");

  /**
   * 타임스탬프 포맷터 (yyyyMMdd'T'HHmmss'Z')
   */
  private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat(
      "yyyyMMdd'T'HHmmss'Z'");

  /**
   * HMAC 서명 계산함. HmacSHA256 알고리즘 사용함.
   *
   * @param stringData 서명 대상 문자열
   * @param key        서명용 키
   * @return 서명된 바이트 배열
   */
  private static byte[] sign(String stringData, byte[] key)
      throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
    byte[] data = stringData.getBytes(CHARSET_NAME);
    Mac e = Mac.getInstance(HMAC_ALGORITHM);
    e.init(new SecretKeySpec(key, HMAC_ALGORITHM));
    return e.doFinal(data);
  }

  /**
   * 문자열 해시값 계산함. SHA-256 사용함.
   *
   * @param text 해시 대상 문자열
   * @return 해시값 Hex 문자열
   */
  private static String hash(String text)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest e = MessageDigest.getInstance(HASH_ALGORITHM);
    e.update(text.getBytes(CHARSET_NAME));
    return Hex.encodeHexString(e.digest());
  }

  /**
   * Query 파라미터 표준화함. 알파벳순으로 정렬하고 URL 인코딩함.
   *
   * @param queryString URI query 부분
   * @return 표준화된 query 문자열
   */
  private static String getStandardizedQueryParameters(String queryString)
      throws UnsupportedEncodingException {
    TreeMap<String, String> sortedQueryParameters = new TreeMap<>();
    // sort by key name
    if (queryString != null && !queryString.isEmpty()) {
      String[] queryStringTokens = queryString.split("&");
      for (String field : queryStringTokens) {
        String[] fieldTokens = field.split("=");
        if (fieldTokens.length > 0) {
          if (fieldTokens.length > 1) {
            sortedQueryParameters.put(fieldTokens[0], fieldTokens[1]);
          } else {
            sortedQueryParameters.put(fieldTokens[0], "");
          }
        }
      }
    }

    StringBuilder standardizedQueryParametersBuilder = new StringBuilder();
    int count = 0;
    for (String key : sortedQueryParameters.keySet()) {
      if (count > 0) {
        standardizedQueryParametersBuilder.append("&");
      }
      standardizedQueryParametersBuilder.append(key).append("=");

      if (sortedQueryParameters.get(key) != null && !sortedQueryParameters.get(key).isEmpty()) {
        standardizedQueryParametersBuilder.append(
            URLEncoder.encode(sortedQueryParameters.get(key), CHARSET_NAME));
      }

      count++;
    }
    return standardizedQueryParametersBuilder.toString();
  }

  /**
   * HTTP Header 배열을 정렬된 Map 으로 반환함. header 이름 기준으로 정렬됨.
   *
   * @param headers HTTP Header 배열
   * @return key 이름 기준으로 정렬된 header Map
   */
  private static TreeMap<String, String> getSortedHeaders(Header[] headers) {
    TreeMap<String, String> sortedHeaders = new TreeMap<>();
    // sort by header name
    for (Header header : headers) {
      String headerName = header.getName().toLowerCase();
      sortedHeaders.put(headerName, header.getValue());
    }

    return sortedHeaders;
  }

  /**
   * 서명에 포함될 Header 이름 리스트 추출함.
   *
   * @param sortedHeaders 정렬된 Header Map
   * @return 세미콜론으로 구분된 header 이름 목록
   */
  private static String getSignedHeaders(TreeMap<String, String> sortedHeaders) {
    StringBuilder signedHeadersBuilder = new StringBuilder();
    for (String headerName : sortedHeaders.keySet()) {
      signedHeadersBuilder.append(headerName.toLowerCase()).append(";");
    }
    String s = signedHeadersBuilder.toString();
    if (s.endsWith(";")) {
      s = s.substring(0, s.length() - 1);
    }
    return s;
  }

  /**
   * Header 표준화 포맷 구성함. 각 header는 "이름:값" 형식으로 구성됨.
   *
   * @param sortedHeaders 정렬된 Header Map
   * @return 표준화된 header 문자열
   */
  private static String getStandardizedHeaders(TreeMap<String, String> sortedHeaders) {
    StringBuilder standardizedHeadersBuilder = new StringBuilder();
    for (String headerName : sortedHeaders.keySet()) {
      standardizedHeadersBuilder.append(headerName.toLowerCase()).append(":")
          .append(sortedHeaders.get(headerName)).append("\n");
    }

    return standardizedHeadersBuilder.toString();
  }

  /**
   * Canonical Request 생성함. Signature 계산에 사용됨.
   *
   * @param request                     HttpUriRequest 객체
   * @param standardizedQueryParameters 표준화된 query string
   * @param standardizedHeaders         표준화된 header string
   * @param signedHeaders               signedHeaders string
   * @return canonical request 문자열
   */
  private static String getCanonicalRequest(HttpUriRequest request,
      String standardizedQueryParameters, String standardizedHeaders, String signedHeaders) {
    StringBuilder canonicalRequestBuilder = new StringBuilder().append(request.getMethod())
        .append("\n")
        .append(request.getURI().getPath()).append("\n")
        .append(standardizedQueryParameters).append("\n")
        .append(standardizedHeaders).append("\n")
        .append(signedHeaders).append("\n")
        .append(UNSIGNED_PAYLOAD);

    return canonicalRequestBuilder.toString();
  }

  /**
   * AWS Signature Scope 구성함. 날짜/리전/서비스/request 고정값으로 구성됨.
   *
   * @param datestamp  yyyyMMdd
   * @param regionName 리전 이름
   * @return Scope 문자열
   */
  private static String getScope(String datestamp, String regionName) {
    StringBuilder scopeBuilder = new StringBuilder().append(datestamp).append("/")
        .append(regionName).append("/")
        .append(SERVICE_NAME).append("/")
        .append(REQUEST_TYPE);
    return scopeBuilder.toString();
  }

  /**
   * 서명 대상 문자열(StringToSign) 생성함.
   *
   * @param timestamp        요청 시간 (yyyyMMdd'T'HHmmss'Z')
   * @param scope            scope 문자열
   * @param canonicalRequest canonical request 문자열
   * @return StringToSign 문자열
   */
  private static String getStringToSign(String timestamp, String scope, String canonicalRequest)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    StringBuilder stringToSignBuilder = new StringBuilder(AWS_ALGORITHM)
        .append("\n")
        .append(timestamp).append("\n")
        .append(scope).append("\n")
        .append(hash(canonicalRequest));

    return stringToSignBuilder.toString();
  }

  /**
   * AWS Signature Version 4 서명 생성함.
   *
   * @param secretKey    비밀 키
   * @param datestamp    날짜 스탬프
   * @param regionName   리전 이름
   * @param stringToSign 서명 대상 문자열
   * @return 서명값 (hex 문자열)
   */
  private static String getSignature(String secretKey, String datestamp, String regionName,
      String stringToSign)
      throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
    byte[] kSecret = ("AWS4" + secretKey).getBytes(CHARSET_NAME);
    byte[] kDate = sign(datestamp, kSecret);
    byte[] kRegion = sign(regionName, kDate);
    byte[] kService = sign(SERVICE_NAME, kRegion);
    byte[] signingKey = sign(REQUEST_TYPE, kService);

    return Hex.encodeHexString(sign(stringToSign, signingKey));
  }

  /**
   * Authorization 헤더 생성함. Signature와 함께 인증에 사용됨.
   *
   * @param accessKey     액세스 키
   * @param scope         scope 문자열
   * @param signedHeaders 서명 대상 header 목록
   * @param signature     서명값
   * @return Authorization 헤더 문자열
   */
  private static String getAuthorization(String accessKey, String scope, String signedHeaders,
      String signature) {
    String signingCredentials = accessKey + "/" + scope;
    String credential = "Credential=" + signingCredentials;
    String signerHeaders = "SignedHeaders=" + signedHeaders;
    String signatureHeader = "Signature=" + signature;

    StringBuilder authHeaderBuilder = new StringBuilder().append(AWS_ALGORITHM).append(" ")
        .append(credential).append(", ")
        .append(signerHeaders).append(", ")
        .append(signatureHeader);

    return authHeaderBuilder.toString();
  }

  /**
   * 요청 객체에 인증 헤더 추가함.
   *
   * @param request    HTTP 요청 객체
   * @param regionName 리전
   * @param accessKey  액세스 키
   * @param secretKey  시크릿 키
   */
  public static void authorization(HttpUriRequest request, String regionName, String accessKey,
      String secretKey) throws Exception {
    Date now = new Date();
    DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    String datestamp = DATE_FORMATTER.format(now);
    String timestamp = TIME_FORMATTER.format(now);

    request.addHeader("X-Amz-Date", timestamp);

    request.addHeader("X-Amz-Content-Sha256", UNSIGNED_PAYLOAD);

    String standardizedQueryParameters = getStandardizedQueryParameters(
        request.getURI().getQuery());

    TreeMap<String, String> sortedHeaders = getSortedHeaders(request.getAllHeaders());
    String signedHeaders = getSignedHeaders(sortedHeaders);
    String standardizedHeaders = getStandardizedHeaders(sortedHeaders);

    String canonicalRequest = getCanonicalRequest(request, standardizedQueryParameters,
        standardizedHeaders, signedHeaders);
    System.out.println("> canonicalRequest :");
    System.out.println(canonicalRequest);

    String scope = getScope(datestamp, regionName);

    String stringToSign = getStringToSign(timestamp, scope, canonicalRequest);
    System.out.println("> stringToSign :");
    System.out.println(stringToSign);

    String signature = getSignature(secretKey, datestamp, regionName, stringToSign);

    String authorization = getAuthorization(accessKey, scope, signedHeaders, signature);
    request.addHeader("Authorization", authorization);
  }
}

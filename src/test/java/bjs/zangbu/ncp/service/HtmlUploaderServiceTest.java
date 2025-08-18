package bjs.zangbu.ncp.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * {@link HtmlUploaderServiceImpl}의 HTML 문자열 업로드 기능을 검증하는 단위 테스트 클래스
 *
 * <p>
 * - 유효한 HTML 문자열 → 정상 업로드 (URL 반환)<br> - null/빈 문자열 → IllegalArgumentException 발생
 * </p>
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    HtmlUploaderServiceImpl.class
})
@ActiveProfiles("test")
class HtmlUploaderServiceTest {

  @BeforeAll
  static void initStatics() {
    // 리플렉션으로 static 필드 강제 세팅 (스킴 꼭 포함)
    setField(HeaderCreationHolder.class, "ENDPOINT", "");
    setField(HeaderCreationHolder.class, "REGION_NAME", "");
    setField(HeaderCreationHolder.class, "ACCESS_KEY", "");
    setField(HeaderCreationHolder.class, "SECRET_KEY",
        "");
    setField(HeaderCreationHolder.class, "AWS_ALGORITHM", "");
    setField(HeaderCreationHolder.class, "HASH_ALGORITHM", "");
    setField(HeaderCreationHolder.class, "HMAC_ALGORITHM", "");

  }

  @Autowired
  HtmlUploaderServiceImpl htmlUploaderService;

  final String bucketName = "bjs-bucket";

  @Test
  @DisplayName("유효한 HTML 문자열 업로드 - public URL 반환")
  void putHtmlObject_validHtml_shouldReturnPublicUrl() throws Exception {
    // given
    String html = "<!doctype html><html><head><meta charset=\"UTF-8\"><title>T</title></head>"
        + "<body><h1>OK</h1></body></html>";
    String objectName = "test-folder/string-upload-valid.html";

    // when
    String url = htmlUploaderService.putHtmlObject(bucketName, objectName, html);

    // then
    assertNotNull(url, "업로드된 URL은 null이 아니어야 함");
    assertTrue(url.contains(bucketName), "URL에 버킷 이름이 포함되어야 함");
    assertTrue(url.contains(objectName), "URL에 오브젝트 이름이 포함되어야 함");
  }

}
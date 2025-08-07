package bjs.zangbu.ncp.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bjs.zangbu.global.config.RootConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@TestPropertySource("classpath:/application.yml")
@ActiveProfiles("test")
class HolderTest {

  @Test
  void config_주입_확인() {
    String endpoint = Holder.HeaderCreationHolder.ENDPOINT;
    String accessKey = Holder.HeaderCreationHolder.ACCESS_KEY;
    String secretKey = Holder.HeaderCreationHolder.SECRET_KEY;

    log.info("▶ ENDPOINT = {}", endpoint);
    log.info("▶ ACCESS_KEY = {}", accessKey);
    log.info("▶ SECRET_KEY = {}", secretKey);

    assertNotNull(endpoint, "ENDPOINT는 null이 아니어야 함");
    assertTrue(endpoint.contains("http"), "ENDPOINT는 http 포함해야 함");
    assertNotNull(accessKey, "ACCESS_KEY는 null이 아니어야 함");
    assertFalse(accessKey.isEmpty(), "ACCESS_KEY는 비어 있으면 안 됨");
    assertNotNull(secretKey, "SECRET_KEY는 null이 아니어야 함");
    assertFalse(secretKey.isEmpty(), "SECRET_KEY는 비어 있으면 안 됨");
  }
}

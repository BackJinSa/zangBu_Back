package bjs.zangbu.ncp.service;

import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.ncp.auth.Holder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
class BinaryUploaderServiceTest {

  @Autowired
  private BinaryUploaderService uploaderService;

  @BeforeAll
  static void initStaticNcpConfig() throws IOException {
    Properties props = new Properties();
    try (InputStream input = Files.newInputStream(
        Paths.get("src/test/resources/application.yml"))) {
      props.load(input);
    }

    // static 필드 직접 할당
    Holder.HeaderCreationHolder.ENDPOINT = props.getProperty("ncp.endpoint");
    Holder.HeaderCreationHolder.ACCESS_KEY = props.getProperty("ncp.accessKey");
    Holder.HeaderCreationHolder.SECRET_KEY = props.getProperty("ncp.secretKey");
    Holder.HeaderCreationHolder.REGION_NAME = props.getProperty("ncp.regionName");
  }

  @Test
  void putPdfObject() throws Exception {

    byte[] pdfBytes = Files.readAllBytes(Paths.get("src/test/resources/test.pdf"));

    String bucketName = "bjs-bucket"; // 실제 버킷 이름으로 변경
    String objectName = "test-folder/uploaded-test.pdf"; // 저장될 경로와 파일명

    uploaderService.putPdfObject(bucketName, objectName, pdfBytes);

    log.info("✔ 업로드 완료: {}", objectName);
  }
}
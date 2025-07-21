package bjs.zangbu.global.encryption;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class RSAEncryptionTest {

    @Test
    void RSAEncryptionTest() throws Exception {
        Properties props = new Properties();
        props.load(Files.newInputStream(Paths.get("src/main/resources/application.yml")));
        String base64PublicKey = props.getProperty("public_key");
        assertNotNull(base64PublicKey, "public_key must be set in application.properties");

        PublicKey publicKey = RSAEncryption.getPublicKey(base64PublicKey);
        assertNotNull(publicKey);

        String plainText = "1234";
        byte[] encrypted = RSAEncryption.encryptText(plainText, publicKey);

        assertNotNull(encrypted);
        assertTrue(encrypted.length > 0);
        System.out.println("Encrypted : " + Base64.getEncoder().encodeToString(encrypted));
    }

}
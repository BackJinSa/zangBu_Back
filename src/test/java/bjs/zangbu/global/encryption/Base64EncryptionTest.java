package bjs.zangbu.global.encryption;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class Base64EncryptionTest {

    @Test
    void codefEncryption() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("application.yml"));

        context.registerBean(PropertySourcesPlaceholderConfigurer.class, () -> configurer);
        context.scan("bjs.zangbu.global.encryption");
        context.refresh();

        Base64Encryption encryption = context.getBean(Base64Encryption.class);

        String encoded = encryption.codefEncryption();
        assertNotNull(encoded);
        System.out.println("Encoded Token: " + encoded);
        context.close();
    }
}
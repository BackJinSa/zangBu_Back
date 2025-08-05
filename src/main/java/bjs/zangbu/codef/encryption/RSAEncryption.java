package bjs.zangbu.codef.encryption;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RSAEncryption {
    private static final String ALGORITHM = "RSA";

    @Value("${codef.public_key}")
    private String publicKeyString;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        // 줄바꿈 복원 및 헤더 제거 todo : 없어도 될거 같긴 함
        String cleanKey = publicKeyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\\\n", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        this.publicKey = kf.generatePublic(spec);
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}

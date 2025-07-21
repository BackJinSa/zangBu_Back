package bjs.zangbu.global.encryption;

import io.codef.api.EasyCodef;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CodefEncryption {
    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${public_key}")
    private String publicKey;
    private EasyCodef codef;

    public CodefEncryption() {
        codef = new EasyCodef(); // 여기선 EasyCodef 인스턴스만 생성 초기화는 PostConstruct에서
    }

    // Spring 컨테이너가 의존성 주입을 끝낸 후 실행됨
    @PostConstruct
    private void initializeCodef() {
        setupClientInfo();   // 주입된 값 사용 가능
        setupPublicKey();    // RSA 공개키 설정
    }

    public void setupClientInfo() {
        codef.setClientInfoForDemo(clientId, clientSecret);
    }

    public void setupPublicKey() {
        codef.setPublicKey(publicKey);
    }

    public EasyCodef getCodefInstance() { // 나중에 codef API 사용할때 이것만 가져오면 됨
        return codef;
    }
}
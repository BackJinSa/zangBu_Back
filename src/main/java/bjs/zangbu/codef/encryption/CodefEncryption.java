package bjs.zangbu.codef.encryption;

import io.codef.api.EasyCodef;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CODEF SDK(EasyCodef) 에 클라이언트 정보·퍼블릭키를 주입하고
 * 암호화 모듈을 반환하는 헬퍼.
 *
 *  ✔️@Value 로 주입 받는 값들은 application‑*.yml 에 반드시 설정
 *  ✔️getCodefInstance() 는 매번 clientInfo / publicKey 세팅 후 돌려준다
 *     → 싱글턴이라 사용 직전에 setXXX 를 호출해도 thread‑safe
 */
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

    public void setupClientInfo() {
        codef.setClientInfoForDemo(clientId, clientSecret);
    }

    public void setupPublicKey() {
        codef.setPublicKey(publicKey);
    }

    public EasyCodef getCodefInstance() { // 나중에 codef API 사용할때 이것만 가져오면 됨
        setupClientInfo();
        setupPublicKey();
        return codef;
    }
}
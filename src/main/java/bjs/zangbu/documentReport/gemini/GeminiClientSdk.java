package bjs.zangbu.documentReport.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class GeminiClientSdk {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-pro}")
    private String model;

    private volatile Client client;

    private Client c() {
        if (client == null) {
            client = Client.builder().apiKey(apiKey).build();
        }
        return client;
    }

    /** txt 프롬프트 + 합쳐진 JSON을 하나의 입력으로 붙여 전송 */
    public String generate(String promptText, String combinedJson) throws InterruptedException {
        String input = String.join(
                "\n",
                "지시문:", promptText,
                "",
                "입력(JSON):",
                combinedJson
        );

        GenerateContentResponse res =
                c().models.generateContent(
                        model,        // 예: gemini-2.5-pro (또는 gemini-2.5-flash)
                        input,
                        /* options */ null // 필요 시 옵션 객체로 temperature, maxTokens 등 지정
                );
        Thread.sleep(30000);
        System.out.println("res = " + res);
        log.info("res.text() = " + res.text());

        return res.text(); // 프롬프트에서 “JSON만” 반환을 강제했으면 그대로 JSON일 것
    }
}

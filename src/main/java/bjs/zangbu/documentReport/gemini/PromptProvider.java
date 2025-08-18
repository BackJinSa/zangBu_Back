// src/main/java/bjs/zangbu/documentReport/prompt/PromptProvider.java
package bjs.zangbu.documentReport.gemini;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class PromptProvider {

    @Value("${gemini.prompt.path:}")
    private String promptPath;

    private final ResourceLoader resourceLoader;

    /** classpath:/prompts/report-role-v1.txt 내용을 그대로 반환 */
    public String loadOrThrow() {
        if (promptPath == null || promptPath.isBlank()) {
            throw new IllegalStateException("gemini.prompt.path 가 비어 있습니다.");
        }
        try {
            Resource res = resourceLoader.getResource(promptPath);
            try (var in = res.getInputStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("프롬프트 파일 로드 실패: " + promptPath, e);
        }
    }
}

package bjs.zangbu.global.config;

import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;


// yml 파일일 경우 직접 파싱 후 변환 처리를 거쳐주는 클래스이다.
public class YamlPropertyConfig extends DefaultPropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        // 만약에 yml 파일이 없다면 기본 properties 파일을 사용한다.
        if (resource == null) {
            return super.createPropertySource(name, resource);
        }

        Resource res = resource.getResource();
        
        // 확장자가 .yml 또는 .yaml 이면 YAML 파싱 처리
        if (res.getFilename().endsWith(".yml") || res.getFilename().endsWith(".yaml")) {
            // YAML 파일을 Map 형태로 읽어들임
            Map<String, Object> yamlMap = loadYamlIntoMap(res);
            // Properties 객체 생성 (key-value 쌍)
            Properties props = new Properties();
            // 재귀적으로 Map 구조를 Properties 형태 (flat key-value)로 변환
            convertMapToProperties(props, yamlMap, null);
            // 변환된 Properties를 PropertiesPropertySource로 감싸서 반환
            // name이 null이면 파일명으로 설정
            return new PropertiesPropertySource(
                    name != null ? name : res.getFilename(),
                    props
            );
        }
        // yml 파일이 아니면 기본 구현 사용 (예: .properties 파일)
        return super.createPropertySource(name, resource);
    }
    // YAML 파일을 읽어서 Map<String, Object> 형태로 반환하고 snakeYAML 라이브러리를 사용한다.
    private Map<String, Object> loadYamlIntoMap(Resource resource) throws IOException {
        Yaml yaml = new Yaml();
        try (var inputStream = resource.getInputStream()) {
            Object loaded = yaml.load(inputStream);
            // 읽은 객체가 Map이면 캐스팅 후 반환, 아니면 빈 Map 반환
            if (loaded instanceof Map) {
                return (Map<String, Object>) loaded;
            }
            return Map.of();
        }
    }

    // Map을 Properties 형태로 변환하는 재귀 함수
    private void convertMapToProperties(Properties props, Map<String, Object> map, String parentKey) {
        map.forEach((key, value) -> {
            String propKey = (parentKey != null) ? parentKey + "." + key : key;
            if (value instanceof Map) {
                convertMapToProperties(props, (Map<String, Object>) value, propKey);
            } else {
                props.put(propKey, value.toString());
            }
        });
    }
}

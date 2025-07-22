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

public class YamlPropertyConfig extends DefaultPropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null) {
            return super.createPropertySource(name, resource);
        }

        Resource res = resource.getResource();
        if (res.getFilename().endsWith(".yml") || res.getFilename().endsWith(".yaml")) {
            Map<String, Object> yamlMap = loadYamlIntoMap(res);
            Properties props = new Properties();
            convertMapToProperties(props, yamlMap, null);

            return new PropertiesPropertySource(
                    name != null ? name : res.getFilename(),
                    props
            );
        }

        return super.createPropertySource(name, resource);
    }

    private Map<String, Object> loadYamlIntoMap(Resource resource) throws IOException {
        Yaml yaml = new Yaml();
        try (var inputStream = resource.getInputStream()) {
            Object loaded = yaml.load(inputStream);
            if (loaded instanceof Map) {
                return (Map<String, Object>) loaded;
            }
            return Map.of();
        }
    }

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

package bjs.zangbu.codef.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;


public class CodefConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T parseDataToDto(String responseJson, Class<T> clazz) {
        try {
            // JSON 파싱
            Map<String, Object> root = mapper.readValue(responseJson, new TypeReference<>() {});
            Object dataObj = root.get("data");
            if (dataObj == null) throw new IllegalArgumentException("응답에 'data' 필드가 없습니다.");

            // 'data' 부분을 다시 JSON 문자열로 직렬화 후 원하는 DTO 타입으로 파싱
            String dataJson = mapper.writeValueAsString(dataObj);
            return mapper.readValue(dataJson, clazz);

        } catch (Exception e) {
            throw new RuntimeException("CODEF 응답 파싱 실패", e);
        }
    }
}

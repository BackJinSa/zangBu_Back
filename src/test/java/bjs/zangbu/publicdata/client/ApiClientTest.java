package bjs.zangbu.publicdata.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ApiClient apiClient;

    @BeforeEach
    void setUp() {
        apiClient = new ApiClient(restTemplate);
        // 테스트용 서비스 키 설정
        ReflectionTestUtils.setField(apiClient, "serviceKey", "test-service-key");
    }

    @Test
    void testGetForMap_Success() {
        // Given
        String baseUrl = "https://api.odcloud.kr/api/test";
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "success");
        expectedResponse.put("data", "test-data");

        when(restTemplate.getForObject(any(), eq(Map.class))).thenReturn(expectedResponse);

        // When
        Map<String, Object> result = apiClient.getForMap(baseUrl, params);

        // Then
        assertNotNull(result);
        assertEquals("success", result.get("result"));
        assertEquals("test-data", result.get("data"));
    }

    @Test
    void testGetForObject_Success() {
        // Given
        String baseUrl = "https://api.odcloud.kr/api/test";
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");

        TestResponse expectedResponse = new TestResponse("test", 123);
        when(restTemplate.getForObject(any(), eq(TestResponse.class))).thenReturn(expectedResponse);

        // When
        TestResponse result = apiClient.getForObject(baseUrl, params, TestResponse.class);

        // Then
        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals(123, result.getValue());
    }

    @Test
    void testGetForMap_WithEmptyParams() {
        // Given
        String baseUrl = "https://api.odcloud.kr/api/test";
        Map<String, String> params = new HashMap<>();

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("result", "success");

        when(restTemplate.getForObject(any(), eq(Map.class))).thenReturn(expectedResponse);

        // When
        Map<String, Object> result = apiClient.getForMap(baseUrl, params);

        // Then
        assertNotNull(result);
        assertEquals("success", result.get("result"));
    }

    @Test
    void testGetForMap_WithNullParams() {
        // Given
        String baseUrl = "https://api.odcloud.kr/api/test";

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            apiClient.getForMap(baseUrl, null);
        });
    }

    // 테스트용 응답 클래스
    static class TestResponse {
        private String name;
        private int value;

        public TestResponse(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}

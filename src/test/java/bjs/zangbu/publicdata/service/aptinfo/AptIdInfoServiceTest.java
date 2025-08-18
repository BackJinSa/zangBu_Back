package bjs.zangbu.publicdata.service.aptinfo;

import bjs.zangbu.publicdata.client.ApiClient;
import bjs.zangbu.publicdata.dto.aptinfo.AptInfo;
import bjs.zangbu.publicdata.dto.aptinfo.DongInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AptIdInfoServiceTest {

    @Mock
    private ApiClient apiClient;

    private AptIdInfoServiceImpl aptIdInfoService;

    @BeforeEach
    void setUp() {
        aptIdInfoService = new AptIdInfoServiceImpl(apiClient);
    }

    @Test
    void testFetchAptInfo_Success() {
        // Given
        String address = "서울특별시 용산구 이태원동";
        int page = 1;
        int perPage = 5;

        // Mock API 응답 데이터
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> mockData = new ArrayList<>();

        Map<String, Object> aptData = new HashMap<>();
        aptData.put("ADRES", "서울특별시 용산구 이태원동 123-45");
        aptData.put("COMPLEX_GB_CD", "01");
        aptData.put("COMPLEX_NM1", "이태원아파트");
        aptData.put("COMPLEX_NM2", "101동");
        aptData.put("COMPLEX_NM3", "");
        aptData.put("COMPLEX_PK", "11350120401804");
        aptData.put("DONG_CNT", 3);
        aptData.put("PNU", "1111010100");
        aptData.put("UNIT_CNT", 1200);
        aptData.put("USEAPR_DT", "2019-12-01");

        mockData.add(aptData);
        mockResponse.put("data", mockData);

        when(apiClient.getForMap(any(), any())).thenReturn(mockResponse);

        // When
        List<AptInfo> result = aptIdInfoService.fetchAptInfo(address, page, perPage);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        AptInfo aptInfo = result.get(0);
        assertEquals("서울특별시 용산구 이태원동 123-45", aptInfo.getAdres());
        assertEquals("01", aptInfo.getComplexGbCd());
        assertEquals("이태원아파트", aptInfo.getComplexNm1());
        assertEquals("101동", aptInfo.getComplexNm2());
        assertEquals("11350120401804", aptInfo.getComplexPk());
        assertEquals(3, aptInfo.getDongCnt());
        assertEquals(1200, aptInfo.getUnitCnt());
        assertEquals("2019-12-01", aptInfo.getUseaprDt());
    }

    @Test
    void testFetchAptInfo_EmptyResponse() {
        // Given
        String address = "존재하지 않는 주소";
        int page = 1;
        int perPage = 5;

        // Mock API 응답 데이터 (빈 데이터)
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("data", new ArrayList<>());

        when(apiClient.getForMap(any(), any())).thenReturn(mockResponse);

        // When
        List<AptInfo> result = aptIdInfoService.fetchAptInfo(address, page, perPage);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchDongInfo_Success() {
        // Given
        String complexPk = "11350120401804";
        int page = 1;
        int perPage = 5;

        // Mock API 응답 데이터
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> mockData = new ArrayList<>();

        Map<String, Object> dongData = new HashMap<>();
        dongData.put("COMPLEX_PK", "11350120401804");
        dongData.put("DONG_NM1", "101동");
        dongData.put("DONG_NM2", "");
        dongData.put("DONG_NM3", "");
        dongData.put("GRND_FLR_CNT", 25);

        mockData.add(dongData);
        mockResponse.put("data", mockData);

        when(apiClient.getForMap(any(), any())).thenReturn(mockResponse);

        // When
        List<DongInfo> result = aptIdInfoService.fetchDongInfo(complexPk, page, perPage);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        DongInfo dongInfo = result.get(0);
        assertEquals("11350120401804", dongInfo.getComplexPk());
        assertEquals("101동", dongInfo.getDongNm1());
        assertEquals("", dongInfo.getDongNm2());
        assertEquals("", dongInfo.getDongNm3());
        assertEquals(25, dongInfo.getGrndFlrCnt());
    }

    @Test
    void testFetchDongInfo_EmptyResponse() {
        // Given
        String complexPk = "존재하지 않는 단지";
        int page = 1;
        int perPage = 5;

        // Mock API 응답 데이터 (빈 데이터)
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("data", new ArrayList<>());

        when(apiClient.getForMap(any(), any())).thenReturn(mockResponse);

        // When
        List<DongInfo> result = aptIdInfoService.fetchDongInfo(complexPk, page, perPage);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchAptInfo_WithNullValues() {
        // Given
        String address = "서울특별시 용산구 이태원동";
        int page = 1;
        int perPage = 5;

        // Mock API 응답 데이터 (일부 null 값 포함)
        Map<String, Object> mockResponse = new HashMap<>();
        List<Map<String, Object>> mockData = new ArrayList<>();

        Map<String, Object> aptData = new HashMap<>();
        aptData.put("ADRES", "서울특별시 용산구 이태원동 123-45");
        aptData.put("COMPLEX_GB_CD", "");
        aptData.put("COMPLEX_NM1", "이태원아파트");
        aptData.put("COMPLEX_NM2", "");
        aptData.put("COMPLEX_NM3", "");
        aptData.put("COMPLEX_PK", "11350120401804");
        aptData.put("DONG_CNT", 0);
        aptData.put("PNU", "");
        aptData.put("UNIT_CNT", 0);
        aptData.put("USEAPR_DT", "");

        mockData.add(aptData);
        mockResponse.put("data", mockData);

        when(apiClient.getForMap(any(), any())).thenReturn(mockResponse);

        // When
        List<AptInfo> result = aptIdInfoService.fetchAptInfo(address, page, perPage);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        AptInfo aptInfo = result.get(0);
        assertEquals("서울특별시 용산구 이태원동 123-45", aptInfo.getAdres());
        assertEquals("", aptInfo.getComplexGbCd());
        assertEquals("이태원아파트", aptInfo.getComplexNm1());
        assertEquals("", aptInfo.getComplexNm2());
        assertEquals("", aptInfo.getComplexNm3());
        assertEquals("11350120401804", aptInfo.getComplexPk());
        assertEquals(0, aptInfo.getDongCnt());
        assertEquals("", aptInfo.getPnu());
        assertEquals(0, aptInfo.getUnitCnt());
        assertEquals("", aptInfo.getUseaprDt());
    }
}

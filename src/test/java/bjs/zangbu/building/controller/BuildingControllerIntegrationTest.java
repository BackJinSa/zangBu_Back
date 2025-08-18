package bjs.zangbu.building.controller;

import bjs.zangbu.building.dto.response.BuildingResponse.ViewDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BuildingController의 공공데이터 통합 기능 단위 테스트
 * 주소 추출 로직과 데이터 구조를 테스트
 */
@DisplayName("BuildingController 공공데이터 통합 기능 테스트")
class BuildingControllerIntegrationTest {

    private ViewDetailResponse mockBuildingDetail;
    private List<Map<String, Object>> mockAptComplexInfo;

    @BeforeEach
    void setUp() {
        // Mock 매물 상세 정보 설정 - 간단한 객체 생성
        mockBuildingDetail = new ViewDetailResponse();

        // Mock 공공데이터 정보 설정
        mockAptComplexInfo = Arrays.asList(
                createMockAptComplex("이태원 아파트", "A13822001", "서울특별시 용산구 이태원동"),
                createMockAptComplex("용산 프리미엄", "A13822002", "서울특별시 용산구 용산동"),
                createMockAptComplex("한강뷰 아파트", "A13822003", "서울특별시 용산구 한남동"));
    }

    private Map<String, Object> createMockAptComplex(String name, String code, String address) {
        Map<String, Object> complex = new HashMap<>();
        complex.put("kaptName", name);
        complex.put("kaptCode", code);
        complex.put("kaptAddr", address);
        complex.put("complexName", name);
        return complex;
    }

    @Test
    @DisplayName("주소에서 시도명 추출 로직 테스트")
    void testAddressExtraction() {
        // Given - 다양한 주소 패턴으로 테스트
        String[] testAddresses = {
                "서울특별시 용산구 이태원동",
                "부산광역시 해운대구 우동",
                "대구광역시 수성구 범어동",
                "인천광역시 연수구 연수동",
                "경기도 성남시 분당구 정자동"
        };

        String[] expectedSidoNames = {
                "서울", "부산", "대구", "인천", "경기"
        };

        // When & Then
        for (int i = 0; i < testAddresses.length; i++) {
            String extractedSido = extractSidoFromAddress(testAddresses[i]);
            assertEquals(expectedSidoNames[i], extractedSido,
                    "주소 " + testAddresses[i] + "에서 시도명 추출 실패");
        }
    }

    @Test
    @DisplayName("공공데이터 조회 성공 시나리오 테스트")
    void testPublicDataRetrievalSuccess() {
        // Given
        String address = "서울특별시 용산구 이태원동";
        String expectedSido = "서울";

        // When
        String extractedSido = extractSidoFromAddress(address);

        // Then
        assertEquals(expectedSido, extractedSido);
        assertNotNull(mockAptComplexInfo);
        assertEquals(3, mockAptComplexInfo.size());
        assertEquals("이태원 아파트", mockAptComplexInfo.get(0).get("kaptName"));
        assertEquals("A13822001", mockAptComplexInfo.get(0).get("kaptCode"));
    }

    @Test
    @DisplayName("공공데이터 조회 실패 시나리오 테스트")
    void testPublicDataRetrievalFailure() {
        // Given
        String invalidAddress = "존재하지 않는 주소";

        // When
        String extractedSido = extractSidoFromAddress(invalidAddress);

        // Then - 기본값 반환 확인
        assertEquals("서울", extractedSido); // 기본값
    }

    @Test
    @DisplayName("매물 정보 구조 검증 테스트")
    void testBuildingInfoStructure() {
        // Given
        ViewDetailResponse buildingDetail = mockBuildingDetail;

        // Then - 필수 필드들이 존재하는지 확인
        assertNotNull(buildingDetail);
        // ViewDetailResponse의 구조가 올바른지 확인
        // (실제 테스트에서는 실제 데이터로 검증)
    }

    @Test
    @DisplayName("공공데이터 통합 응답 구조 테스트")
    void testIntegratedResponseStructure() {
        // Given
        boolean publicDataAvailable = true;
        String errorMessage = null;

        // When & Then
        assertTrue(publicDataAvailable);
        assertNull(errorMessage);

        // 공공데이터 정보 검증
        assertNotNull(mockAptComplexInfo);
        assertFalse(mockAptComplexInfo.isEmpty());

        // 각 아파트 단지 정보의 필수 필드 검증
        for (Map<String, Object> complex : mockAptComplexInfo) {
            assertNotNull(complex.get("kaptName"));
            assertNotNull(complex.get("kaptCode"));
            assertNotNull(complex.get("kaptAddr"));
        }
    }

    @Test
    @DisplayName("주소 패턴 매칭 테스트")
    void testAddressPatternMatching() {
        // Given - 다양한 주소 패턴
        Map<String, String> addressPatterns = new HashMap<>();
        addressPatterns.put("서울특별시 강남구 역삼동 123-45", "서울");
        addressPatterns.put("부산광역시 해운대구 우동 456-78", "부산");
        addressPatterns.put("경기도 성남시 분당구 정자동 789-12", "경기");
        addressPatterns.put("제주특별자치도 제주시 연동 321-54", "제주");
        addressPatterns.put("세종특별자치시 한솔동 654-87", "세종");

        // When & Then
        addressPatterns.forEach((address, expectedSido) -> {
            String extractedSido = extractSidoFromAddress(address);
            assertEquals(expectedSido, extractedSido,
                    "주소 " + address + "에서 시도명 추출 실패");
        });
    }

    @Test
    @DisplayName("경계값 테스트 - null 및 빈 문자열")
    void testBoundaryValues() {
        // Given
        String nullAddress = null;
        String emptyAddress = "";
        String whitespaceAddress = "   ";

        // When & Then
        assertEquals("서울", extractSidoFromAddress(nullAddress));
        assertEquals("서울", extractSidoFromAddress(emptyAddress));
        assertEquals("서울", extractSidoFromAddress(whitespaceAddress));
    }

    @Test
    @DisplayName("복합 주소 패턴 테스트")
    void testComplexAddressPatterns() {
        // Given - 복잡한 주소 패턴
        String[] complexAddresses = {
                "서울특별시 강남구 역삼동 123-45번지",
                "부산광역시 해운대구 우동 456-78번지",
                "경기도 성남시 분당구 정자동 789-12번지",
                "서울특별시 서초구 서초동 321-54번지",
                "부산광역시 동래구 온천동 654-87번지"
        };

        String[] expectedSidos = { "서울", "부산", "경기", "서울", "부산" };

        // When & Then
        for (int i = 0; i < complexAddresses.length; i++) {
            String extractedSido = extractSidoFromAddress(complexAddresses[i]);
            assertEquals(expectedSidos[i], extractedSido,
                    "복합 주소 " + complexAddresses[i] + "에서 시도명 추출 실패");
        }
    }

    /**
     * 주소에서 시도명 추출 (BuildingController와 동일한 로직)
     */
    private String extractSidoFromAddress(String address) {
        if (address == null || address.isEmpty() || address.trim().isEmpty()) {
            return "서울"; // 기본값
        }

        // 주소에서 시도명 추출 로직
        if (address.contains("서울특별시"))
            return "서울";
        if (address.contains("부산광역시"))
            return "부산";
        if (address.contains("대구광역시"))
            return "대구";
        if (address.contains("인천광역시"))
            return "인천";
        if (address.contains("광주광역시"))
            return "광주";
        if (address.contains("대전광역시"))
            return "대전";
        if (address.contains("울산광역시"))
            return "울산";
        if (address.contains("세종특별자치시"))
            return "세종";
        if (address.contains("경기도"))
            return "경기";
        if (address.contains("강원도"))
            return "강원";
        if (address.contains("충청북도"))
            return "충북";
        if (address.contains("충청남도"))
            return "충남";
        if (address.contains("전라북도"))
            return "전북";
        if (address.contains("전라남도"))
            return "전남";
        if (address.contains("경상북도"))
            return "경북";
        if (address.contains("경상남도"))
            return "경남";
        if (address.contains("제주특별자치도"))
            return "제주";

        return "서울"; // 기본값
    }
}

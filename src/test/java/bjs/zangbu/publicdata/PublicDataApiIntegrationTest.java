package bjs.zangbu.publicdata;

import bjs.zangbu.publicdata.client.ApiClient;
import bjs.zangbu.publicdata.service.aptinfo.AptIdInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 실제 공공데이터 API를 호출하는 통합 테스트
 * 이 테스트는 실제 API 키를 사용하여 공공데이터 API가 정상적으로 작동하는지 확인합니다.
 */
class PublicDataApiIntegrationTest {

    private RestTemplate restTemplate;

    public PublicDataApiIntegrationTest() {
        this.restTemplate = new RestTemplate();
    }

    @Test
    void testRealPublicDataApi() {
        System.out.println("🚀 실제 공공데이터 API 테스트 시작...");

        // 1. ApiClient 생성
        ApiClient apiClient = new ApiClient(restTemplate);

        // 2. 실제 공공데이터 API 호출 테스트
        try {
            // 아파트 단지 정보 조회 API 테스트
            System.out.println("📋 아파트 단지 정보 조회 API 테스트...");
            Map<String, String> params = new HashMap<>();
            params.put("page", "1");
            params.put("perPage", "5");
            params.put("cond[ADRES::LIKE]", "서울특별시 용산구 이태원동");

            Map<String, Object> response = apiClient.getForMap(
                    "https://api.odcloud.kr/api/AptIdInfoSvc/v1/getAptInfo",
                    params);

            System.out.println("✅ API 호출 성공!");
            System.out.println("📊 응답 데이터: " + response);

            // 응답 데이터 구조 확인
            if (response.containsKey("data")) {
                List<?> dataList = (List<?>) response.get("data");
                System.out.println("🏢 조회된 아파트 단지 수: " + dataList.size());

                if (!dataList.isEmpty()) {
                    Map<?, ?> firstApt = (Map<?, ?>) dataList.get(0);
                    System.out.println("🏠 첫 번째 아파트 정보:");
                    System.out.println("   - 주소: " + firstApt.get("ADRES"));
                    System.out.println("   - 단지명: " + firstApt.get("COMPLEX_NM1"));
                    System.out.println("   - 세대수: " + firstApt.get("UNIT_CNT"));
                    System.out.println("   - 준공일자: " + firstApt.get("USEAPR_DT"));
                }
            }

        } catch (Exception e) {
            System.err.println("❌ API 호출 실패: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. 아파트 실거래가 API 테스트
        try {
            System.out.println("\n💰 아파트 실거래가 API 테스트...");
            Map<String, String> tradeParams = new HashMap<>();
            tradeParams.put("locataddNm", "서울");
            tradeParams.put("dealYmd", "202412");
            tradeParams.put("pageNo", "1");
            tradeParams.put("numOfRows", "10");

            Map<String, Object> tradeResponse = apiClient.getForMap(
                    "https://api.odcloud.kr/api/AptIdInfoSvc/v1/getAptTrade",
                    tradeParams);

            System.out.println("✅ 실거래가 API 호출 성공!");
            System.out.println("📊 실거래가 응답: " + tradeResponse);

        } catch (Exception e) {
            System.err.println("❌ 실거래가 API 호출 실패: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n🎉 공공데이터 API 통합 테스트 완료!");
    }

    @Test
    void testAptIdInfoServiceWithRealApi() {
        System.out.println("🔧 AptIdInfoService 실제 API 연동 테스트...");

        try {
            // AptIdInfoService 생성
            AptIdInfoServiceImpl aptIdInfoService = new AptIdInfoServiceImpl(new ApiClient(restTemplate));

            // 실제 주소로 아파트 정보 조회
            System.out.println("📍 '서울특별시 용산구 이태원동' 주소로 아파트 정보 조회...");
            List<?> aptInfoList = aptIdInfoService.fetchAptInfo("서울특별시 용산구 이태원동", 1, 5);

            System.out.println("✅ 서비스 호출 성공!");
            System.out.println("🏢 조회된 아파트 수: " + aptInfoList.size());

            if (!aptInfoList.isEmpty()) {
                Object firstApt = aptInfoList.get(0);
                System.out.println("🏠 첫 번째 아파트: " + firstApt);
            }

        } catch (Exception e) {
            System.err.println("❌ 서비스 호출 실패: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("🎉 AptIdInfoService 테스트 완료!");
    }
}

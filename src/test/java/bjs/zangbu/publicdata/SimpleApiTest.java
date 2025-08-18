package bjs.zangbu.publicdata;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.HashMap;

/**
 * 실제 공공데이터 API를 호출하는 간단한 테스트 프로그램
 */
public class SimpleApiTest {

    // application.yml에 설정된 공공데이터 API 키
    private static final String SERVICE_KEY = "KI6q5jPyEQnEa9tmhllI6W8ufddtQ68gxlGFxHFspiOhfRCEF%2BfoUQ4oHLgL%2Bs61oIGO%2F1lS75LSfB%2FIBuFeSQ%3D%3D";

    public static void main(String[] args) {
        System.out.println("🚀 공공데이터 API 실제 호출 테스트 시작!");
        System.out.println("🔑 API 키: " + SERVICE_KEY.substring(0, 20) + "...");

        // 1. 아파트 단지 정보 조회 API 테스트
        testAptInfoApi();

        // 2. 아파트 실거래가 API 테스트
        testAptTradeApi();

        System.out.println("\n🎉 모든 API 테스트 완료!");
    }

    /**
     * 아파트 단지 정보 조회 API 테스트
     */
    private static void testAptInfoApi() {
        System.out.println("\n📋 아파트 단지 정보 조회 API 테스트...");

        try {
            // API URL과 파라미터 구성
            String baseUrl = "https://api.odcloud.kr/api/AptIdInfoSvc/v1/getAptInfo";
            String queryParams = String.format(
                    "?serviceKey=%s&page=1&perPage=5&cond[ADRES::LIKE]=%s",
                    SERVICE_KEY,
                    java.net.URLEncoder.encode("서울특별시 용산구 이태원동", "UTF-8"));

            String fullUrl = baseUrl + queryParams;
            System.out.println("🌐 요청 URL: " + fullUrl);

            // HTTP 클라이언트 생성 및 요청
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .GET()
                    .build();

            // 응답 받기
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("✅ 응답 상태 코드: " + response.statusCode());
            System.out.println("📊 응답 데이터:");
            System.out.println(response.body());

            // 응답 데이터 파싱 및 분석
            analyzeAptInfoResponse(response.body());

        } catch (Exception e) {
            System.err.println("❌ API 호출 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 아파트 실거래가 API 테스트
     */
    private static void testAptTradeApi() {
        System.out.println("\n💰 아파트 실거래가 API 테스트...");

        try {
            // API URL과 파라미터 구성
            String baseUrl = "https://api.odcloud.kr/api/AptIdInfoSvc/v1/getAptTrade";
            String queryParams = String.format(
                    "?serviceKey=%s&locataddNm=%s&dealYmd=%s&pageNo=1&numOfRows=10",
                    SERVICE_KEY,
                    java.net.URLEncoder.encode("서울", "UTF-8"),
                    "202412");

            String fullUrl = baseUrl + queryParams;
            System.out.println("🌐 요청 URL: " + fullUrl);

            // HTTP 클라이언트 생성 및 요청
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .GET()
                    .build();

            // 응답 받기
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("✅ 응답 상태 코드: " + response.statusCode());
            System.out.println("📊 응답 데이터:");
            System.out.println(response.body());

        } catch (Exception e) {
            System.err.println("❌ API 호출 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 아파트 정보 응답 데이터 분석
     */
    private static void analyzeAptInfoResponse(String responseBody) {
        System.out.println("\n🔍 응답 데이터 분석:");

        try {
            // 간단한 문자열 분석 (실제로는 JSON 파서 사용 권장)
            if (responseBody.contains("data")) {
                System.out.println("✅ 'data' 필드가 응답에 포함되어 있습니다.");

                if (responseBody.contains("서울특별시 용산구 이태원동")) {
                    System.out.println("✅ 요청한 주소가 응답에 포함되어 있습니다.");
                }

                if (responseBody.contains("COMPLEX_NM1")) {
                    System.out.println("✅ 아파트 단지명 필드가 응답에 포함되어 있습니다.");
                }

                if (responseBody.contains("UNIT_CNT")) {
                    System.out.println("✅ 세대수 필드가 응답에 포함되어 있습니다.");
                }

                if (responseBody.contains("USEAPR_DT")) {
                    System.out.println("✅ 준공일자 필드가 응답에 포함되어 있습니다.");
                }

                // 응답 길이 확인
                System.out.println("📏 응답 데이터 길이: " + responseBody.length() + " 문자");

            } else {
                System.out.println("❌ 'data' 필드가 응답에 포함되어 있지 않습니다.");
            }

        } catch (Exception e) {
            System.err.println("❌ 응답 분석 실패: " + e.getMessage());
        }
    }
}

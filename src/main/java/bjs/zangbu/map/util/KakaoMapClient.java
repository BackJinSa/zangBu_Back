package bjs.zangbu.map.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import bjs.zangbu.map.dto.request.MapCategoryRequest;
import bjs.zangbu.map.dto.response.MapCategoryResponse;
import bjs.zangbu.map.dto.response.MapSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class KakaoMapClient {
        private final RestTemplate rest = new RestTemplate();

        @Value("${kakao.rest-api-key}")
        private String apiKey;

        // search 부분
        public List<MapSearchResponse> searchByKeyword(String query) {
                String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" +
                                URLEncoder.encode(query, StandardCharsets.UTF_8);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "KakaoAK " + apiKey);
                HttpEntity<Void> req = new HttpEntity<>(headers);

                ResponseEntity<JsonNode> resp = rest.exchange(url, HttpMethod.GET, req, JsonNode.class);
                System.out.println("asdasdasdasdasdasd#@!@#!@#!@#!@#!@#"+ resp.getBody());
                JsonNode docs = resp.getBody().get("documents");
                return StreamSupport.stream(docs.spliterator(), false)
                                .map(node -> new MapSearchResponse(
                                                node.get("place_name").asText(),
                                                node.get("address_name").asText(),
                                                node.get("road_address_name").asText(),
                                                node.get("x").asText(),
                                                node.get("y").asText(),
                                                node.get("place_url").asText()))
                                .collect(Collectors.toList());
        }

        public Optional<double[]> geocodeRoadAddress(String roadAddress) {
                String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" +
                                URLEncoder.encode(roadAddress, StandardCharsets.UTF_8);

                log.info("카카오 주소 변환 API 요청 URL: {}", url);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "KakaoAK " + apiKey);
                HttpEntity<Void> req = new HttpEntity<>(headers);

                ResponseEntity<JsonNode> resp = rest.exchange(url, HttpMethod.GET, req, JsonNode.class);

                log.info("카카오 API 응답: {}", resp.getBody());

                JsonNode docs = resp.getBody().get("documents");
                if (docs == null || !docs.elements().hasNext())
                        return Optional.empty();

                JsonNode first = docs.get(0);
                // road_address가 없으면 address로 폴백
                JsonNode addr = first.hasNonNull("road_address") ? first.get("road_address") : first.get("address");
                if (addr == null || addr.isNull())
                        return Optional.empty();

                double x = addr.get("x").asDouble(); // 경도
                double y = addr.get("y").asDouble(); // 위도
                return Optional.of(new double[] { y, x }); // [위도, 경도]
        }

        // category 부분
        public List<MapCategoryResponse> searchByCategory(MapCategoryRequest req) {
                // 1. 요청 파라미터를 문자열로 조합하여 카카오 REST API URL 생성
                String url = String.format(
                                "https://dapi.kakao.com/v2/local/search/category.json?" +
                                                "category_group_code=%s&x=%s&y=%s&radius=%d&page=%d&size=%d&sort=%s",
                                req.getCategory_group_code(),
                                req.getX(), req.getY(),
                                req.getRadius(),
                                req.getPage(), req.getSize(),
                                req.getSort());

                // 2. HTTP 헤더 세팅: REST API Key와 Content-Type
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "KakaoAK " + apiKey);
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Void> httpReq = new HttpEntity<>(headers);

                // 3. RestTemplate.exchange()로 GET 요청 실행, JSON 노드 형태로 응답 수신
                ResponseEntity<JsonNode> resp = rest.exchange(
                                url,
                                HttpMethod.GET,
                                httpReq,
                                JsonNode.class);

                // 4. 응답 JSON에서 "documents" 배열 추출
                JsonNode docs = resp.getBody().get("documents");

                // 5. JSON 배열을 스트림으로 순회하며 MapCategoryResponse DTO로 변환
                return StreamSupport.stream(docs.spliterator(), false)
                                .map(node -> new MapCategoryResponse(
                                                node.get("place_name").asText(), // 장소 이름
                                                node.get("distance").asText(), // 중심 좌표와의 거리
                                                node.get("place_url").asText(), // 상세 페이지 URL
                                                node.get("category_group_code").asText(), // 카테고리 코드
                                                node.get("category_group_name").asText(), // 카테고리 이름
                                                node.get("address_name").asText(), // 지번 주소
                                                node.get("road_address_name").asText(), // 도로명 주소
                                                node.get("x").asText(), // 경도
                                                node.get("y").asText(), // 위도
                                                node.get("phone").asText(), // 전화번호
                                                node.get("id").asText() // 장소 고유 ID
                                ))
                                .collect(Collectors.toList());
        }
}

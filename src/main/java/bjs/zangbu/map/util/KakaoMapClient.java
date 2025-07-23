package bjs.zangbu.map.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import bjs.zangbu.map.dto.response.MapSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class KakaoMapClient {
    private final RestTemplate rest = new RestTemplate();

    @Value("${kakao.rest-api-key}")
    private String apiKey;

    public List<MapSearchResponse> searchByKeyword(String query) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> resp =
                rest.exchange(url, HttpMethod.GET, req, JsonNode.class);

        JsonNode docs = resp.getBody().get("documents");
        return StreamSupport.stream(docs.spliterator(), false)
                .map(node -> new MapSearchResponse(
                        node.get("place_name").asText(),
                        node.get("address_name").asText(),
                        node.get("road_address_name").asText(),
                        node.get("x").asText(),
                        node.get("y").asText(),
                        node.get("place_url").asText()
                ))
                .collect(Collectors.toList());
    }
}

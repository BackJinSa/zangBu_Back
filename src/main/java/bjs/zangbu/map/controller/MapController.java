package bjs.zangbu.map.controller;

import bjs.zangbu.map.dto.request.MapCategoryRequest;
import bjs.zangbu.map.dto.request.MapListRequest;
import bjs.zangbu.map.dto.request.MapSearchRequest;
import bjs.zangbu.map.dto.response.MapCategoryResponse;
import bjs.zangbu.map.dto.response.MapListResponse;
import bjs.zangbu.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {
    private final MapService mapService;

    @PostMapping("/list")
    public ResponseEntity<?> list(@RequestBody List<MapListRequest> body) {
        try {
            List<MapListResponse> result = mapService.locate(body);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("매물을 불러오는데 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("서버에서 매물을 불러오는데 실패횄습니다.");
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody MapSearchRequest body) {
        try {
            return ResponseEntity.ok(mapService.search(body));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("매물 정보를 가져오는데 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버에서 매물에 대한 정보를 가져오는데 실패했습니다.");
        }
    }

    @PostMapping("/category")
    public ResponseEntity<?> category(@RequestBody MapCategoryRequest body) {
        try {
            List<MapCategoryResponse> result = mapService.category(body);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("카테고리로 필터링하는데 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("서버에서 카테고리로 필터링하는데 오류가 발생했습니다.");
        }
    }
}

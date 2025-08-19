package bjs.zangbu.map.controller;

import bjs.zangbu.map.dto.request.MapCategoryRequest;
import bjs.zangbu.map.dto.request.MapFilterRequest;
import bjs.zangbu.map.dto.request.MapListRequest;
import bjs.zangbu.map.dto.request.MapSearchRequest;
import bjs.zangbu.map.dto.response.MapCategoryResponse;
import bjs.zangbu.map.dto.response.MapListResponse;
import bjs.zangbu.map.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
@Log4j2
public class MapController {
    private final MapService mapService;

    @PostMapping("")
    public List<MapListResponse> getFilteredMapList(
            @RequestBody MapFilterRequest req) {
        return mapService.locateWithFilter(req);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        try {
            List<MapListResponse> result = mapService.getAllBuildingLocations();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("지도 매물 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body("서버에서 매물을 불러오는데 실패했습니다.");
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

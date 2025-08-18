package bjs.zangbu.publicdata.controller;

import bjs.zangbu.publicdata.service.aptinfo.PublicDataIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 공공데이터 통합 정보를 제공하는 컨트롤러
 * 사진에 보이는 모든 매물 정보를 공공데이터 API로 제공
 */
@RestController
@RequestMapping("/publicdata/integration")
@RequiredArgsConstructor
public class PublicDataIntegrationController {

    private final PublicDataIntegrationService publicDataIntegrationService;

    /**
     * 주소 기반으로 아파트 단지의 모든 상세 정보 조회
     * 사진에 보이는 면적, 층수, 주소, 난방, 준공일자, 세대수 등을 포함
     * 
     * @param address 조회할 주소 (예: "서울특별시 용산구 이태원동")
     * @return 통합된 아파트 정보
     */
    @GetMapping("/apt-info")
    public ResponseEntity<Map<String, Object>> getCompleteAptInfo(
            @RequestParam("address") String address) {

        Map<String, Object> result = publicDataIntegrationService.getCompleteAptInfo(address);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 특정 아파트 단지의 실거래가 정보 조회
     * 
     * @param complexPk     아파트 단지 고유번호
     * @param dealYearMonth 거래년월 (예: "202412")
     * @return 실거래가 정보
     */
    @GetMapping("/apt-trade")
    public ResponseEntity<Map<String, Object>> getAptTradeInfo(
            @RequestParam("complexPk") String complexPk,
            @RequestParam("dealYearMonth") String dealYearMonth) {

        Map<String, Object> result = publicDataIntegrationService.getAptTradeInfo(complexPk, dealYearMonth);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 매물 ID로 통합 정보 조회 (프론트엔드에서 사용)
     * 
     * @param buildingId 매물 ID
     * @return 통합된 매물 정보
     */
    @GetMapping("/property/{buildingId}")
    public ResponseEntity<Map<String, Object>> getPropertyInfo(
            @PathVariable("buildingId") String buildingId) {

        // 실제로는 buildingId로 매물 정보를 조회하고, 그 주소로 공공데이터를 가져와야 함
        // 현재는 테스트용으로 하드코딩된 주소 사용
        String testAddress = "서울특별시 용산구 이태원동";

        Map<String, Object> result = publicDataIntegrationService.getCompleteAptInfo(testAddress);
        result.put("buildingId", buildingId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

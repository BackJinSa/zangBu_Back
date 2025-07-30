package bjs.zangbu.publicdata.controller.managecost;

import bjs.zangbu.publicdata.dto.managecost.GasCost;
import bjs.zangbu.publicdata.dto.managecost.HeatCost;
import bjs.zangbu.publicdata.service.managecost.ManageCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publicdata/managecost")
@RequiredArgsConstructor
public class ManageCostController {

    private final ManageCostService service;

    /** 단지별 난방비 조회
     * GET /publicdata/managecost/heat?kaptCode=XXX&searchDate=YYYYMM
     */
    @GetMapping("/heat")
    public ResponseEntity<HeatCost> getHeatCost(
            @RequestParam String kaptCode,
            @RequestParam String searchDate
    ) {
        return ResponseEntity.ok(service.fetchHeatCost(kaptCode, searchDate));
    }

    /** 단지별 가스비 조회
     * GET /publicdata/managecost/gas?kaptCode=XXX&searchDate=YYYYMM
     */
    @GetMapping("/gas")
    public ResponseEntity<GasCost> getGasCost(
            @RequestParam String kaptCode,
            @RequestParam String searchDate
    ) {
        return ResponseEntity.ok(service.fetchGasCost(kaptCode, searchDate));
    }
}
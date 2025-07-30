package bjs.zangbu.publicdata.controller.managecost.publicuse;

import bjs.zangbu.publicdata.dto.managecost.publicuse.CleanCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.EduCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.EtcCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.VehicleCost;
import bjs.zangbu.publicdata.service.managecost.publicuse.PublicUseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/publicdata/commonuse")
public class PublicUseController {
    private final PublicUseService svc;

    @GetMapping("/vehicle")
    public ResponseEntity<VehicleCost> getVehicle(
            @RequestParam String kaptCode,
            @RequestParam String searchDate
    ) {
        return ResponseEntity.ok(svc.fetchVehicleCost(kaptCode, searchDate));
    }

    @GetMapping("/etc")
    public ResponseEntity<EtcCost> getEtc(
            @RequestParam String kaptCode,
            @RequestParam String searchDate
    ) {
        return ResponseEntity.ok(svc.fetchEtcCost(kaptCode, searchDate));
    }

    @GetMapping("/edu")
    public ResponseEntity<EduCost> getEdu(
            @RequestParam String kaptCode,
            @RequestParam String searchDate
    ) {
        return ResponseEntity.ok(svc.fetchEduCost(kaptCode, searchDate));
    }

    @GetMapping("/clean")
    public ResponseEntity<CleanCost> getClean(
            @RequestParam String kaptCode,
            @RequestParam String searchDate
    ) {
        return ResponseEntity.ok(svc.fetchCleanCost(kaptCode, searchDate));
    }
}

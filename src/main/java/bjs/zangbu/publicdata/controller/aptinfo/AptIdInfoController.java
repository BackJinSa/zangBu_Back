package bjs.zangbu.publicdata.controller.aptinfo;

import bjs.zangbu.publicdata.dto.aptinfo.AptInfo;
import bjs.zangbu.publicdata.dto.aptinfo.DongInfo;
import bjs.zangbu.publicdata.service.aptinfo.AptIdInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * /publicdata/aptidinfo 경로로 들어오는
 * 공동주택 단지 식별정보 조회 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/publicdata/aptidinfo")
@RequiredArgsConstructor
public class AptIdInfoController {

    private final AptIdInfoService svc;

    /**
     * 주소 조건으로 단지 기본정보 조회
     * GET /publicdata/aptidinfo/info?adres=서울&page=1&perPage=5
     */
    @GetMapping("/info")
    public ResponseEntity<List<AptInfo>> getAptInfo(
            @RequestParam String adres,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int perPage
    ) {
        return ResponseEntity.ok(svc.fetchAptInfo(adres, page, perPage));
    }

    /**
     * 단지고유번호로 동정보 조회
     * GET /publicdata/aptidinfo/dong?complexPk=11350120401804&page=1&perPage=5
     */
    @GetMapping("/dong")
    public ResponseEntity<List<DongInfo>> getDongInfo(
            @RequestParam String complexPk,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int perPage
    ) {
        return ResponseEntity.ok(svc.fetchDongInfo(complexPk, page, perPage));
    }
}

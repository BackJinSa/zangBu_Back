package bjs.zangbu.publicdata.controller.aptlist;

import bjs.zangbu.publicdata.dto.aptlist.AptComplex;
import bjs.zangbu.publicdata.dto.aptlist.RoadAptComplex;
import bjs.zangbu.publicdata.service.aptlist.AptListService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * /publicdata/aptlist 경로로 들어오는
 * 아파트 단지 조회 관련 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/publicdata/aptlist")
@RequiredArgsConstructor
public class AptListController {

    private final AptListService svc;

    /** 전체 아파트 단지 목록 조회 */
    @GetMapping("/total")
    public ResponseEntity<List<AptComplex>> total(
            @RequestParam(defaultValue="1") int pageNo,
            @RequestParam(defaultValue="1500") int numOfRows
    ) {
        return ResponseEntity.ok(svc.getTotalAptList(pageNo, numOfRows));
    }

    /** 시도별 아파트 단지 조회 */
    @GetMapping("/sido")
    public ResponseEntity<List<AptComplex>> sido(
            @RequestParam String sidoCode,
            @RequestParam(defaultValue="1") int pageNo,
            @RequestParam(defaultValue="3") int numOfRows
    ) {
        return ResponseEntity.ok(svc.getSidoAptList(sidoCode, pageNo, numOfRows));
    }

    /** 시군구별 아파트 단지 조회 */
    @GetMapping("/sigungu")
    public ResponseEntity<List<AptComplex>> sigungu(
            @RequestParam String sigunguCode,
            @RequestParam(defaultValue="1") int pageNo,
            @RequestParam(defaultValue="3") int numOfRows
    ) {
        return ResponseEntity.ok(svc.getSigunguAptList(sigunguCode, pageNo, numOfRows));
    }

    /** 법정동별 아파트 단지 조회 */
    @GetMapping("/legaldong")
    public ResponseEntity<List<AptComplex>> legaldong(
            @RequestParam String bjdCode,
            @RequestParam(defaultValue="1") int pageNo,
            @RequestParam(defaultValue="5") int numOfRows
    ) {
        return ResponseEntity.ok(svc.getLegaldongAptList(bjdCode, pageNo, numOfRows));
    }

    /** 도로명 기반 아파트 단지 조회 */
    @GetMapping("/roadname")
    public ResponseEntity<List<RoadAptComplex>> roadname(
            @RequestParam String roadCode,
            @RequestParam(defaultValue="1") int pageNo,
            @RequestParam(defaultValue="5") int numOfRows
    ) {
        return ResponseEntity.ok(svc.getRoadnameAptList(roadCode, pageNo, numOfRows));
    }
}
package bjs.zangbu.publicdata.controller.trade;

import bjs.zangbu.publicdata.dto.trade.HousingRent;
import bjs.zangbu.publicdata.service.trade.HousingRentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/publicdata/housingrent")
public class HousingRentController {
    private final HousingRentService service;

    /**
     * GET /publicdata/shrent
     * @param lawdCd    법정동 코드 앞 5자리
     * @param dealYmd   계약년월 (YYYYMM)
     * @param pageNo    페이지 번호 (기본 1)
     * @param numOfRows 페이지당 건수 (기본 5)
     */
    @GetMapping
    public ResponseEntity<List<HousingRent>> getSHRents(
            @RequestParam("LAWD_CD") String lawdCd,
            @RequestParam("DEAL_YMD") String dealYmd,
            @RequestParam(value="pageNo", defaultValue="1") int pageNo,
            @RequestParam(value="numOfRows", defaultValue="5") int numOfRows
    ) {
        List<HousingRent> list = service.fetchSHRents(lawdCd, dealYmd, pageNo, numOfRows);
        return ResponseEntity.ok(list);
    }
}

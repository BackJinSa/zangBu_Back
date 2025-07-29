package bjs.zangbu.publicdata.controller.trade;

import bjs.zangbu.publicdata.dto.trade.VillaRent;
import bjs.zangbu.publicdata.service.trade.VillaRentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/publicdata/villarent")
@RequiredArgsConstructor
public class VillaController {
    private final VillaRentService service;

    /**
     * 법정동코드 앞 5자리(lawdCd)와 년월(dealYmd)로 연립·다세대 월세 실거래가 조회
     * GET /publicdata/rhrent?lawdCd=11710&dealYmd=201512
     */
    @GetMapping
    public ResponseEntity<List<VillaRent>> getRHRents(
            @RequestParam("lawdCd") String lawdCd,
            @RequestParam("dealYmd") String dealYmd) {
        List<VillaRent> rents = service.fetchVillaRents(lawdCd, dealYmd);
        return ResponseEntity.ok(rents);
    }
}

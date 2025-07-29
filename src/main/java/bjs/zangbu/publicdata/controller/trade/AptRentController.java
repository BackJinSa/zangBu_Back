package bjs.zangbu.publicdata.controller.trade;

import bjs.zangbu.publicdata.dto.trade.AptRent;
import bjs.zangbu.publicdata.service.trade.AptRentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/publicdata/rent")
@RequiredArgsConstructor
public class AptRentController {

    private final AptRentService service;

    /**
     * 법정동코드 앞 5자리(lawdCd)와 계약년월(dealYmd)으로
     * 아파트 전·월세 실거래가 조회
     *
     * GET /publicdata/rent?lawdCd=11710&dealYmd=201512
     */
    @GetMapping
    public ResponseEntity<List<AptRent>> getAptRent(
            @RequestParam("lawdCd") String lawdCd,
            @RequestParam("dealYmd") String dealYmd) {

        List<AptRent> rents = service.fetchAptRent(lawdCd, dealYmd);
        return ResponseEntity.ok(rents);
    }
}
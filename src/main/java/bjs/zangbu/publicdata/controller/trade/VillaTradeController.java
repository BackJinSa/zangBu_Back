package bjs.zangbu.publicdata.controller.trade;

import bjs.zangbu.publicdata.dto.trade.VillaTrade;
import bjs.zangbu.publicdata.service.trade.VillaTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/publicdata/villatrade")
@RequiredArgsConstructor
public class VillaTradeController {
    private final VillaTradeService service;

    /**
     * 법정동코드 앞 5자리(lawdCd)와 년월(dealYmd)로 연립·다세대 매매 실거래가 조회
     * GET /publicdata/rhtrade?lawdCd=11710&dealYmd=201512
     */
    @GetMapping
    public ResponseEntity<List<VillaTrade>> getRHTrades(
            @RequestParam("lawdCd") String lawdCd,
            @RequestParam("dealYmd") String dealYmd) {
        List<VillaTrade> trades = service.fetchVillaTrades(lawdCd, dealYmd);
        return ResponseEntity.ok(trades);
    }
}

package bjs.zangbu.publicdata.controller.trade;

import java.util.List;

import bjs.zangbu.publicdata.dto.law.LawCode;
import bjs.zangbu.publicdata.dto.trade.AptTrade;
import bjs.zangbu.publicdata.service.law.LawCodeService;
import bjs.zangbu.publicdata.service.trade.AptTradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publicdata/apt")
@RequiredArgsConstructor
public class AptTradeController {

    private final LawCodeService lawService;
    private final AptTradeService tradeService;

    /**
     * 1) locataddNm (ex: "서울") 로 법정동 코드 목록 조회
     * 2) 첫 번째 코드의 앞 5자리(LAWD_CD)만 잘라서
     * 3) DEAL_YMD, pageNo, numOfRows 와 함께 실거래가 조회
     *
     * GET /publicdata/apt/trade
     *  ?locataddNm=서울
     *  &dealYmd=201512
     *  [&pageNo=1]
     *  [&numOfRows=1500]
     */
    @GetMapping("/trade")
    public ResponseEntity<List<AptTrade>> getAptTrades(
            @RequestParam("locataddNm") String locataddNm,
            @RequestParam("dealYmd")    String dealYmd,
            @RequestParam(value="pageNo",    defaultValue="1")     int pageNo,
            @RequestParam(value="numOfRows", defaultValue="1500")  int numOfRows
    ) {
        // 1) 법정동 코드 가져오기
        List<LawCode> codes = lawService.fetchLawCodes(locataddNm);
        if (codes.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(List.of());
        }

        // 2) 앞 5자리만 잘라서 시군구 코드로 사용
        String lawdCd5 = codes.get(0).getRegionCd().substring(0,5);

        // 3) 아파트 매매 실거래가 조회
        List<AptTrade> trades = tradeService.fetchAptTrades(lawdCd5, dealYmd, pageNo, numOfRows);
        return ResponseEntity.ok(trades);
    }
}

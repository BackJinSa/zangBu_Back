package bjs.zangbu.publicdata.controller.trade;

import bjs.zangbu.publicdata.dto.trade.OfficeRent;
import bjs.zangbu.publicdata.service.trade.OfficeRentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/publicdata/offirent")
public class OfficeRentController {

  private final OfficeRentService service;

  /**
   * GET /publicdata/offirent
   *
   * @param lawdCd    법정동 코드 앞 5자리
   * @param dealYmd   계약년월 (YYYYMM)
   * @param pageNo    페이지 번호 (기본 1)
   * @param numOfRows 페이지당 건수 (기본 1500)
   */
  @GetMapping
  public ResponseEntity<List<OfficeRent>> getOffiRents(
      @RequestParam("LAWD_CD") String lawdCd,
      @RequestParam("DEAL_YMD") String dealYmd,
      @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
      @RequestParam(value = "numOfRows", defaultValue = "1500") int numOfRows
  ) {
    List<OfficeRent> list = service.fetchOfficeRents(lawdCd, dealYmd, pageNo, numOfRows);
    return ResponseEntity.ok(list);
  }
}

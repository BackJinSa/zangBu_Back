package bjs.zangbu.publicdata.controller.law;

import bjs.zangbu.publicdata.dto.law.LawCode;
import bjs.zangbu.publicdata.service.law.LawCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/publicdata/law")
@RequiredArgsConstructor
public class LawCodeController {

    private final LawCodeService service;

    /**
     * locataddNm(예: "서울") 로 법정동 코드 목록 조회
     * GET /publicdata/law?locataddNm=서울
     */
    @GetMapping
    public ResponseEntity<List<LawCode>> getLawCodes(
            @RequestParam("locataddNm") String locataddNm) {
        List<LawCode> codes = service.fetchLawCodes(locataddNm);
        return ResponseEntity.ok(codes);
    }
}
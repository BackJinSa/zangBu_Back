package bjs.zangbu.publicdata.service.trade;

import bjs.zangbu.publicdata.dto.trade.AptRent;

import java.util.List;

public interface AptRentService {
    /**
     * @param lawdCd   법정동 코드 앞 5자리 (예: "11710")
     * @param dealYmd  계약년월 (예: "201512")
     */
    List<AptRent> fetchAptRent(String lawdCd, String dealYmd);
}
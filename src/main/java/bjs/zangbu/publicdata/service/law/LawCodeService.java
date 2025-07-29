package bjs.zangbu.publicdata.service.law;

import bjs.zangbu.publicdata.dto.law.LawCode;

import java.util.List;

public interface LawCodeService {
    List<LawCode> fetchLawCodes(String locataddNm);
}

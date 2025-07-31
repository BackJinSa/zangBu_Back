package bjs.zangbu.publicdata.service.aptinfo;

import bjs.zangbu.publicdata.dto.aptinfo.AptInfo;
import bjs.zangbu.publicdata.dto.aptinfo.DongInfo;

import java.util.List;

/**
 * 공동주택 단지 식별정보 조회 서비스 인터페이스
 */
public interface AptIdInfoService {
    /**
     * 주소(ADRES) 조건으로 단지 기본정보 조회
     * @param adres 주소 키워드 (LIKE 검색)
     * @param page  페이지 인덱스
     * @param perPage 페이지 크기
     */
    List<AptInfo> fetchAptInfo(String adres, int page, int perPage);

    /**
     * 단지고유번호(COMPLEX_PK) 로 동정보 조회
     * @param complexPk 단지고유번호
     * @param page 페이지 인덱스
     * @param perPage 페이지 크기
     */
    List<DongInfo> fetchDongInfo(String complexPk, int page, int perPage);
}
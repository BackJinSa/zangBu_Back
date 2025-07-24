package bjs.zangbu.building.service;

import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;

public interface BuildingService {

    // 매물 상세 정보를 조회하는 서비스
    ViewDetailResponse viewDetailService(ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    // 매물 찜하기 서비스 - 찜 추가
    void bookMarkService(BookmarkRequest request, String memberId);

    // 매물 찜 해제 서비스 - 찜 삭제
    void bookMarkServiceDelete(Long buildingId, String memberId);

    // 매물 등록 서비스
    void SaleRegistration(SaleRegistrationRequest request, String memberId);

    // 필터 조건과 페이징 정보를 기반으로 매물 목록 조회
    FilteredResponse getBuildingList(String buildingName, String saleType, Long startPrice, Long endPrice, String propertyType, int page, int size, String memberId);

    // 매물 삭제 서비스
    void removeBuilding(Long buildingId);
}

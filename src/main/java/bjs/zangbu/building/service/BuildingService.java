package bjs.zangbu.building.service;
import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;

/**
 * Building(매물) 관련 서비스 인터페이스
 * 매물 상세 조회, 찜 추가/삭제, 등록, 삭제, 목록 조회 기능을 정의한다.
 */
public interface BuildingService {

    /**
     * 매물 찜하기 서비스 (찜 추가)
     *
     * @param request 찜 요청 DTO
     * @param memberId 찜하는 회원 ID
     */
    void bookMarkService(BookmarkRequest request, String memberId);

    /**
     * 매물 찜 해제 서비스 (찜 삭제)
     *
     * @param buildingId 찜 해제할 매물 ID
     * @param memberId 찜 해제하는 회원 ID
     */
    void bookMarkServiceDelete(Long buildingId, String memberId);

    /**
     * 매물 등록 서비스
     *
     * @param request 매물 등록 요청 DTO
     * @param memberId 매물 등록자 회원 ID
     */
    void SaleRegistration(SaleRegistrationRequest request, String memberId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    /**
     * 필터 조건과 페이징 정보를 기반으로 매물 목록을 조회한다.
     *
     * @param buildingName 매물명 필터
     * @param saleType 매물 판매 유형 필터
     * @param startPrice 가격 범위 시작 필터
     * @param endPrice 가격 범위 종료 필터
     * @param propertyType 부동산 종류 필터
     * @param page 요청 페이지 번호
     * @param size 페이지당 데이터 수
     * @param memberId 로그인 회원 ID (찜 여부 체크용)
     * @return 페이징된 매물 목록 응답 DTO
     */
    FilteredResponse getBuildingList(String buildingName, String saleType, Long startPrice, Long endPrice, String propertyType, int page, int size, String memberId);

    /**
     * 매물을 삭제한다.
     *
     * @param buildingId 삭제할 매물 ID
     */
    void removeBuilding(Long buildingId);

    ViewDetailResponse BuildingDetail(Long buildingId, String memberId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    ViewDetailResponse BuildingDetailWithoutMemberId(Long buildingId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    void updateBuilding(UpdateBuilding request, String memberId);
}

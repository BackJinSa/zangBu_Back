package bjs.zangbu.building.service;
import bjs.zangbu.bookmark.mapper.BookMarkMapper;
import bjs.zangbu.building.dto.request.BuildingRequest.SaleRegistrationRequest.ComplexDetails;
import bjs.zangbu.building.dto.request.BuildingRequest.SaleRegistrationRequest.ImageDetails;
import bjs.zangbu.building.dto.response.BuildingResponse.FilteredResponse.Filtered;
import bjs.zangbu.building.filter.BuildingFilter;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.*;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.complexList.mapper.ComplexListMapper;
import bjs.zangbu.complexList.vo.ComplexList;
import bjs.zangbu.imageList.mapper.ImageListMapper;
import bjs.zangbu.imageList.vo.ImageList;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {
    // 외부 서비스 및 매퍼 주입
    private final CodefService codefService;
    private final BuildingMapper buildingMapper;
    private final BookMarkMapper bookMarkMapper;
    private final BuildingFilter buildingFilter;
    private final ComplexListMapper complexListMapper;
    private final ImageListMapper imageListMapper;

    // 특정 매물 상세 정보를 조회하는 서비스
    @Override
    public ViewDetailResponse viewDetailService(ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        // 요청된 buildingId가 DB에 존재하는지 검증
        buildingFilter.validateBuildingExists(request.getBuildingId());
        // CodefService를 통해 매물 가격 정보 JSON 데이터 조회
        String jsonResponse = codefService.priceInformation(request);
        // JSON 데이터를 ViewDetailResponse DTO로 변환
        ViewDetailResponse response = CodefConverter.parseDataToDto(jsonResponse, ViewDetailResponse.class);
        return response;
    }

    // 매물 찜하기 서비스 - 찜 추가
    @Transactional
    @Override
    public void bookMarkService(BookmarkRequest request, String memberId) {
        // building 존재 여부 검증
        buildingFilter.validateBuildingExists(request.getBuildingId());
        // 찜 정보 insert
        bookMarkMapper.insertBookMark(memberId ,request.getBuildingId());
        // 찜 수 증가
        buildingMapper.incrementBookmarkCount(request.getBuildingId());
    }

    // 매물 찜 해제 서비스 - 찜 삭제
    @Transactional
    @Override
    public void bookMarkServiceDelete(Long buildingId, String memberId) {
        // building 존재 여부 검증
        buildingFilter.validateBuildingExists(buildingId);
        // 찜 정보 삭제
        bookMarkMapper.deleteBookMark(memberId, buildingId);
    }

    // 매물 등록 서비스

    @Transactional
    @Override
    public void SaleRegistration(SaleRegistrationRequest request, String memberId) {
        // 복합 단지 정보 변환 및 DB 저장 후 생성된 complexId 반환
        ComplexList complexList = ComplexDetails.toVo(request.getComplexList());
        Long complexId = complexListMapper.createComplexList(complexList);

        // 건물 정보 변환 및 저장 후 buildingId 반환
        Building building = SaleRegistrationRequest.BuildingDetails.toVo(request.getBuilding(), complexId, memberId);
        Long buildingId = buildingMapper.createBuilding(building);

        // 이미지 정보 변환 후 저장
        ImageList imageList = ImageDetails.toVo(request.getImage(), complexId, memberId, buildingId);
        imageListMapper.createImageList(imageList);
    }

    //  매물 목록 조회 (필터링 및 페이징)
    @Transactional
    @Override
    public FilteredResponse getBuildingList(String buildingName, String saleType, Long startPrice, Long endPrice, String propertyType, int page, int size, String memberId) {
        int offset = page * size;

        // 1) 매퍼에서 바로 DTO 리스트 받기
        List<Filtered> filteredList = buildingMapper.selectFilteredBuildings(
                buildingName, saleType, startPrice, endPrice, propertyType, offset, size);

        // 2) 다음 페이지 여부
        boolean hasNext = filteredList.size() == size;

        // 3) 로그인 회원 찜한 매물 ID 목록 조회
        List<Long> bookmarkedBuildingIds;
        if (memberId != null && !memberId.isBlank()) {
            bookmarkedBuildingIds = bookMarkMapper.selectBookmarkedBuildingIdsByMember(memberId);
        } else {
            bookmarkedBuildingIds = List.of();
        }

        // 4) 찜 여부 업데이트: 새 객체 생성
        List<Filtered> updatedList = filteredList.stream()
                .map(f -> new Filtered(
                        f.getBuildingId(),
                        f.getBuildingName(),
                        f.getPrice(),
                        f.getRankAverage(),
                        bookmarkedBuildingIds.contains(f.getBuildingId())
                ))
                .collect(Collectors.toList());

        // 5) 응답 DTO로 반환
        return new FilteredResponse(updatedList, hasNext);
    }

    // 매물 삭제 서비스
    @Transactional
    @Override
    public void removeBuilding(Long buildingId) {
        // 매물 존재 여부 검증
        buildingFilter.validateBuildingExists(buildingId);
        // 매물 삭제
        buildingMapper.deleteBuilding(buildingId);
    }
}

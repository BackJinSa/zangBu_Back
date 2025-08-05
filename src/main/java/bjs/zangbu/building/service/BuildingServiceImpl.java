package bjs.zangbu.building.service;

import bjs.zangbu.bookmark.service.BookMarkService;
import bjs.zangbu.building.dto.request.BuildingRequest.SaleRegistrationRequest.ComplexDetails;
import bjs.zangbu.building.dto.request.BuildingRequest.SaleRegistrationRequest.ImageDetails;
import bjs.zangbu.building.filter.BuildingFilter;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.*;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.complexList.service.ComplexListService;
import bjs.zangbu.complexList.vo.ComplexList;
import bjs.zangbu.imageList.service.ImageListService;
import bjs.zangbu.imageList.vo.ImageList;
import bjs.zangbu.ncp.service.MultipartUploaderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.UnsupportedEncodingException;
import java.util.List;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.BUCKET_NAME;

/**
 * {@link BuildingService} 구현체.
 * 매물 조회, 찜하기, 등록, 삭제 등 매물 관련 비즈니스 로직을 처리하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final CodefService codefService;
    private final BuildingMapper buildingMapper;
    private final BuildingFilter buildingFilter;
    private final ComplexListService complexListService;
    private final ImageListService imageListService;
    private final BookMarkService bookMarkService;
    private final MultipartUploaderService multiPartUploaderService;

    /**
     * 필터링 조건에 따른 특정 매물의 상세 정보를 조회합니다.
     *
     * @param request 매물 ID를 포함하는 {@link ViewDetailRequest} DTO
     * @return CODEF API 응답을 변환한 매물 상세 정보 DTO
     * @throws UnsupportedEncodingException 인코딩 예외
     * @throws JsonProcessingException JSON 처리 예외
     * @throws InterruptedException API 호출 지연 예외
     */
    @Override
    public ViewDetailResponse viewDetailFilterService(ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        buildingFilter.validateBuildingExists(request.getBuildingId());
        String jsonResponse = codefService.FilterpriceInformation(request);
        return CodefConverter.parseDataToDto(jsonResponse, ViewDetailResponse.class);
    }

    /**
     * 매물 찜하기 서비스를 제공합니다.
     * 매물 존재 여부를 검증한 후, 찜을 추가하고 해당 매물의 찜 수를 1 증가시킵니다.
     *
     * @param request 찜 요청 DTO
     * @param memberId 찜을 수행하는 회원 ID
     */
    @Transactional
    @Override
    public void bookMarkService(BookmarkRequest request, String memberId) {
        buildingFilter.validateBuildingExists(request.getBuildingId());
        bookMarkService.insertBookMark(memberId, request.getBuildingId());
        buildingMapper.incrementBookmarkCount(request.getBuildingId());
    }

    /**
     * 매물 찜 해제 서비스를 제공합니다.
     * 매물 존재 여부를 검증한 후, 찜을 삭제합니다.
     *
     * @param buildingId 찜 해제할 매물 ID
     * @param memberId 찜을 해제하는 회원 ID
     */
    @Transactional
    @Override
    public void bookMarkServiceDelete(Long buildingId, String memberId) {
        buildingFilter.validateBuildingExists(buildingId);
        bookMarkService.deleteBookMark(memberId, buildingId);
    }

    /**
     * 매물 등록 서비스를 제공합니다.
     * 요청 DTO를 기반으로 복합 단지 정보, 건물 정보, 이미지 정보를 저장합니다.
     *
     * @param request 매물 등록 요청 DTO
     * @param memberId 매물 등록자 회원 ID
     */
    @Transactional
    @Override
    public void SaleRegistration(SaleRegistrationRequest request, String memberId) {
        ComplexList complexList = ComplexDetails.toVo(request.getComplexList());
        Long complexId = complexListService.createComplexList(complexList);

        Building building = SaleRegistrationRequest.BuildingDetails.toVo(request.getBuilding(), complexId, memberId);
        Long buildingId = buildingMapper.createBuilding(building);

        String fileExtension = "";
        String imageUrl = null;

        MultipartFile multipartFile = request.getImage().getImageFile();
        fileExtension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        try {
            if (fileExtension.equals("jpeg")) { // jpeg 파일만 허용하는 로직
                imageUrl = multiPartUploaderService.multipartUpload(
                        BUCKET_NAME,
                        "/image/" + multipartFile.getOriginalFilename(),
                        multipartFile
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드에 실패했습니다.");
        }
        ImageList imageList = ImageDetails.toVo(imageUrl, complexId, memberId, buildingId);
        imageListService.createImageList(imageList);
    }

    /**
     * 필터링 조건과 페이징 정보를 기반으로 매물 목록을 조회합니다.
     *
     * @param buildingName 매물명 필터
     * @param saleType 매물 판매 유형 필터
     * @param startPrice 가격 범위 시작 필터
     * @param endPrice 가격 범위 종료 필터
     * @param propertyType 부동산 종류 필터
     * @param page 요청 페이지 번호
     * @param size 페이지당 데이터 수
     * @param memberId 로그인 회원 ID (찜 여부 확인용)
     * @return 페이징 처리된 매물 목록이 담긴 {@link FilteredResponse} DTO
     */
    @Transactional
    @Override
    public FilteredResponse getBuildingList(
            String buildingName, String saleType, Long startPrice, Long endPrice,
            String propertyType, int page, int size, String memberId) {

        PageHelper.startPage(page, size);

        List<FilteredResponse.Filtered> filteredList = buildingMapper.selectFilteredBuildings(
                buildingName, saleType, startPrice, endPrice, propertyType);

        List<Long> bookmarkedBuildingIds = (memberId != null && !memberId.isBlank())
                ? bookMarkService.selectBookmarkedBuildingIdsByMember(memberId)
                : List.of();

        List<FilteredResponse.Filtered> updatedList = filteredList.stream()
                .map(f -> new FilteredResponse.Filtered(
                        f.getBuildingId(),
                        f.getBuildingName(),
                        f.getPrice(),
                        f.getRankAverage(),
                        bookmarkedBuildingIds.contains(f.getBuildingId())
                ))
                .toList();

        PageInfo<FilteredResponse.Filtered> pageInfo = new PageInfo<>(updatedList);

        return FilteredResponse.toDto(pageInfo);
    }

    /**
     * 매물을 삭제합니다.
     * 매물 존재 여부 검증 후 삭제를 처리합니다.
     *
     * @param buildingId 삭제할 매물 ID
     */
    @Transactional
    @Override
    public void removeBuilding(Long buildingId) {
        buildingFilter.validateBuildingExists(buildingId);
        buildingMapper.deleteBuilding(buildingId);
    }

    /**
     * 매물 ID로 상세 정보를 조회합니다.
     *
     * @param buildingId 상세 정보를 조회할 매물 ID
     * @return CODEF API 응답을 변환한 매물 상세 정보 DTO
     * @throws UnsupportedEncodingException 인코딩 예외
     * @throws JsonProcessingException JSON 처리 예외
     * @throws InterruptedException API 호출 지연 예외
     */
    @Override
    public ViewDetailResponse viewDetailService(Long buildingId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        buildingFilter.validateBuildingExists(buildingId);
        String jsonResponse = codefService.priceInformation(buildingId);
        return CodefConverter.parseDataToDto(jsonResponse, ViewDetailResponse.class);
    }
}
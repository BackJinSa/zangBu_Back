package bjs.zangbu.building.service;

import bjs.zangbu.bookmark.service.BookMarkService;
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
import bjs.zangbu.notification.service.NotificationService;
import bjs.zangbu.review.service.ReviewService;
import bjs.zangbu.review.vo.ReviewListResponseVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.BUCKET_NAME;

/**
 * {@link BuildingService} 구현체.
 * 매물 조회, 찜하기, 등록, 삭제 등 매물 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class BuildingServiceImpl implements BuildingService {

    private final CodefService codefService;
    private final BuildingMapper buildingMapper;
    private final BuildingFilter buildingFilter;
    private final ComplexListService complexListService;
    private final ImageListService imageListService;
    private final BookMarkService bookMarkService;
    private final MultipartUploaderService multiPartUploaderService;
    private final ReviewService reviewService;
    private final NotificationService notificationService;

    /**
     * 필터링 조건에 따른 특정 매물의 상세 정보를 조회합니다.
     * CODEF API 응답과 DB 정보를 결합하여 최종 응답을 생성합니다.
     *
     * @param request 매물 ID를 포함하는 {@link ViewDetailRequest} DTO
     * @param memberId 현재 로그인한 회원 ID
     * @return CODEF API 응답과 DB 정보를 결합한 매물 상세 정보 DTO
     */
    /**
     * 매물을 찜 목록에 추가하고, 찜 수를 1 증가시킵니다.
     *
     * @param request  찜 요청 DTO
     * @param memberId 찜을 수행하는 회원 ID
     */
    @Transactional
    @Override
    public void bookMarkService(BookmarkRequest request, String memberId) {
        buildingFilter.validateBuildingExists(request.getBuildingId());
        Integer price = buildingMapper.selectCurrentPrice(request.getBuildingId());
        Long complexId = complexListService.getComplexIdByBuildingId(request.getBuildingId());
        bookMarkService.insertBookMark(memberId, request.getBuildingId(), complexId, price);
        buildingMapper.incrementBookmarkCount(request.getBuildingId());
    }

    /**
     * 매물 찜을 해제하고, 해당 매물의 찜 수를 1 감소시킵니다.
     *
     * @param buildingId 찜 해제할 매물 ID
     * @param memberId   찜을 해제하는 회원 ID
     */
    @Transactional
    @Override
    public void bookMarkServiceDelete(Long buildingId, String memberId) {
        buildingFilter.validateBuildingExists(buildingId);
        bookMarkService.deleteBookMark(memberId, buildingId);
        buildingMapper.decrementBookmarkCount(buildingId);
    }

    /**
     * 매물 등록 서비스를 제공합니다.
     * 요청 DTO를 기반으로 복합 단지 정보, 건물 정보, 이미지 정보를 저장합니다.
     *
     * @param request  매물 등록 요청 DTO
     * @param memberId 매물 등록자 회원 ID
     */
    @Override
    @Transactional
    public void SaleRegistration(SaleRegistrationRequest request, String memberId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        String codefResponse1 = codefService.realEstateRegistrationAddressSearch(request);
        System.out.println(codefResponse1);
        // CODEF 응답 JSON에서 'data' 필드를 Map으로 파싱
        Map<String, Object> dataMap = CodefConverter.parseDataToDto(codefResponse1, Map.class);
        System.out.println(dataMap);
        // Map에서 "commUniqueNo" 값을 추출
        String authenticity = (String) dataMap.get("commUniqueNo");
        System.out.println(authenticity);
        String codefResponse2 = codefService.RealEstateRegistrationRegister(authenticity, request.getIdentity());
        System.out.println(codefResponse2);
        Map<String, Object> dataMap2 = CodefConverter.parseDataToDto(codefResponse2, Map.class);
        System.out.println(dataMap2);
        String resMatchYN = (String) dataMap2.get("resMatchYN");
        System.out.println(resMatchYN);
        if(!resMatchYN.equals("1")) {
            return;
        }
        // complex 저장
        ComplexList complex = ComplexDetails.toVo(request.getComplexList());
        Long complexId = complexListService.createComplexList(complex);

        // building 저장
        Building building = BuildingDetails.toVo(request.getBuilding(), complexId, memberId);
        buildingMapper.createBuilding(building);
        Long buildingId = buildingMapper.selectLastInsertId();

        // 3) DTO에서 MultipartFile 리스트 추출 → 각 파일을 업로드하고 URL 리스트 생성
// 3) DTO에서 MultipartFile 리스트 추출 → 각 파일을 업로드하고 URL 리스트 생성
        List<MultipartFile> imageFiles = request.getImage().getImages().stream()
                .map(ImageDetails::getImageFile) // 각 ImageDetails 객체에서 MultipartFile 추출
                .toList();
        List<String> imageUrls = imageFiles.stream()
                .map(multipartFile -> {
                    try {
                        // 원본 파일명에서 확장자를 추출
                        String originalFilename = multipartFile.getOriginalFilename();
                        String fileExtension = "";
                        int dotIndex = originalFilename.lastIndexOf('.');
                        if (dotIndex > 0) {
                            fileExtension = originalFilename.substring(dotIndex);
                        }

                        // 새로운 파일명 생성: "memberId" + "UUID" + "확장자"
                        String newFilename = memberId + "-" + UUID.randomUUID().toString() + fileExtension;

                        // 새로운 파일명을 사용하여 업로드
                        return multiPartUploaderService.multipartUpload(
                                BUCKET_NAME, "image/" + newFilename, multipartFile
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
                    }
                }).toList();

        log.info("이미지 URL 리스트: {}", imageUrls);

        // 4) image_list INSERT (List<String> 형태로 저장)
        for (String imageUrl : imageUrls) {
            ImageList imageList = ImageDetails.toVo(imageUrl, complexId, memberId, buildingId);
            imageListService.createImageList(imageList);
        }
    }

    /**
     * 필터링 조건과 페이징 정보를 기반으로 매물 목록을 조회합니다.
     *
     * @param buildingName 매물명 필터
     * @param saleType     매물 판매 유형 필터
     * @param startPrice   가격 범위 시작 필터
     * @param endPrice     가격 범위 종료 필터
     * @param propertyType 부동산 종류 필터
     * @param page         요청 페이지 번호
     * @param size         페이지당 데이터 수
     * @param memberId     로그인 회원 ID (찜 여부 확인용)
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
     * @throws JsonProcessingException      JSON 처리 예외
     * @throws InterruptedException         API 호출 지연 예외
     */
    @Override
    public ViewDetailResponse BuildingDetail(Long buildingId, String memberId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        buildingFilter.validateBuildingExists(buildingId);
        Building building = buildingMapper.getBuildingById(buildingId);
        List<String> imageUrl = imageListService.getBuildingImageUrll(buildingId);
        boolean isBookmarked = bookMarkService.isBookmarked(buildingId, memberId);
        String jsonResponse = codefService.getBuildingDetail(buildingId);
        List<ReviewListResponseVO> reviewListResponse = reviewService.getRecentReviews(buildingId, 3);
        ViewDetailResponse codefData = CodefConverter.parseDataToDto(jsonResponse, ViewDetailResponse.class);
        return ViewDetailResponse.toDto(codefData, building, imageUrl, isBookmarked, reviewListResponse);
    }

    @Override
    public ViewDetailResponse BuildingDetailWithoutMemberId(Long buildingId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        buildingFilter.validateBuildingExists(buildingId);
        Building building = buildingMapper.getBuildingById(buildingId);
        System.out.println(building);
        List<String> imageUrl = imageListService.getBuildingImageUrll(buildingId);
        String jsonResponse = codefService.getBuildingDetail(buildingId);
        List<ReviewListResponseVO> reviewListResponse = reviewService.getRecentReviews(buildingId, 3);
        ViewDetailResponse codefData = CodefConverter.parseDataToDto(jsonResponse, ViewDetailResponse.class);
        return ViewDetailResponse.toDto(codefData, building, imageUrl, false, reviewListResponse);
    }

    @Override
    public void updateBuilding(UpdateBuilding request, String memberId) {
        buildingMapper.updateBuilding(request, memberId);
        notificationService.detectPriceChangeForAllBookmarks();
    }
}
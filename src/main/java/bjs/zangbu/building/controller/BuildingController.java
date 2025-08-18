package bjs.zangbu.building.controller;

import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.FilteredResponse;
import bjs.zangbu.building.dto.response.BuildingResponse.ViewDetailResponse;
import bjs.zangbu.building.service.BuildingService;
import bjs.zangbu.security.account.vo.CustomUser;
import bjs.zangbu.publicdata.service.aptinfo.AptIdInfoService;
import bjs.zangbu.publicdata.service.aptlist.AptListService;
import bjs.zangbu.publicdata.service.managecost.publicuse.PublicUseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import bjs.zangbu.building.dto.response.BuildingResponse.BuildingDetailWithPublicDataResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/building")
@Api(tags = "Building API", description = "건물 매물 관련 기능 API")
public class BuildingController {

  private final BuildingService buildingService;
  private final AptIdInfoService aptIdInfoService;
  private final AptListService aptListService;
  private final PublicUseService publicUseService;

  /**
   * 특정 조건에 맞는 매물 리스트 조회
   *
   * @param buildingName 건물명 (옵션)
   * @param saleType     매물 판매 유형 (옵션)
   * @param startPrice   가격 시작 범위 (옵션)
   * @param endPrice     가격 종료 범위 (옵션)
   * @param propertyType 부동산 종류 (옵션)
   * @param page         페이지 번호 (기본값 1)
   * @param size         페이지당 데이터 수 (기본값 10)
   * @param user         인증된 사용자 정보
   * @return 필터링된 매물 리스트 및 페이징 정보가 담긴 FilteredResponse 객체
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @GetMapping("/list")
  @ApiOperation(value = "필터링된 매물 리스트 조회", notes = "특정 조건에 맞는 매물 목록 조회")
  public ResponseEntity<?> getBuildingList(
      @RequestParam(value = "buildingName", required = false) String buildingName,
      @RequestParam(value = "saleType", required = false) String saleType,
      @RequestParam(value = "startPrice", required = false) Long startPrice,
      @RequestParam(value = "endPrice", required = false) Long endPrice,
      @RequestParam(value = "propertyType", required = false) String propertyType,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @ApiIgnore @AuthenticationPrincipal CustomUser user) {
    if (user == null || user.getMember().getMemberId() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    String memberId = user.getMember().getMemberId();
    FilteredResponse pageInfo = buildingService.getBuildingList(
        buildingName, saleType, startPrice, endPrice, propertyType, page, size, memberId);
    return ResponseEntity.ok(pageInfo);
  }

  /**
   * 매물 ID로 매물 상세 정보 조회
   *
   * @param buildingId 조회할 매물의 ID
   * @param user       인증된 사용자 정보
   * @return 매물 상세 정보를 담은 ViewDetailResponse 객체
   * @throws UnsupportedEncodingException 인코딩 관련 예외 발생 시
   * @throws JsonProcessingException      JSON 처리 중 예외 발생 시
   * @throws InterruptedException         처리 중 인터럽트 발생 시
   * @throws ResponseStatusException      로그인 정보가 없으면 401 에러 발생
   */
  @GetMapping("/{buildingId}")
  @ApiOperation(value = "매물 상세 정보 조회", notes = "매물 ID로 상세 정보 조회")
  public ResponseEntity<?> BuildingDetail(
      @PathVariable("buildingId") Long buildingId,
      @ApiIgnore @AuthenticationPrincipal CustomUser user)
      throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    if (user == null || user.getMember().getMemberId() == null) {
      ViewDetailResponse response1 = buildingService.BuildingDetailWithoutMemberId(buildingId);
      return ResponseEntity.ok(response1);
    }
    ViewDetailResponse response2 = buildingService.BuildingDetail(buildingId, user.getMember().getMemberId());
    return ResponseEntity.ok(response2);
  }

  /**
   * 매물 신규 등록
   *
   * @param building    등록할 건물 정보
   * @param complexList 건물 단지 정보
   * @param imageFiles  등록할 이미지 파일 (필수)
   * @param identity    사용자 신원 확인 정보
   * @param user        인증된 사용자 정보
   * @return 성공 메시지와 HTTP 201 상태코드 반환
   * @throws UnsupportedEncodingException 인코딩 관련 예외 발생 시
   * @throws JsonProcessingException      JSON 처리 중 예외 발생 시
   * @throws InterruptedException         처리 중 인터럽트 발생 시
   * @throws ResponseStatusException      로그인 정보가 없으면 401 에러 발생
   */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiOperation(value = "매물 등록", notes = "로그인한 사용자가 매물 신규 등록")
  public ResponseEntity<?> saleRegistration(
      @RequestPart("building") BuildingDetails building,
      @RequestPart("complexList") ComplexDetails complexList,
      @RequestPart(value = "imageFile", required = true) List<MultipartFile> imageFiles,
      @RequestPart("identity") String identity,
      @ApiIgnore @AuthenticationPrincipal CustomUser user)
      throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    if (user == null || user.getMember().getMemberId() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }

    System.out.println(identity);
    String memberId = user.getMember().getMemberId();
    ImageDetailsList imageList = new ImageDetailsList();
    imageList.setImages(
        imageFiles.stream()
            .map(f -> new ImageDetails(f))
            .collect(Collectors.toList()));
    SaleRegistrationRequest request = new SaleRegistrationRequest(building, complexList, imageList, identity);
    buildingService.SaleRegistration(request, memberId);
    return ResponseEntity.status(HttpStatus.CREATED).body("매물이 성공적으로 등록되었습니다.");
  }

  /**
   * 매물 삭제
   *
   * @param buildingId 삭제할 매물의 ID
   * @param user       인증된 사용자 정보
   * @return 성공 메시지와 HTTP 200 상태코드 반환
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @DeleteMapping("/remove/{buildingId}")
  @ApiOperation(value = "매물 삭제", notes = "매물 ID로 삭제")
  public ResponseEntity<?> removeBuilding(
      @PathVariable("buildingId") Long buildingId,
      @ApiIgnore @AuthenticationPrincipal CustomUser user) {
    if (user == null || user.getMember().getMemberId() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    buildingService.removeBuilding(buildingId);
    return ResponseEntity.status(HttpStatus.OK).body("매물이 성공적으로 삭제되었습니다.");
  }

  /**
   * 매물 찜하기
   *
   * @param request 찜하기 요청 정보가 담긴 BookmarkRequest 객체
   * @param user    인증된 사용자 정보
   * @return 성공 메시지와 HTTP 200 상태코드 반환
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @PostMapping("/bookmark")
  @ApiOperation(value = "매물 찜하기", notes = "로그인한 사용자가 매물을 찜 목록에 추가")
  public ResponseEntity<?> bookMark(
      @RequestBody BookmarkRequest request,
      @ApiIgnore @AuthenticationPrincipal CustomUser user) {
    if (user == null || user.getMember().getMemberId() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    String memberId = user.getMember().getMemberId();
    buildingService.bookMarkService(request, memberId);
    return ResponseEntity.status(HttpStatus.OK).body("매물 찜하기에 성공했습니다.");
  }

  /**
   * 매물 찜하기 취소
   *
   * @param buildingId 찜 취소할 매물 ID
   * @param user       인증된 사용자 정보
   * @return 성공 메시지와 HTTP 200 상태코드 반환
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @DeleteMapping("/bookmark/{buildingId}")
  @ApiOperation(value = "매물 찜하기 취소", notes = "찜한 매물을 목록에서 해제")
  public ResponseEntity<?> bookMarkDelete(
      @PathVariable("buildingId") Long buildingId,
      @ApiIgnore @AuthenticationPrincipal CustomUser user) {
    if (user == null || user.getMember().getMemberId() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    String memberId = user.getMember().getMemberId();
    buildingService.bookMarkServiceDelete(buildingId, memberId);
    return ResponseEntity.status(HttpStatus.OK).body("매물 찜하기 취소에 성공했습니다.");
  }

  @PatchMapping("/update")
  @ApiOperation(value = "매물 수정", notes = "등록된 특정 매물 값 수정")
  public ResponseEntity<?> updateBuilding(
      @RequestBody UpdateBuilding request,
      @ApiIgnore @AuthenticationPrincipal CustomUser user) {
    if (user == null || user.getMember().getMemberId() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    String memberId = user.getMember().getMemberId();
    buildingService.updateBuilding(request, memberId);
    return ResponseEntity.status(HttpStatus.OK).body(request.getBuildingId());
  }

  /**
   * 매물 상세 정보 + 공공데이터 통합 조회
   * 사진의 매물 정보에 표시되는 모든 정보를 포함
   *
   * @param buildingId 조회할 매물의 ID
   * @param user       인증된 사용자 정보
   * @return 매물 상세 정보와 공공데이터가 통합된 응답
   */
  @GetMapping("/{buildingId}/detail-with-publicdata")
  @ApiOperation(value = "매물 상세 정보 + 공공데이터 통합 조회", notes = "매물 기본 정보와 공공데이터를 통합하여 제공")
  public ResponseEntity<?> getBuildingDetailWithPublicData(
      @PathVariable("buildingId") Long buildingId,
      @ApiIgnore @AuthenticationPrincipal CustomUser user)
      throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {

    // 1. 기본 매물 정보 조회
    ViewDetailResponse buildingDetail;
    if (user == null || user.getMember().getMemberId() == null) {
      buildingDetail = buildingService.BuildingDetailWithoutMemberId(buildingId);
    } else {
      buildingDetail = buildingService.BuildingDetail(buildingId, user.getMember().getMemberId());
    }

    // 2. 공공데이터 정보 조회 (주소 기반)
    try {
      // 도로명 주소 또는 지번 주소에서 시도 정보 추출
      String address = buildingDetail.getCommAddrRoadName() != null ? buildingDetail.getCommAddrRoadName()
          : buildingDetail.getCommAddrLotNumber();

      String sidoName = extractSidoFromAddress(address);

      // 아파트 단지 정보 조회
      List<Object> aptComplexInfo = (List<Object>) (List<?>) aptListService.getSidoAptList(sidoName, 1, 10);

      // 응답 데이터 구성
      BuildingDetailWithPublicDataResponse response = BuildingDetailWithPublicDataResponse.builder()
          .buildingDetail(buildingDetail)
          .aptComplexInfo(aptComplexInfo)
          .publicDataAvailable(true)
          .build();

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      // 공공데이터 조회 실패 시에도 기본 매물 정보는 반환
      BuildingDetailWithPublicDataResponse response = BuildingDetailWithPublicDataResponse.builder()
          .buildingDetail(buildingDetail)
          .publicDataAvailable(false)
          .errorMessage("공공데이터 조회 중 오류가 발생했습니다: " + e.getMessage())
          .build();

      return ResponseEntity.ok(response);
    }
  }

  /**
   * 주소에서 시도명 추출
   */
  private String extractSidoFromAddress(String address) {
    if (address == null || address.isEmpty()) {
      return "서울"; // 기본값
    }

    // 주소에서 시도명 추출 로직
    if (address.contains("서울특별시"))
      return "서울";
    if (address.contains("부산광역시"))
      return "부산";
    if (address.contains("대구광역시"))
      return "대구";
    if (address.contains("인천광역시"))
      return "인천";
    if (address.contains("광주광역시"))
      return "광주";
    if (address.contains("대전광역시"))
      return "대전";
    if (address.contains("울산광역시"))
      return "울산";
    if (address.contains("세종특별자치시"))
      return "세종";
    if (address.contains("경기도"))
      return "경기";
    if (address.contains("강원도"))
      return "강원";
    if (address.contains("충청북도"))
      return "충북";
    if (address.contains("충청남도"))
      return "충남";
    if (address.contains("전라북도"))
      return "전북";
    if (address.contains("전라남도"))
      return "전남";
    if (address.contains("경상북도"))
      return "경북";
    if (address.contains("경상남도"))
      return "경남";
    if (address.contains("제주특별자치도"))
      return "제주";

    return "서울"; // 기본값
  }
}
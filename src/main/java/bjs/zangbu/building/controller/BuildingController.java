package bjs.zangbu.building.controller;

import bjs.zangbu.building.dto.request.BuildingRequest.BookmarkRequest;
import bjs.zangbu.building.dto.request.BuildingRequest.BuildingDetails;
import bjs.zangbu.building.dto.request.BuildingRequest.ComplexDetails;
import bjs.zangbu.building.dto.request.BuildingRequest.ImageDetails;
import bjs.zangbu.building.dto.request.BuildingRequest.SaleRegistrationRequest;
import bjs.zangbu.building.dto.request.BuildingRequest.ViewDetailRequest;
import bjs.zangbu.building.dto.response.BuildingResponse.FilteredResponse;
import bjs.zangbu.building.dto.response.BuildingResponse.ViewDetailResponse;
import bjs.zangbu.building.service.BuildingService;
import bjs.zangbu.security.account.vo.CustomUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Building 관련 HTTP 요청을 처리하는 REST 컨트롤러. 매물 상세 조회, 찜하기, 찜 해제, 등록, 목록 조회, 삭제 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/building")
@Api(tags = "Building API", description = "건물 매물 관련 기능 API")
public class BuildingController {

  private final BuildingService buildingService;

  /**
   * 매물 상세 조회 요청 처리. {@code POST /building} 엔드포인트를 통해 매물의 상세 정보를 조회합니다. 이 API는 특정 필터링 조건에 따라 조회된 매물에
   * 대한 상세 정보를 제공합니다.
   *
   * @param request 매물 ID를 포함하는 {@link ViewDetailRequest} DTO
   * @return 매물 상세 정보가 담긴 {@link ViewDetailResponse}와 함께 200 OK 응답
   * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
   * @throws JsonProcessingException      JSON 처리 중 발생하는 예외
   * @throws InterruptedException         API 호출 지연 시 발생하는 예외
   */
  @ApiOperation(
      value = "매물 필터링 상세보기",
      notes = "필터링된 매물에 대한 상세정보를 조회합니다.",
      response = ViewDetailResponse.class
  )
  @ApiResponses({
      @ApiResponse(code = 200, message = "매물 상세정보 조회 성공"),
      @ApiResponse(code = 400, message = "매물 상세정보 조회 실패"),
      @ApiResponse(code = 500, message = "서버 오류로 인한 매물 상세정보 조회 실패")
  })
  @PostMapping("")
  public ResponseEntity<?> viewDetail(
      @ApiParam(value = "매물 상세 조회 요청 DTO", required = true)
      @RequestBody ViewDetailRequest request)
      throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    ViewDetailResponse response = buildingService.viewDetailFilterService(request);
    return ResponseEntity.ok(response);
  }

  /**
   * 매물 목록 조회 요청 처리 (필터링 및 페이징 지원). {@code GET /building/list} 엔드포인트를 통해 특정 조건에 맞는 매물 목록을 페이징하여
   * 조회합니다.
   *
   * @param buildingName 매물명 필터 (선택적)
   * @param saleType     판매 유형 필터 (선택적)
   * @param startPrice   가격 범위 시작 필터 (선택적)
   * @param endPrice     가격 범위 종료 필터 (선택적)
   * @param propertyType 부동산 종류 필터 (선택적)
   * @param page         요청 페이지 번호 (기본값: 1)
   * @param size         페이지당 데이터 수 (기본값: 10)
   * @param userDetails  인증된 사용자 정보 (찜 여부 반영용)
   * @return 페이징된 매물 목록이 담긴 {@link FilteredResponse} DTO와 함께 200 OK 응답
   */
  @ApiOperation(
      value = "필터링된 매물 리스트 조회",
      notes = "특정 조건에 따라 필터링된 매물 목록을 조회합니다.",
      response = FilteredResponse.class
  )
  @ApiResponses({
      @ApiResponse(code = 200, message = "필터링된 매물 목록 조회 성공"),
      @ApiResponse(code = 400, message = "필터링된 매물 목록 조회 실패"),
      @ApiResponse(code = 500, message = "서버 오류로 인한 필터링된 매물 목록 조회 실패")
  })
  @GetMapping("/list")
  public ResponseEntity<?> getBuildingList(
      @ApiParam(value = "매물명 필터", example = "강남빌딩")
      @RequestParam(value = "buildingName", required = false) String buildingName,
      @ApiParam(value = "판매 유형 필터", example = "sale")
      @RequestParam(value = "saleType", required = false) String saleType,
      @ApiParam(value = "가격 범위 시작", example = "100000000")
      @RequestParam(value = "startPrice", required = false) Long startPrice,
      @ApiParam(value = "가격 범위 종료", example = "500000000")
      @RequestParam(value = "endPrice", required = false) Long endPrice,
      @ApiParam(value = "부동산 종류 필터", example = "apartment")
      @RequestParam(value = "propertyType", required = false) String propertyType,
      @ApiParam(value = "페이지 번호", example = "1")
      @RequestParam(value = "page", defaultValue = "1") int page,
      @ApiParam(value = "페이지당 항목 수", example = "10")
      @RequestParam(value = "size", defaultValue = "10") int size,
      @ApiIgnore
      @AuthenticationPrincipal UserDetails userDetails) {

    String memberId = (userDetails != null && !userDetails.getUsername().isBlank())
        ? userDetails.getUsername() : null;

    FilteredResponse pageInfo = buildingService.getBuildingList(buildingName, saleType, startPrice,
        endPrice, propertyType, page, size, memberId);
    return ResponseEntity.ok(pageInfo);
  }

  /**
   * 매물 상세 정보 조회 요청 처리. {@code GET /building/{buildingId}} 엔드포인트를 통해 매물 ID로 상세 정보를 조회합니다.
   *
   * @param buildingId 상세 정보를 조회할 매물 ID
   * @return 매물 상세 정보가 담긴 {@link ViewDetailResponse}와 함께 200 OK 응답
   * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
   * @throws JsonProcessingException      JSON 처리 중 발생하는 예외
   * @throws InterruptedException         API 호출 지연 시 발생하는 예외
   */
  @ApiOperation(
      value = "매물 상세 정보 조회",
      notes = "매물 ID를 기반으로 매물의 상세 정보를 조회합니다.",
      response = ViewDetailResponse.class
  )
  @ApiResponses({
      @ApiResponse(code = 200, message = "매물 상세정보 조회 성공"),
      @ApiResponse(code = 400, message = "매물 상세정보 조회 실패"),
      @ApiResponse(code = 500, message = "서버 오류로 인한 매물 상세정보 조회 실패")
  })
  @GetMapping("/building/{buildingId}")
  public ResponseEntity<?> getBuilding(
      @ApiParam(value = "매물 ID", example = "100")
      @PathVariable("buildingId") Long buildingId)
      throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    ViewDetailResponse response = buildingService.viewDetailService(buildingId);
    return ResponseEntity.ok(response);
  }


  /**
   * 매물 등록 요청 처리. {@code POST /building/upload} 엔드포인트를 통해 매물을 신규 등록합니다.
   *
   * @param building 매물 등록 정보를 담고 있는 {@link SaleRegistrationRequest} DTO
   * @param user     인증된 사용자 정보
   * @return 200 OK 응답 (본문 없음)
   * @throws ResponseStatusException 인증되지 않은 사용자가 요청할 경우 401 Unauthorized 에러 반환
   */
  @ApiOperation(
      value = "매물 등록",
      notes = "로그인한 사용자가 매물을 신규 등록합니다."
  )
  @ApiResponses({
      @ApiResponse(code = 201, message = "매물 등록 성공"),
      @ApiResponse(code = 400, message = "매물 등록 실패"),
      @ApiResponse(code = 401, message = "로그인한 사용자만 매물 등록 가능")
  })
  @PostMapping(value = "/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<?> saleRegistration(
      @RequestPart("building") BuildingDetails building,
      @RequestPart("complexList") ComplexDetails complexList,
      @RequestPart(value = "imageFile", required = true) MultipartFile imageFile,
      @AuthenticationPrincipal CustomUser user
  ) {
    if (user == null || user.getUsername().isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인을 한 사용자만 매물 등록 가능합니다.");
    }
    String memberId = user.getMember().getMemberId();
    ImageDetails image =
        (imageFile != null ? new ImageDetails(imageFile) : null);

    SaleRegistrationRequest request =
        new SaleRegistrationRequest(building, complexList, image);

    buildingService.SaleRegistration(request, memberId);
    return ResponseEntity.status(HttpStatus.CREATED).body("매물이 성공적으로 등록되었습니다.");
  }


  /**
   * 매물 삭제 요청 처리. {@code DELETE /building/remove/{buildingId}} 엔드포인트를 통해 특정 매물을 삭제합니다.
   *
   * @param buildingId 삭제할 매물 ID
   * @return 200 OK 응답 (본문 없음)
   */
  @ApiOperation(
      value = "매물 삭제",
      notes = "매물 ID를 기반으로 특정 매물을 삭제합니다."
  )
  @ApiResponses({
      @ApiResponse(code = 200, message = "매물 삭제 성공"),
      @ApiResponse(code = 400, message = "매물 삭제 실패"),
      @ApiResponse(code = 500, message = "서버 오류로 인한 매물 삭제 실패")
  })
  @DeleteMapping("/remove/{buildingId}")
  public ResponseEntity<?> removeBuilding(
      @ApiParam(value = "매물 ID", example = "100")
      @PathVariable("buildingId") Long buildingId) {
    buildingService.removeBuilding(buildingId);
    return ResponseEntity.status(HttpStatus.OK).body("매물이 성공적으로 삭제되었습니다.");
  }


  /**
   * 매물 찜하기 요청 처리. {@code POST /building/bookmark} 엔드포인트를 통해 특정 회원이 특정 매물을 찜합니다.
   *
   * @param request     찜할 매물 ID를 포함하는 {@link BookmarkRequest} DTO
   * @param userDetails 인증된 사용자 정보
   * @return 200 OK 응답 (본문 없음)
   */
  @ApiOperation(
      value = "매물 찜하기",
      notes = "로그인한 사용자가 특정 매물을 찜 목록에 추가합니다.",
      response = BookmarkRequest.class
  )
  @ApiResponses({
      @ApiResponse(code = 200, message = "매물 찜하기 성공"),
      @ApiResponse(code = 400, message = "매물 찜하기 실패"),
      @ApiResponse(code = 401, message = "로그인 정보 없음"),
      @ApiResponse(code = 500, message = "서버 오류로 인한 매물 찜하기 실패")
  })
  @PostMapping("/bookmark")
  public ResponseEntity<?> bookMark(
      @ApiParam(value = "찜하기 요청 DTO", required = true)
      @RequestBody BookmarkRequest request,
      @ApiIgnore
      @AuthenticationPrincipal UserDetails userDetails) {
    String memberId = userDetails.getUsername();
    buildingService.bookMarkService(request, memberId);
    return ResponseEntity.status(HttpStatus.OK).body("매물 찜하기에 성공했습니다.");
  }


  /**
   * 매물 찜 해제 요청 처리. {@code DELETE /building/bookmark/{buildingId}} 엔드포인트를 통해 찜한 매물을 해제합니다.
   *
   * @param buildingId  찜 해제할 매물 ID
   * @param userDetails 인증된 사용자 정보
   * @return 200 OK 응답 (본문 없음)
   */
  @ApiOperation(
      value = "매물 찜하기 취소",
      notes = "로그인한 사용자가 찜한 매물을 찜 목록에서 해제합니다."
  )
  @ApiResponses({
      @ApiResponse(code = 200, message = "매물 찜하기 취소 성공"),
      @ApiResponse(code = 400, message = "매물 찜하기 취소 실패"),
      @ApiResponse(code = 500, message = "서버 오류로 인한 찜하기 취소 실패")
  })
  @DeleteMapping("/bookmark/{buildingId}")
  public ResponseEntity<?> bookMarkDelete(
      @ApiParam(value = "매물 ID", example = "100")
      @PathVariable("buildingId") Long buildingId,
      @ApiIgnore
      @AuthenticationPrincipal UserDetails userDetails) {
    String memberId = userDetails.getUsername();
    buildingService.bookMarkServiceDelete(buildingId, memberId);
    return ResponseEntity.status(HttpStatus.OK).body("매물 찜하기 취소에 성공했습니다.");
  }

}
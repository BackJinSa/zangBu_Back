package bjs.zangbu.building.controller;

import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.FilteredResponse;
import bjs.zangbu.building.dto.response.BuildingResponse.ViewDetailResponse;
import bjs.zangbu.building.service.BuildingService;
import bjs.zangbu.security.account.vo.CustomUser;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/building")
@Api(tags = "Building API", description = "건물 매물 관련 기능 API")
public class BuildingController {

  private final BuildingService buildingService;

  /**
   * 특정 조건에 맞는 매물 리스트 조회
   *
   * @param buildingName 건물명 (옵션)
   * @param saleType 매물 판매 유형 (옵션)
   * @param startPrice 가격 시작 범위 (옵션)
   * @param endPrice 가격 종료 범위 (옵션)
   * @param propertyType 부동산 종류 (옵션)
   * @param page 페이지 번호 (기본값 1)
   * @param size 페이지당 데이터 수 (기본값 10)
   * @param user 인증된 사용자 정보
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
          @ApiIgnore @AuthenticationPrincipal CustomUser user
  ) {
    if (user == null || user.getMember() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    String memberId = user.getMember().getMemberId();
    FilteredResponse pageInfo = buildingService.getBuildingList(
            buildingName, saleType, startPrice, endPrice, propertyType, page, size, memberId
    );
    return ResponseEntity.ok(pageInfo);
  }

  /**
   * 매물 ID로 매물 상세 정보 조회
   *
   * @param buildingId 조회할 매물의 ID
   * @param user 인증된 사용자 정보
   * @return 매물 상세 정보를 담은 ViewDetailResponse 객체
   * @throws UnsupportedEncodingException 인코딩 관련 예외 발생 시
   * @throws JsonProcessingException JSON 처리 중 예외 발생 시
   * @throws InterruptedException 처리 중 인터럽트 발생 시
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @GetMapping("/{buildingId}")
  @ApiOperation(value = "매물 상세 정보 조회", notes = "매물 ID로 상세 정보 조회")
  public ResponseEntity<?> BuildingDetail(
          @PathVariable("buildingId") Long buildingId,
          @ApiIgnore @AuthenticationPrincipal CustomUser user
  ) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    if (user == null || user.getMember() == null) {
      ViewDetailResponse response1 = buildingService.BuildingDetailWithoutMemberId(buildingId);
      return ResponseEntity.ok(response1);
    }
    ViewDetailResponse response2 = buildingService.BuildingDetail(buildingId, user.getMember().getMemberId());
    return ResponseEntity.ok(response2);
  }

  /**
   * 매물 신규 등록
   *
   * @param building 등록할 건물 정보
   * @param complexList 건물 단지 정보
   * @param imageFiles 등록할 이미지 파일 (필수)
   * @param identity 사용자 신원 확인 정보
   * @param user 인증된 사용자 정보
   * @return 성공 메시지와 HTTP 201 상태코드 반환
   * @throws UnsupportedEncodingException 인코딩 관련 예외 발생 시
   * @throws JsonProcessingException JSON 처리 중 예외 발생 시
   * @throws InterruptedException 처리 중 인터럽트 발생 시
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiOperation(value = "매물 등록", notes = "로그인한 사용자가 매물 신규 등록")
  public ResponseEntity<?> saleRegistration(
          @RequestPart("building") BuildingDetails building,
          @RequestPart("complexList") ComplexDetails complexList,
          @RequestPart(value = "imageFile", required = true) List<MultipartFile> imageFiles,
          @RequestPart("identity") String identity,
          @ApiIgnore @AuthenticationPrincipal CustomUser user
  ) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
    if (user == null || user.getMember() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    String memberId = user.getMember().getMemberId();
    ImageDetailsList imageList = new ImageDetailsList();
    imageList.setImages(
            imageFiles.stream()
                    .map(f -> new ImageDetails(f))
                    .collect(Collectors.toList())
    );
    SaleRegistrationRequest request = new SaleRegistrationRequest(building, complexList, imageList, identity);
    buildingService.SaleRegistration(request, memberId);
    return ResponseEntity.status(HttpStatus.CREATED).body("매물이 성공적으로 등록되었습니다.");
  }

  /**
   * 매물 삭제
   *
   * @param buildingId 삭제할 매물의 ID
   * @param user 인증된 사용자 정보
   * @return 성공 메시지와 HTTP 200 상태코드 반환
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @DeleteMapping("/remove/{buildingId}")
  @ApiOperation(value = "매물 삭제", notes = "매물 ID로 삭제")
  public ResponseEntity<?> removeBuilding(
          @PathVariable("buildingId") Long buildingId,
          @ApiIgnore @AuthenticationPrincipal CustomUser user
  ) {
    if (user == null || user.getMember() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    buildingService.removeBuilding(buildingId);
    return ResponseEntity.status(HttpStatus.OK).body("매물이 성공적으로 삭제되었습니다.");
  }

  /**
   * 매물 찜하기
   *
   * @param request 찜하기 요청 정보가 담긴 BookmarkRequest 객체
   * @param user 인증된 사용자 정보
   * @return 성공 메시지와 HTTP 200 상태코드 반환
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @PostMapping("/bookmark")
  @ApiOperation(value = "매물 찜하기", notes = "로그인한 사용자가 매물을 찜 목록에 추가")
  public ResponseEntity<?> bookMark(
          @RequestBody BookmarkRequest request,
          @ApiIgnore @AuthenticationPrincipal CustomUser user
  ) {
    if (user == null || user.getMember() == null) {
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
   * @param user 인증된 사용자 정보
   * @return 성공 메시지와 HTTP 200 상태코드 반환
   * @throws ResponseStatusException 로그인 정보가 없으면 401 에러 발생
   */
  @DeleteMapping("/bookmark/{buildingId}")
  @ApiOperation(value = "매물 찜하기 취소", notes = "찜한 매물을 목록에서 해제")
  public ResponseEntity<?> bookMarkDelete(
          @PathVariable("buildingId") Long buildingId,
          @ApiIgnore @AuthenticationPrincipal CustomUser user
  ) {
    if (user == null || user.getMember() == null) {
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
          @ApiIgnore @AuthenticationPrincipal CustomUser user
  ) {
    if (user == null || user.getMember() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 정보가 없습니다.");
    }
    String memberId = user.getMember().getMemberId();
    buildingService.updateBuilding(request, memberId);
    return ResponseEntity.status(HttpStatus.OK).body("매물 수정에 성공했습니다.");
  }
}
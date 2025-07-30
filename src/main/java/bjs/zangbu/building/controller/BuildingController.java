package bjs.zangbu.building.controller;

import bjs.zangbu.building.dto.response.BuildingResponse.*;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.service.BuildingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;

/**
 * Building 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 매물 상세 조회, 찜하기, 찜 해제, 등록, 목록 조회, 삭제 기능을 제공한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/building")
@Tag(name = "Building API", description = "빌딩 관련 API")
public class BuildingController {

    private final BuildingService buildingService;

    /**
     * 매물 상세 조회 요청 처리
     * POST /building
     *
     * @param request 매물 상세 조회 요청 DTO
     * @return 매물 상세 정보 응답 DTO와 200 OK
     * @throws UnsupportedEncodingException 인코딩 예외
     * @throws JsonProcessingException JSON 처리 예외
     * @throws InterruptedException API 호출 지연 예외
     */
    @PostMapping("")
    @Operation(summary = "매물 상세보기", description = "매물에 대한 상세정보를 보는 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "매물 상세 조회 요청 DTO",
            required = true,
            content = @Content(schema = @Schema(implementation = ViewDetailRequest.class))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "특정 매물에 대한 상세정보를 조회하는데 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "특정 매물에 대한 상세정보를 조회하는데 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 특정 매물에 대한 상세정보를 불러오는데 실패했습니다.")
    })
    public ResponseEntity<?> viewDetail(@RequestBody ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        ViewDetailResponse response = buildingService.viewDetailService(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 매물 찜하기 요청 처리
     * POST /building/bookmark
     *
     * @param request 찜 요청 DTO
     * @param userDetails 인증된 사용자 정보
     * @return 200 OK 응답 (본문 없음)
     */
    @PostMapping("/bookmark")
    @Operation(summary = "매물 찜하기", description = "특정 유저가 특정 매물을 찜하는 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "찜하기 요청 DTO",
            required = true,
            content = @Content(schema = @Schema(implementation = BookmarkRequest.class))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매물 찜하기에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "매물 찜하기에 실패했습니다."),
            @ApiResponse(responseCode = "401", description = "로그인 정보가 없어서 매물 찜하기에 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 매물 찜하기를 하는중 오류가 발생했습니다.")
    })
    public ResponseEntity<?> bookMark(@RequestBody BookmarkRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        String memberId = userDetails.getUsername();
        buildingService.bookMarkService(request, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 매물 찜 해제 요청 처리
     * DELETE /building/bookmark/{buildingId}
     *
     * @param buildingId 찜 해제할 매물 ID
     * @param userDetails 인증된 사용자 정보
     * @return 200 OK 응답 (본문 없음)
     */
    @DeleteMapping("/bookmark/{buildingId}")
    @Operation(summary = "매물 찜하기 취소", description = "특정 유저가 특정 매물을 찜하기 취소하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매물 찜하기 취소에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "매물 찜하기 취소에 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 매물 찜하기를 취소하는데 오류가 발생했습니다.")
    })
    public ResponseEntity<?> bookMarkDelete(@PathVariable("buildingId") Long buildingId, @AuthenticationPrincipal UserDetails userDetails) {
        String memberId = userDetails.getUsername();
        buildingService.bookMarkServiceDelete(buildingId, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 매물 등록 요청 처리
     * POST /building/upload
     *
     * @param request 매물 등록 요청 DTO
     * @param userDetails 인증된 사용자 정보
     * @return 200 OK 응답 (본문 없음)
     * @throws ResponseStatusException 인증 안된 경우 401 에러 반환
     */
    @PostMapping("/upload")
    @Operation(summary = "매물 등록", description = "특정 유저가 매물을 등록하는 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "매물 등록 요청 DTO",
            required = true,
            content = @Content(schema = @Schema(implementation = SaleRegistrationRequest.class))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "매물 등록에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "매물 등록에 실패했습니다."),
            @ApiResponse(responseCode = "401", description = "로그인을 한 사용자만 매물 등록이 가능합니다.")
    })
    public ResponseEntity<?> saleRegistration(@RequestBody SaleRegistrationRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인을 한 사용자만 매물 등록 가능합니다.");
        }
        String memberId = userDetails.getUsername();
        buildingService.SaleRegistration(request, memberId);
        return ResponseEntity.ok().build();
    }

    /**
     * 매물 목록 조회 요청 처리 (필터링 및 페이징 지원)
     * GET /building/list
     *
     * @param buildingName 매물명 필터 (선택적)
     * @param saleType 판매 유형 필터 (선택적)
     * @param startPrice 가격 범위 시작 필터 (선택적)
     * @param endPrice 가격 범위 종료 필터 (선택적)
     * @param propertyType 부동산 종류 필터 (선택적)
     * @param page 페이지 번호 (기본 1)
     * @param size 페이지 크기 (기본 10)
     * @param userDetails 인증된 사용자 정보 (찜 여부 반영용)
     * @return 페이징된 매물 목록 DTO와 200 OK
     */
    @GetMapping("/list")
    @Operation(summary = "필터링된 매물 리스트", description = "특정 조건에 따라 필터링된 매물 보여주는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "필터링된 매물을 가져오는데 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "필터링된 매물을 가져오는데 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 필터링된 매물을 가져오는데 오류가 발생했습니다.")
    })
    public ResponseEntity<?> getBuildingList(
            @RequestParam(value = "buildingName", required = false) String buildingName,
            @RequestParam(value = "saleType", required = false) String saleType,
            @RequestParam(value = "startPrice", required = false) Long startPrice,
            @RequestParam(value = "endPrice", required = false) Long endPrice,
            @RequestParam(value = "propertyType", required = false) String propertyType,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String memberId = (userDetails != null && !userDetails.getUsername().isBlank())
                ? userDetails.getUsername() : null;

        FilteredResponse pageInfo = buildingService.getBuildingList(buildingName, saleType, startPrice, endPrice, propertyType, page, size, memberId);
        return ResponseEntity.ok(pageInfo);
    }

    /**
     * 매물 삭제 요청 처리
     * DELETE /building/remove/{buildingId}
     *
     * @param buildingId 삭제할 매물 ID
     * @return 200 OK 응답 (본문 없음)
     */
    @DeleteMapping("/remove/{buildingId}")
    @Operation(summary = "매물 삭제", description = "특정 매물에 대해서 삭제하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매물을 삭제하는데 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "매물을 삭제하는데 실패했습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 매물을 삭제하는데 오류가 발생했습니다.")
    })
    public ResponseEntity<?> removeBuilding(@PathVariable("buildingId") Long buildingId) {
        buildingService.removeBuilding(buildingId);
        return ResponseEntity.ok().build();
    }
}
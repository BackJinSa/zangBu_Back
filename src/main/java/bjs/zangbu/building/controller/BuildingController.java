package bjs.zangbu.building.controller;
import bjs.zangbu.building.dto.response.BuildingResponse.*;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.service.BuildingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
// building 엔드포인트로 시작하는 요청들을 처리하는 컨트롤러
@RequestMapping("/building")
public class BuildingController {
    // 서비스 호출을 위한 BuildingService 주입
    private final BuildingService buildingService;

    // 매물 상세 조회 POST 요청 처리
    @PostMapping("")
    public ResponseEntity<?> viewDetail(@RequestBody ViewDetailRequest request)
    // Codef API 호출 시 발생할 수 있는 예외들을 메서드에서 처리하지 않고 던짐
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        // 받은 요청 객체를 서비스로 전달하여 처리
        ViewDetailResponse response = buildingService.viewDetailService(request);
        return ResponseEntity.ok(response); // 200 OK와 함께 응답 반환
    }

    // 매물 찜하기 POST 요청 처리
    @PostMapping("/bookmark")
    public ResponseEntity<?> bookMark(@RequestBody BookmarkRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        // 인증된 사용자 아이디 추출
        String memberId = userDetails.getUsername();
        // 찜하기 서비스 호출
        buildingService.bookMarkService(request, memberId);
        return ResponseEntity.ok().build(); // 200 OK 반환, 별도 응답 바디 없음
    }

    // 매물 찜 해제 DELETE 요청 처리
    @DeleteMapping("/bookmark/{buildingId}")
    public ResponseEntity<?> bookMarkDelete(@PathVariable("buildingId") Long buildingId, @AuthenticationPrincipal UserDetails userDetails) {
        // 인증된 사용자 아이디 추출
        String memberId = userDetails.getUsername();
        // 찜 해제 서비스 호출
        buildingService.bookMarkServiceDelete(buildingId, memberId);
        return ResponseEntity.ok().build(); // 200 OK 반환
    }

    // 매물 등록 POST 요청 처리
    @PostMapping("/upload")
    public ResponseEntity<?> saleRegistration(@RequestBody SaleRegistrationRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        // 인증된 사용자 아이디 추출
        String memberId = userDetails.getUsername();
        // 매물 등록 서비스 호출
        buildingService.SaleRegistration(request, memberId);
        return ResponseEntity.ok().build(); // 200 OK 반환
    }

    // 매물 목록 조회 GET 요청 처리 (필터링 및 페이징 지원)
    @GetMapping("/list")
    public ResponseEntity<?> getBuildingList(
            @RequestParam(value = "buildingName", required = false) String buildingName,
            @RequestParam(value = "saleType", required = false) String saleType,
            @RequestParam(value = "startPrice", required = false) Long startPrice,
            @RequestParam(value = "endPrice", required = false) Long endPrice,
            @RequestParam(value = "propertyType", required = false) String propertyType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 인증된 사용자 아이디가 있으면 사용, 없으면 null 처리
        String memberId = (userDetails != null && !userDetails.getUsername().isBlank())
                ? userDetails.getUsername() : null;
        // 서비스에 필터 조건과 페이징 정보 전달하여 결과 조회
        FilteredResponse response = buildingService.getBuildingList(buildingName, saleType, startPrice, endPrice, propertyType, page, size, memberId);
        return ResponseEntity.ok(response); // 조회 결과 반환
    }

    // 매물 삭제 DELETE 요청 처리
    @DeleteMapping("/remove/{buildingId}")
    public ResponseEntity<?> removeBuilding(@PathVariable("buildingId") Long buildingId) {
        // 매물 삭제 서비스 호출
        buildingService.removeBuilding(buildingId);
        return ResponseEntity.ok().build(); // 200 OK 반환
    }
}


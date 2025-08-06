package bjs.zangbu.building.controller;

import bjs.zangbu.building.dto.response.MainResponse;
import bjs.zangbu.building.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 메인 페이지 관련 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
// @Tag(name = "Main API", description = "메인페이지 API")
public class MainController {

  /**
   * 메인 페이지 관련 서비스 주입
   */
  private final MainService mainService;

  /**
   * 메인 페이지 정보를 조회하는 엔드포인트
   *
   * @param userDetails 인증된 사용자 정보 (Spring Security에서 주입)
   * @return ResponseEntity - 성공 시 200 OK와 메시지 반환, 실패 시 적절한 HTTP 상태 코드와 메시지 반환
   */
  @RequestMapping("")
//     @Operation(summary = "메인 페이지", description = "메인 페이지에서 부르는 API")
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "메인 페이지를 불러오는데 성공했습니다."),
//       @ApiResponse(responseCode = "404", description = "메인 페이지를 불러오는데 실패했습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 메인 페이지 정보를 가져오는데 실패했습니다.")
//})

  public ResponseEntity<?> mainPage(@AuthenticationPrincipal UserDetails userDetails) {
    try {
      // 인증된 사용자 ID 추출
      String memberId = userDetails.getUsername();

      // 메인 페이지 정보 조회 서비스 호출
      MainResponse.MainPageResponse response = mainService.mainPage(memberId);

      // 성공 메시지 및 200 OK 반환
      return ResponseEntity.ok().body("메인 페이지를 불러오는데 성공했습니다.");
    } catch (IllegalArgumentException e) {
      // 데이터가 없을 경우 404 Not Found 반환
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("메인 페이지를 불러오는데 실패했습니다.");
    } catch (Exception e) {
      // 기타 서버 에러 발생 시 500 Internal Server Error 반환
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버에서 메인 페이지 정보를 가져오는데 실패했습니다.");
    }
  }
}

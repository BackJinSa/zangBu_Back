package bjs.zangbu.building.controller;

import bjs.zangbu.building.dto.response.MainResponse;
import bjs.zangbu.building.dto.response.MainResponse.MainPageResponse;
import bjs.zangbu.building.service.MainService;
import bjs.zangbu.security.account.vo.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 메인 페이지 관련 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
@Api(tags = "Main API", description = "메인페이지 관련 기능 API")
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
  @ApiOperation(
          value = "메인 페이지 조회",
          notes = "메인 페이지에 필요한 정보를 조회합니다.",
          response = MainPageResponse.class
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "메인 페이지를 불러오는데 성공했습니다."),
          @ApiResponse(code = 401, message = "인증되지 않은 사용자 접근"),
          @ApiResponse(code = 404, message = "메인 페이지를 불러오는데 실패했습니다. (데이터 없음)"),
          @ApiResponse(code = 500, message = "서버 오류로 인한 메인 페이지 정보 조회 실패")
  })
  @GetMapping("")
  public ResponseEntity<?> mainPage(@ApiIgnore @AuthenticationPrincipal CustomUser user) {
    try {
      // 인증된 사용자 ID 추출
      String memberId = user.getMember().getMemberId();

      // 메인 페이지 정보 조회 서비스 호출
      MainPageResponse response = mainService.mainPage(memberId);

      // 성공 메시지 및 200 OK 반환
      return ResponseEntity.ok().body(response);
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
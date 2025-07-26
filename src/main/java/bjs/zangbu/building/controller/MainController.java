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

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @RequestMapping("")
    public ResponseEntity<?> mainPage(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String memberId = userDetails.getUsername();
            MainResponse.MainPageResponse response = mainService.mainPage(memberId);
            // 성공 메시지와 200 OK 응답
            return ResponseEntity.ok().body("메인 페이지를 불러오는데 성공했습니다.");
        } catch (IllegalArgumentException e) {
            // 404 Not Found 응답: 메인 페이지를 불러오는데 실패
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("메인 페이지를 불러오는데 실패했습니다.");
        } catch (Exception e) {
            // 500 Internal Server Error 응답: 서버 에러
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 메인 페이지 정보를 가져오는데 실패했습니다.");
        }
    }
}

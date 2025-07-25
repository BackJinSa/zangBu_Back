package bjs.zangbu.building.controller;

import bjs.zangbu.building.dto.response.MainResponse;
import bjs.zangbu.building.service.BuildingService;
import bjs.zangbu.building.service.MainService;
import lombok.RequiredArgsConstructor;
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
        String memberId = userDetails.getUsername();
        MainResponse.MainPageResponse response = mainService.mainPage(memberId);
        return ResponseEntity.ok(response);
    }
}

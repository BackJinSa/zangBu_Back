package bjs.zangbu.codef.controller;

import bjs.zangbu.codef.dto.request.CodefRequest.secureNoRequest;
import bjs.zangbu.codef.service.CodefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/codef")
public class CodefController {

    private final CodefService codefService;

    @PostMapping("/secure")
    public ResponseEntity<?> secure(@RequestBody secureNoRequest request) {
        codefService.processSecureNo(request.getSessionKey(), request.getSecureNo());
        return ResponseEntity.ok().build();
    }
}

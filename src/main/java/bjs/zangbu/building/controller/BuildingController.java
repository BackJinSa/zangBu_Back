package bjs.zangbu.building.controller;
import bjs.zangbu.building.dto.response.BuildingResponse.*;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.building.service.BuildingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/building")
public class BuildingController {

    private final BuildingService buildingService;
    @PostMapping("")
    public ResponseEntity<ViewDetailResponse> viewDetail(ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        ViewDetailResponse response = buildingService.viewDetailService(request);
        return ResponseEntity.ok(response);
    }

}

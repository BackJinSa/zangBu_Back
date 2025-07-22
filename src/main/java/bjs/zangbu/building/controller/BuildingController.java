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
// building 엔드포인트 시작을 위해서 RequestMapping을 해줌.
@RequestMapping("/building")
public class BuildingController {
    // BuildingService로 요청을 보내기 위해서 private final BuildingService buildingSerivce를 선언하고 RequiredArgsConsturctor 어노테이션을 붙혔다.
    private final BuildingService buildingService;
    @PostMapping("")
    public ResponseEntity<ViewDetailResponse> viewDetail(ViewDetailRequest request)
    // 이거는 Codef API를 쓸라면 무조건 에러 처리를 해야하는데 이걸 전역에러 처리하기엔 Codef에서만 사용하기때문에 이렇게 처리했다.
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        // service에 request값 받은거를 넘긴다.
        ViewDetailResponse response = buildingService.viewDetailService(request);
        return ResponseEntity.ok(response);
    }

}

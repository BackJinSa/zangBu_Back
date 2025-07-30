package bjs.zangbu.codef.controller;

import bjs.zangbu.codef.dto.request.CodefRequest.secureNoRequest;
import bjs.zangbu.codef.service.CodefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/codef")
@Tag(name = "Codef API", description = "CODEF 연동 API")
public class CodefController {

    private final CodefService codefService;

    /**
     * CODEF 보안번호 인증 요청 처리
     * POST /codef/secure
     *
     * @param request 보안번호 인증 요청 DTO
     * @return 인증 성공 시 200 OK
     */
    @PostMapping("/secure")
    @Operation(summary = "보안인증 처리", description = "세션키와 보안번호를 사용하여 보안인증을 처리하는 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "보안 인증 요청 DTO",
            required = true,
            content = @Content(schema = @Schema(implementation = secureNoRequest.class))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "보안 인증 처리에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 올바르지 않습니다."),
            @ApiResponse(responseCode = "500", description = "서버에서 보안 인증 처리 중 오류가 발생했습니다.")
    })
    public ResponseEntity<?> secure(@RequestBody secureNoRequest request) {
        codefService.processSecureNo(request.getSessionKey(), request.getSecureNo());
        return ResponseEntity.ok().build();
    }
}

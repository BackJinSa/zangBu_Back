package bjs.zangbu.codef.controller;

import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.dto.request.CodefRequest.AddressRequest;
import bjs.zangbu.codef.dto.request.CodefRequest.secureNoRequest;
import bjs.zangbu.codef.dto.response.CodefResponse.ComplexResponse;
import bjs.zangbu.codef.service.CodefService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/codef")
@Tag(name = "Codef API", description = "CODEF 연동 API")
public class CodefController {

    private final CodefService codefService;

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

    @PostMapping("/complex")
    @Operation(summary = "일련번호 처리", description = "건물명으로 단지 일련번호(complexNo)를 조회하고 반환하는 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "건물명 조회를 위한 요청 DTO",
            required = true,
            content = @Content(schema = @Schema(implementation = AddressRequest.class))
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "단지 일련번호 조회에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(responseCode = "404", description = "일치하는 단지를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<?> complexNo(@RequestBody AddressRequest request) throws
            JsonProcessingException, InterruptedException, UnsupportedEncodingException {
        String resTypeJson = codefService.justListInquiry(request);

        ComplexResponse complexResponse = CodefConverter.parseDataToDto(resTypeJson, ComplexResponse.class);
        List<ComplexResponse.ComplexInfo> complexInfoList = complexResponse.getData();
        String targetComplexName = request.getBuildingName();

        String matchingComplexNo = complexInfoList.stream()
                .filter(info -> info.getResComplexName().equals(targetComplexName))
                .findFirst()
                .map(ComplexResponse.ComplexInfo::getCommComplexNo)
                .orElse("일치하는 complexNo를 찾을 수 없습니다.");

        Map<String, String> response = new HashMap<>();
        response.put("complexNo", matchingComplexNo);
        return ResponseEntity.ok(response);
    }
}
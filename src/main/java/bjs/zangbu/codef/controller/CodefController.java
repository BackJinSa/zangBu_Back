package bjs.zangbu.codef.controller;
import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.dto.request.CodefRequest.AddressRequest;
import bjs.zangbu.codef.dto.request.CodefRequest.secureNoRequest;
import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.codef.service.CodefTwoFactorService;
import bjs.zangbu.security.account.dto.request.AuthRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CODEF 연동 관련 HTTP 요청을 처리하는 REST 컨트롤러. 보안 인증 및 단지 일련번호 조회 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/codef")
@Api(tags = "Codef API", description = "CODEF 연동 API")
public class CodefController {

  private final CodefService codefService;
  private final CodefTwoFactorService codefTwoFactorService;

  @PostMapping(
          path = "/captcha",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ApiOperation(value = "캡차(보안문자) 받기", notes = "2-Way 완료 후 캡차 이미지와 세션키를 반환합니다.")
  @ApiResponses({
          @ApiResponse(code = 200, message = "성공"),
          @ApiResponse(code = 400, message = "요청 파라미터 오류"),
          @ApiResponse(code = 502, message = "CODEF 응답 비정상"),
          @ApiResponse(code = 504, message = "2차 인증 타임아웃")
  })
  public ResponseEntity<String> requestCaptcha(
          @ApiParam(value = "CODEF 주민등록 진위확인 요청", required = true)
          @RequestBody AuthRequest.VerifyCodefRequest request
  ) throws Exception {
    // 서비스가 JSON 문자열을 반환하도록 구현되어 있으므로 그대로 패스스루
    String json = codefTwoFactorService.residentRegistrationAuthenticityConfirmation(request);
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(json);
  }

  /**
   * 보안인증 요청 처리. {@code POST /codef/secure} 엔드포인트를 통해 세션키와 보안번호를 사용하여 보안인증을 진행합니다.
   *
   * @param request 세션키와 보안번호를 포함하는 {@link secureNoRequest} DTO
   * @return 200 OK 응답 (본문 없음)
   */
  @ApiOperation(
          value = "보안인증 처리",
          notes = "세션키와 보안번호를 사용하여 보안인증을 처리합니다."
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "보안 인증 처리에 성공했습니다."),
          @ApiResponse(code = 400, message = "요청 파라미터가 올바르지 않습니다."),
          @ApiResponse(code = 500, message = "서버에서 보안 인증 처리 중 오류가 발생했습니다.")
  })
  @PostMapping("/secure")
  public ResponseEntity<?> secure(
          @ApiParam(value = "보안 인증 요청 DTO", required = true)
          @RequestBody secureNoRequest request) {
    String json = codefService.processSecureNo(request.getSessionKey(), request.getSecureNo());
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(json);
  }

  /**
   * 단지 일련번호(complexNo) 조회 요청 처리. {@code POST /codef/complex} 엔드포인트를 통해 건물명을 기반으로 단지 일련번호를 조회합니다.
   *
   * @param request 건물명을 포함하는 {@link AddressRequest} DTO
   * @return 단지 일련번호가 담긴 {@link Map}과 함께 200 OK 응답
   * @throws JsonProcessingException      JSON 처리 중 발생하는 예외
   * @throws InterruptedException         API 호출 지연 시 발생하는 예외
   * @throws UnsupportedEncodingException 인코딩 지원되지 않을 때 발생하는 예외
   */
  @ApiOperation(
          value = "일련번호 처리",
          notes = "건물명으로 단지 일련번호(complexNo)를 조회하고 반환합니다.",
          response = Map.class
  )
  @ApiResponses({
          @ApiResponse(code = 200, message = "단지 일련번호 조회에 성공했습니다."),
          @ApiResponse(code = 404, message = "일치하는 단지를 찾을 수 없습니다."),
          @ApiResponse(code = 500, message = "서버 오류")
  })
  @PostMapping("/complex")
  public ResponseEntity<?> complexNo(
          @ApiParam(value = "건물명 조회를 위한 요청 DTO", required = true)
          @RequestBody AddressRequest request) throws
          JsonProcessingException, InterruptedException, UnsupportedEncodingException {
    String resTypeJson = codefService.justListInquiry(request);
    List<Map<String, Object>> dataList = CodefConverter.parseDataToDto(resTypeJson, List.class);

    if (dataList == null) {
      Map<String, String> response = new HashMap<>();
      response.put("complexNo", "조회된 데이터가 없습니다.");
      return ResponseEntity.ok(response);
    }

    String targetComplexName = request.getBuildingName();
    System.out.println(dataList);
    String matchingComplexNo = dataList.stream()
            .filter(infoMap -> {
              Object complexNameObj = infoMap.get("resComplexName");
              if (complexNameObj instanceof String) {
                return ((String) complexNameObj).contains(targetComplexName);
              }
              return false;
            })
            .findFirst()
            .map(infoMap -> (String) infoMap.get("commComplexNo"))
            .orElse("일치하는 complexNo를 찾을 수 없습니다.");

    Map<String, String> response = new HashMap<>();
    response.put("complexNo", matchingComplexNo);
    return ResponseEntity.ok(response);
  }
}
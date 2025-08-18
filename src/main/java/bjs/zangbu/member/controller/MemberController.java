package bjs.zangbu.member.controller;

import bjs.zangbu.building.vo.Building;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameCheck;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameRequest;
import bjs.zangbu.member.dto.request.MemberRequest.EditNotificationConsentRequest;
import bjs.zangbu.member.dto.request.MemberRequest.EditPassword;
import bjs.zangbu.member.dto.response.MemberResponse;
import bjs.zangbu.member.dto.response.MemberResponse.BookmarkList;
import bjs.zangbu.member.dto.response.MemberResponse.EditMyPage;
import bjs.zangbu.member.mapper.MemberMapper;
import bjs.zangbu.member.service.MemberService;
import bjs.zangbu.security.account.vo.CustomUser;
import bjs.zangbu.security.account.vo.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/member/mypage")
@Api(tags = "Member API", value = "회원 마이페이지 관련 기능 API")
public class MemberController {

  private final MemberService memberService;
  private final MemberMapper memberMapper;

  //1. 찜한 매물 리스트 조회
//     @Operation(
//  summary ="찜한 매물 리스트 조회",
//  description ="회원이 찜한 매물 리스트를 조회합니다."
//      )
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "찜한 매물을 불러오는데 성공했습니다."),
//       @ApiResponse(responseCode = "400", description = "찜한 매물을 불러오는데 실패했습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 찜한 매물 정보를 불러오는데 실패했습니다.")
//  })
  @GetMapping("/favorites")
  public ResponseEntity<?> getFavorites(
      @AuthenticationPrincipal CustomUser customUser,
      //이렇게 써주면 spring security 필터가 인증 수행한 결과를 자동으로 주입
//      @Parameter(description = "요청 페이지", example = "1")
      @RequestParam(defaultValue = "1") int page,         // 요청 페이지 (1부터 시작)
//      @Parameter(description = "페이지당 항목 수", example = "10")
      @RequestParam(defaultValue = "10") int size         // 페이지당 항목 수)
  ) {
    log.info("[/favorites] principal email={}, memberId={}",
            customUser.getMember().getEmail(), customUser.getMember().getMemberId());
    try {
      //인증된 사용자 정보
      Member member = customUser.getMember();

      // PageHelper 페이지네이션 시작
      PageHelper.startPage(page, size);

      // Response 생성(페이지네이션된 데이터 포함)
      BookmarkList response = memberService.getBookmarks(member.getMemberId());

      return ResponseEntity.ok(response); //200
    } catch (ResponseStatusException e) { //400
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버에서 찜한 매물 정보를 불러오는데 실패했습니다.");
    }
  }

  //2. 찜한 매물 삭제
//     @Operation(
//  summary ="찜한 매물 삭제",
//  description ="회원이 찜한 매물을 삭제합니다."
//      )
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "찜한 매물을 삭제했습니다."),
//       @ApiResponse(responseCode = "400", description = "찜한 매물을 찾을 수 없습니다."),
//       @ApiResponse(responseCode = "500", description = "찜한 매물 삭제 중 오류가 발생했습니다.")
//  })
  @DeleteMapping("/favorite/delete")
  public ResponseEntity<?> deleteFavorite(
//      @Parameter(description = "회원 ID")
          @AuthenticationPrincipal CustomUser customUser,
//      @Parameter(description = "건물 ID")
          @RequestParam Long buildingId) {
    try {
      Member member = customUser.getMember();
      memberService.deleteBookmark(member.getMemberId(), buildingId);
      return ResponseEntity.ok().build(); //200
    } catch (ResponseStatusException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) { //500
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("찜한 매물 삭제 중 오류가 발생했습니다.");
    }
  }

  //3. 회원정보 수정 페이지로 이동
//     @Operation(
//  summary ="회원정보 수정 페이지 조회",
//  description ="회원정보 수정 페이지에 필요한 데이터를 조회합니다."
//      )
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "정보를 불러오는데 성공했습니다."),
//       @ApiResponse(responseCode = "400", description = "정보를 불러오는데 실패했습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 정보를 불러오는데 실패했습니다.")
//  })
  @PostMapping("/edit")
  public ResponseEntity<?> getEditPage(
      @AuthenticationPrincipal CustomUser customUser
  ) {
    try {
      Member member = customUser.getMember();

      EditMyPage response = memberService.getMyPageInfo(member.getEmail());

      //member id 넘겨서 정보 가져오기
      return ResponseEntity.ok(response); //200
    } catch (ResponseStatusException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) { //500
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버에서 정보를 불러오는데 실패했습니다.");
    }
  }

  //4. 비밀번호 변경
//     @Operation(
//  summary ="비밀번호 변경",
//  description ="회원의 비밀번호를 변경합니다."
//      )
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "비밀번호 변경에 성공했습니다."),
//       @ApiResponse(responseCode = "400", description = "비밀번호 변경에 실패했습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 비밀번호 변경을 처리하는데 실패했습니다.")
//  })
  @PatchMapping("/edit/password")
  public ResponseEntity<?> changePassword(
      @AuthenticationPrincipal CustomUser customUser,
//      @io.swagger.v3.oas.annotations.parameters.RequestBody(
//          description = "비밀번호 변경 요청 DTO",
//          required = true,
//          content = @Content(
//              schema = @Schema(implementation = EditPassword.class)
//          )
//      )
      @RequestBody EditPassword request
  ) {
    try {
      Member member = customUser.getMember();

      memberService.editPassword(member.getMemberId(), request);
      return ResponseEntity.ok().build(); //200
    } catch (IllegalArgumentException e) {
      //비밀번호 불일치 400
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버에서 비밀번호 변경을 처리하는데 실패했습니다.");
    } catch (Exception e) {
      // 그 외의 예외 처리
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버에서 비밀번호 변경을 처리하는데 실패했습니다.");
    }
  }

  //5. 닉네임 중복 확인
//     @Operation(
//  summary ="닉네임 중복 확인",
//  description ="회원의 닉네임 중복 여부를 확인합니다."
//      )
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "중복되는 닉네임이 없습니다."),
//       @ApiResponse(responseCode = "400", description = "중복되는 닉네임이 있습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 중복되는 닉네임을 찾는데 오류가 발생했습니다.")
//  })
  @PostMapping("/edit/nickname/check")
  public ResponseEntity<?> checkNickname(
      @AuthenticationPrincipal CustomUser customUser,
//      @io.swagger.v3.oas.annotations.parameters.RequestBody(
//          description = "닉네임 중복 확인 요청 DTO",
//          required = true,
//          content = @Content(
//              schema = @Schema(implementation = EditNicknameCheck.class)
//          )
//      )
      @RequestBody EditNicknameCheck request
  ) {
    try {
      Member member = customUser.getMember();

      String newNickname = request.getNickname();
      String currentNickname = member.getNickname();

      //만약 현재 닉네임과 같은 닉네임 넣으면
      if (newNickname.equals(currentNickname)) {
        return ResponseEntity.ok("현재 닉네임과 동일합니다.");
      }

      boolean isDuplicated = memberService.isNicknameDuplicated(newNickname);
      //중복 여부 판단
      if (isDuplicated) {
        return ResponseEntity.badRequest().body("중복되는 닉네임이 있습니다.");
      }
      return ResponseEntity.ok("중복되는 닉네임이 없습니다."); //200

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버에서 중복되는 닉네임을 찾는데 오류가 발생했습니다.");
    }
  }

  //6. 닉네임 변경
//     @Operation(
//  summary ="닉네임 변경",description ="회원의 닉네임을 변경합니다."
//      )
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "닉네임 변경에 성공했습니다."),
//       @ApiResponse(responseCode = "400", description = "닉네임 변경에 실패했습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 닉네임 변경을 처리하는데 실패했습니다.")
//  })
  @PatchMapping("/edit/nickname")
  public ResponseEntity<?> changeNickname(
          @AuthenticationPrincipal CustomUser customUser,
//      @io.swagger.v3.oas.annotations.parameters.RequestBody(
//          description = "닉네임 변경 요청 DTO",
//          required = true,
//          content = @Content(
//              schema = @Schema(implementation = EditNicknameRequest.class)
//          )
//      )
          @RequestBody EditNicknameRequest request) {

    if (request.getNewNickname() == null || request.getNewNickname().isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    try {
      Member member = customUser.getMember();

      memberService.editNickname(member.getMemberId(), request);

      Member refreshed = memberMapper.findByEmail(member.getEmail());
      String updateNickname = refreshed.getNickname();

      return ResponseEntity.ok(new MemberResponse.EditNicknameResponse(updateNickname));
    } catch (IllegalStateException | IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("서버에서 닉네임 변경을 처리하는데 실패했습니다.");
    }
  }

  //7. 탈퇴 페이지
//     @Operation(
//  summary ="회원 탈퇴",description ="회원 탈퇴를 처리합니다."
//      )
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "204", description = "탈퇴가 정상적으로 처리되었습니다."),
//       @ApiResponse(responseCode = "400", description = "탈퇴 처리가 실패되었습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 유저 탈퇴 처리에 실패했습니다.")
//  })
  @DeleteMapping("/remove")
  public ResponseEntity<?> deleteMember(
          @AuthenticationPrincipal CustomUser customUser,
          HttpServletResponse response
  ) {
    try {
      Member member = customUser.getMember();

      // 1) 서비스 호출(서버측 refresh 삭제 + 회원 삭제)
      memberService.removeMember(member.getMemberId());

      // 2) 클라이언트 refresh 쿠키 만료
      response.addHeader("Set-Cookie",
              "refreshToken=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None");

      return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("서버에서 유저 탈퇴 처리에 실패했습니다.");
    }
  }

  //8. 알림 수신 여부 변경
//     @Operation(summary = "알림 수신 여부 변경",
//  description ="알림 수신 동의 상태를 변경합니다.")
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "알림 수신 여부 변경에 성공했습니다."),
//       @ApiResponse(responseCode = "400", description = "알림 수신 여부 변경에 실패했습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 알림 수신 여부 변경을 처리하는데 실패했습니다.")
//  })
  @PatchMapping("/edit/notification/consent")
  public ResponseEntity<?> updateNotificationConsent(
      @AuthenticationPrincipal CustomUser customUser,
//      @io.swagger.v3.oas.annotations.parameters.RequestBody(
//          description = "알림 수신 여부 변경 요청 DTO",
//          required = true,
//          content = @Content(
//              schema = @Schema(implementation = EditNotificationConsentRequest.class)
//          )
//      )
      @RequestBody EditNotificationConsentRequest request
  ) {
    try {
      Member member = customUser.getMember();

      memberService.updateFcmConsent(member.getMemberId(), request.getConsent());
      return ResponseEntity.ok().build(); //200
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("알림 수신 여부 변경에 실패했습니다.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버에서 알림 수신 여부 변경을 처리하는데 실패했습니다.");
    }
  }

  //9. 알림 수신 여부 조회
//     @Operation(summary = "알림 수신 여부 조회",
//  description ="알림 수신 동의 여부를 조회합니다.")
// 
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "알림 수신 여부 조회에 성공했습니다."),
//       @ApiResponse(responseCode = "400", description = "알림 수신 여부 조회에 실패했습니다."),
//       @ApiResponse(responseCode = "500", description = "서버에서 알림 수신 여부 조회를 처리하는데 실패했습니다.")
//  })
  @GetMapping("/notification/consent")
  public ResponseEntity<?> getNotificationConsent(
      @AuthenticationPrincipal CustomUser customUser
  ) {
    try {
      Member member = customUser.getMember();

      boolean consent = memberService.getFcmConsent(member.getMemberId());
      //수신 여부 리턴
      return ResponseEntity.ok(consent);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("알림 수신 여부 조회에 실패했습니다.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버에서 알림 수신 여부 조회를 처리하는데 실패했습니다.");
    }
  }

  //10 사용자가 등록한 매물 리스트 조회
//    @Operation(summary = "사용자가 등록한 매물 조회 리스트",
//    description ="매물 조회합니다.")
//
//   @ApiResponses({
//       @ApiResponse(responseCode = "200", description = "매물 리스트 조회에 성공했습니다."),
//       @ApiResponse(responseCode = "400", description = "매물 리스트 조회에 실패했습니다."),
//       @ApiResponse(responseCode = "500", description = "매물 리스트 조회를 처리하는데 실패했습니다.")
//  })
  @GetMapping("/myBuildings")
  public ResponseEntity<?> getMyBuildings(
          @AuthenticationPrincipal CustomUser customUser,
          @RequestParam(defaultValue = "1") int page,
          @RequestParam(defaultValue = "10") int size
  ) {
    try {
      String memberId = customUser.getMember().getMemberId();

      PageHelper.startPage(page, size);
      List<Building> voList = memberService.getMyBuildings(memberId);

      PageInfo<Building> pageInfo = new PageInfo<>(voList);

      MemberResponse.MyBuildingList response = MemberResponse.MyBuildingList.toDto(pageInfo);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("서버에서 등록한 매물 정보를 불러오는데 실패했습니다.");
    }
  }
}

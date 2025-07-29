package bjs.zangbu.member.controller;

import bjs.zangbu.member.dto.request.MemberRequest.EditNotificationConsentRequest;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameCheck;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameRequest;
import bjs.zangbu.member.dto.request.MemberRequest.EditPassword;
import bjs.zangbu.member.mapper.MemberMapper;
import bjs.zangbu.member.service.MemberService;
import bjs.zangbu.security.account.vo.Member;
import bjs.zangbu.security.util.JwtProcessor;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/member/mypage")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final JwtProcessor jwtProcessor;

    //공통 부분 메서드
    //jwt 이용해서 인증된 사용자의 member 반환
    private Member getAuthenticatedMember(String accessTokenHeader) {
        //헤더 존재와 시작부 확인
        if (accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        //Bearer {token}에서 access token 부분만 추출
        String accessToken = accessTokenHeader.replace("Bearer ", "").trim();

        //jwt 유효성 판단
        if (!jwtProcessor.validateToken(accessToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        //jwt에서 이메일만 추출
        String email = jwtProcessor.getEmail(accessToken);
        //이메일로 db에서 해당 멤버 가져오기
        Member member = memberMapper.findByEmail(email);

        //db에 멤버 없을 때 에러 처리
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "회원 정보를 찾을 수 없습니다.");
        }
        return member;
    }

    //1. 찜한 매물 리스트 조회
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites(
            @RequestHeader("Authorization") String accessTokenHeader) {

        Member member = getAuthenticatedMember(accessTokenHeader);

        return ResponseEntity.ok(memberService.getBookmarks(member.getMemberId()));
    }

    //2. 찜한 매물 삭제
    @PostMapping("/favorite/delete")
    public ResponseEntity<Void> deleteFavorite(@RequestParam String memberId, @RequestParam Long buildingId) {
        memberService.deleteBookmark(memberId, buildingId);
        return ResponseEntity.ok().build();
    }

    //3. 회원정보 수정 페이지로 이동
    @GetMapping("/edit")
    public ResponseEntity<?> getEditPage(
            @RequestHeader("Authorization") String accessTokenHeader
    ) {
        Member member = getAuthenticatedMember(accessTokenHeader);

        //member id 넘겨서 정보 가져오기
        return ResponseEntity.ok(memberService.getMyPageInfo(member.getMemberId()));
    }

    //4. 비밀번호 변경
    @PostMapping("/edit/password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody EditPassword request) {

        Member member = getAuthenticatedMember(accessTokenHeader);

        memberService.editPassword(member.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    //5. 닉네임 중복 확인
    @PostMapping("/edit/nickname/check")
    public ResponseEntity<?> checkNickname(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody EditNicknameCheck request
    ) {
        Member member = getAuthenticatedMember(accessTokenHeader);

        String newNickname = request.getNickname();
        String currentNickname = member.getNickname();

        //만약 현재 닉네임과 같은 닉네임 넣으면
        if (newNickname.equals(currentNickname)) {
            return ResponseEntity.ok("현재 닉네임과 동일합니다.");
        }

        //중복 여부 판단
        if (memberService.isNicknameDuplicated(newNickname)) {
            return ResponseEntity.badRequest().body("중복되는 닉네임이 있습니다.");
        }
        return ResponseEntity.ok("중복되는 닉네임이 없습니다.");
    }

    //6. 닉네임 변경
    @PostMapping("/edit/nickname")
    public ResponseEntity<?> changeNickname(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody EditNicknameRequest request) {

        Member member = getAuthenticatedMember(accessTokenHeader);

        String newNickname = request.getNewNickname();
        String currentNickname = member.getNickname();

        //현재 닉네임과 같은지
        if (newNickname.equals(currentNickname)) {
            return ResponseEntity.ok("현재 닉네임과 동일합니다.");
        }

        //닉네임 중복 확인
        if (memberService.isNicknameDuplicated(newNickname)) {
            return ResponseEntity.badRequest().body("중복되는 닉네임이 있습니다.");
        }

        memberService.editNickname(member.getMemberId(), request);
        return ResponseEntity.ok("닉네임이 성공적으로 변경되었습니다.");
    }

    //7. 탈퇴 페이지
    @PostMapping("/remove")
    //브라우저 또는 HTML 폼에서 DELETE를 직접 지원하지 않아서 post 사용하는 경우가 더 많음
    public ResponseEntity<?> deleteMember(
            @RequestHeader("Authorization") String accessTokenHeader
    ) {

        Member member = getAuthenticatedMember(accessTokenHeader);

        memberService.removeMember(member.getMemberId());
        return ResponseEntity.ok().build();
    }

    //8. 알림 수신 여부 변경
    @PostMapping("/edit/notification/consent")
    public ResponseEntity<?> updateNotificationConsent(
            @RequestHeader("Authorization") String accessTokenHeader,
            @RequestBody EditNotificationConsentRequest request
    ){

        Member member = getAuthenticatedMember(accessTokenHeader);

        memberService.updateFcmConsent(member.getMemberId(), request.getConsent());
        return ResponseEntity.ok().build();
    }


    //9. 알림 수신 여부 조회
    @GetMapping("/notification/consent")
    public ResponseEntity<?> getNotificationConsent(
            @RequestHeader("Authorization") String accessTokenHeader
    ){
        Member member = getAuthenticatedMember(accessTokenHeader);

        boolean consent = memberService.getFcmConsent(member.getMemberId());
        //수신 여부 리턴
        return ResponseEntity.ok(consent);
    }

}

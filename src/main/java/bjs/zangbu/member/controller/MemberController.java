package bjs.zangbu.member.controller;

import bjs.zangbu.member.dto.join.BookmarkBuilding;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameCheck;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameRequest;
import bjs.zangbu.member.dto.request.MemberRequest.EditPassword;
import bjs.zangbu.member.dto.response.MemberResponse.EditMyPage;
import bjs.zangbu.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/mypage")
public class MemberController {

    private final MemberService memberService;

    //1. 찜한 매물 리스트 조회
    @GetMapping("/favorites")
    public ResponseEntity<List<BookmarkBuilding>> getFavorites(@RequestParam String memberId) {
        return ResponseEntity.ok(memberService.getBookmarks(memberId));
    }

    //2. 찜한 매물 삭제
    @PostMapping("/favorite/delete")
    public ResponseEntity<Void> deleteFavorite(@RequestParam String memberId, @RequestParam Long buildingId) {
        memberService.deleteBookmark(memberId, buildingId);
        return ResponseEntity.ok().build();
    }

    //3. 회원정보 수정 페이지로 이동
    @GetMapping("/edit")
    public ResponseEntity<EditMyPage> getEditPage(@RequestParam String memberId) {
        return ResponseEntity.ok(memberService.getMyPageInfo(memberId));
    }

    //4. 비밀번호 변경
    @PostMapping("/edit/password")
    public ResponseEntity<Void> changePassword(@RequestParam String memberId, @RequestBody EditPassword request) {
        memberService.editPassword(memberId, request);
        return ResponseEntity.ok().build();
    }

    //5. 닉네임 중복 확인
    @PostMapping("/edit/nickname/check")
    public ResponseEntity<Boolean> checkNickname(@RequestBody EditNicknameCheck request) {
        return ResponseEntity.ok(memberService.isNicknameDuplicated(request.getNickname()));
    }

    //6. 닉네임 변경
    @PostMapping("/edit/nickname")
    public ResponseEntity<Void> changeNickname(@RequestParam String memberId, @RequestBody EditNicknameRequest request) {
        memberService.editNickname(memberId, request);
        return ResponseEntity.ok().build();
    }

    //7. 탈퇴 페이지
    @PostMapping("/remove")
    //브라우저 또는 HTML 폼에서 DELETE를 직접 지원하지 않아서 post 사용하는 경우가 더 많음
    public ResponseEntity<Void> deleteMember(@RequestParam String memberId) {
        memberService.removeMember(memberId);
        return ResponseEntity.ok().build();
    }
}

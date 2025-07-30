package bjs.zangbu.member.service;

import bjs.zangbu.member.dto.join.BookmarkBuilding;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameRequest;
import bjs.zangbu.member.dto.request.MemberRequest.EditPassword;
import bjs.zangbu.member.dto.response.MemberResponse.BookmarkList;
import bjs.zangbu.member.dto.response.MemberResponse.EditMyPage;
import bjs.zangbu.member.mapper.MemberMapper;
import bjs.zangbu.security.account.vo.Member;
import com.github.pagehelper.PageInfo;
import com.google.api.Http;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    //북마크 리스트 전체 가져오기
    @Override
    public BookmarkList getBookmarks(String memberId) {
        List<BookmarkBuilding> bookmarkBuildings= memberMapper.getBookmarksByMemberId(memberId);
        //북마크 리스트 가져올 때 에러 처리
        if (bookmarkBuildings == null || bookmarkBuildings.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "찜한 매물을 불러오는데 실패했습니다.");
        }
        PageInfo<BookmarkBuilding> pageInfo = new PageInfo<>(bookmarkBuildings);
        return BookmarkList.toDto(pageInfo);
    }

    //찜한 매물 삭제
    @Override
    public void deleteBookmark(String memberId, Long buildingId) {
        int result = memberMapper.deleteBookMark(memberId, buildingId);

        if(result == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "찜한 매물을 찾을 수 없습니다.");
        }
    }

    //마이페이지에 뜰 정보
    @Override
    public EditMyPage getMyPageInfo(String email) {
        Member member = memberMapper.get(email);

        //회원 존재하지 않을 때
        if(member == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "정보를 불러오는데 실패했습니다.");
        }

        return new EditMyPage(member.getNickname(), member.getPassword());
    }

    //비밀번호 변경
    @Override
    public void editPassword(String memberId, EditPassword request) {
        String currentPasswordEncoded = memberMapper.findPasswordByMemberId(memberId);
        //현재 비밀번호와 일치하지 않는 경우
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentPasswordEncoded)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        //새로운 비밀번호 인코딩
        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());

        int result = memberMapper.updatePassword(memberId, newEncodedPassword);
        if (result == 0) { //400
            throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
        }
    }

    //닉네임 중복 여부
    @Override
    public boolean isNicknameDuplicated(String nickname) {
        return memberMapper.countByNickname(nickname) > 0;
    }

    //닉네임 변경
    @Override
    public void editNickname(String memberId, EditNicknameRequest request) {

        if (isNicknameDuplicated(request.getNewNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        int result = memberMapper.updateNickname(memberId, request.getNewNickname());
        if (result == 0) { //400
            throw new IllegalStateException("닉네임 변경에 실패했습니다.");
        }
    }

    //회원 탈퇴
    @Override
    public void removeMember(String memberId) {
        int result = memberMapper.deleteMemberId(memberId);
        if (result == 0) { //400
            throw new IllegalStateException("탈퇴 처리가 실패되었습니다.");
        }
    }

    //닉네임 가져오기
    @Override
    public String getNickname(String memberId) {
        return memberMapper.getNicknameByMemberId(memberId);
    }

    //알림 수신 여부 변경
    @Override
    public void updateFcmConsent(String memberId, boolean consent) {
        try {
            memberMapper.updateFcmConsent(memberId, consent);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    //알림 수신 여부 조회
    @Override
    public boolean getFcmConsent(String memberId) {
        try {
            Boolean result = memberMapper.selectFcmConsentByMemberId(memberId);
            return result != null && result;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}

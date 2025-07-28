package bjs.zangbu.member.service;

import bjs.zangbu.member.dto.join.BookmarkBuilding;
import bjs.zangbu.member.dto.request.MemberRequest.EditNicknameRequest;
import bjs.zangbu.member.dto.request.MemberRequest.EditPassword;
import bjs.zangbu.member.dto.response.MemberResponse.EditMyPage;
import bjs.zangbu.member.mapper.MemberMapper;
import bjs.zangbu.security.account.vo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    //북마크 리스트 가져오기
    @Override
    public List<BookmarkBuilding> getBookmarks(String memberId) {
        return memberMapper.getBookmarksByMemberId(memberId);
    }

    //찜한 매물 삭제
    @Override
    public void deleteBookmark(String memberId, Long buildingId) {
        memberMapper.deleteBookMark(memberId, buildingId);
    }

    //마이페이지에 뜰 정보
    @Override
    public EditMyPage getMyPageInfo(String email) {
        Member member = memberMapper.get(email);
        return new EditMyPage(member.getNickname(), member.getPassword());
    }

    //비밀번호 변경
    @Override
    public void editPassword(String memberId, EditPassword request) {
        String currentPasswordEncoded = memberMapper.findPasswordByMemberId(memberId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentPasswordEncoded)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());
        memberMapper.updatePassword(memberId, newEncodedPassword);
    }

    //닉네임 중복 여부
    @Override
    public boolean isNicknameDuplicated(String nickname) {
        return memberMapper.countByNickname(nickname) > 0;
    }

    //닉네임 변경
    @Override
    public void editNickname(String memberId, EditNicknameRequest request) {
        String currentNickname = memberMapper.getNicknameByMemberId(memberId);
        if (!currentNickname.equals(request.getCurrentNickname())) {
            throw new IllegalArgumentException("현재 닉네임이 일치하지 않습니다.");
        }
        if (isNicknameDuplicated(request.getNewNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        memberMapper.updateNickname(memberId, request.getNewNickname());
    }

    //회원 탈퇴
    @Override
    public void removeMember(String memberId) {
        memberMapper.deleteMemberId(memberId);
    }

    //닉네임 가져오기
    @Override
    public String getNickname(String memberId) {
        return memberMapper.getNicknameByMemberId(memberId);
    }

    //알림 수신 여부 변경
    @Override
    public void updateFcmConsent(String memberId, boolean consent) {
        memberMapper.updateFcmConsent(memberId, consent);
    }

    //알림 수신 여부 조회
    @Override
    public boolean getFcmConsent(String memberId) {
        Boolean result = memberMapper.selectFcmConsentByMemberId(memberId);
        return result != null && result;
    }
}

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

    @Override
    public List<BookmarkBuilding> getBookmarks(String memberId) {
        return memberMapper.getBookmarksByMemberId(memberId);
    }

    @Override
    public void deleteBookmark(String memberId, Long buildingId) {
        memberMapper.deleteBookMark(memberId, buildingId);
    }

    @Override
    public EditMyPage getMyPageInfo(String memberId) {
        Member member = memberMapper.get(memberId);
        return new EditMyPage(member.getNickname(), member.getPassword());
    }

    @Override
    public void editPassword(String memberId, EditPassword request) {
        String currentPasswordEncoded = memberMapper.findPasswordByMemberId(memberId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentPasswordEncoded)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());
        memberMapper.updatePassword(memberId, newEncodedPassword);
    }

    @Override
    public boolean isNicknameDuplicated(String nickname) {
        return memberMapper.countByNickname(nickname) > 0;
    }

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

    @Override
    public void removeMember(String memberId) {
        memberMapper.deleteMemberId(memberId);
    }

    @Override
    public String getNickname(String memberId) {
        return memberMapper.getNicknameByMemberId(memberId);
    }

    @Override
    public void updateFcmConsent(String memberId, boolean consent) {
        memberMapper.updateFcmConsent(memberId, consent);
    }

    @Override
    public boolean getFcmConsent(String memberId) {
        Boolean result = memberMapper.selectFcmConsentByMemberId(memberId);
        return result != null && result;
    }
}

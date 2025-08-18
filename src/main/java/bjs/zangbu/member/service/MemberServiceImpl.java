package bjs.zangbu.member.service;

import bjs.zangbu.building.vo.BuildingImg;
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
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh:"; //prefix
    private static final String LOGIN_TOKEN_PREFIX = "login:";

    //북마크 리스트 전체 가져오기
    @Override
    public BookmarkList getBookmarks(String memberId) {
        List<BookmarkBuilding> bookmarkBuildings= memberMapper.getBookmarksByMemberId(memberId);

        //북마크한 내역 없을 수도 있으니 에러처리하지 않고 그대로 반환
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
        Member member = memberMapper.findByEmail(email);

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
        String newNickname = request.getNewNickname();

        if (isNicknameDuplicated(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (newNickname == null || newNickname.isBlank()) {
            throw new IllegalArgumentException("닉네임이 비어있습니다.");
        }
        int result = memberMapper.updateNickname(memberId, newNickname);
        if (result == 0) { //400
            throw new IllegalStateException("닉네임 변경에 실패했습니다.");
        }
    }

    //회원 탈퇴
    @Override
    public void removeMember(String memberId) {
        Member member = memberMapper.findByMemberId(memberId);
        String email = member.getEmail();

        // 1) 서버측 토큰 무효화 (실패해도 회원 삭제는 진행)
        try {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
            redisTemplate.delete(LOGIN_TOKEN_PREFIX + email); // 쓰고 있다면 함께 제거
        } catch (Exception e) {
            // 로그만 남기고 계속 진행
            log.warn("[DELETE] token cleanup failed for email={}", email, e);
        }

        //회원 삭제
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

    // 내가 등록한 매물 전체 리스트 조회 (대표 이미지 포함)
    @Override
    public List<BuildingImg> getMyBuildings(String memberId) {
        validateMemberId(memberId);
        return memberMapper.findMyBuildingsWithImg(memberId);
    }

    // 유저 식별 헬퍼
    private void validateMemberId(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("회원 식별자가 유효하지 않습니다.");
        }
    }

    //생년월일 조회
    @Override
    public String getBirth(String memberId) {
        return memberMapper.getBirthByMemberId(memberId);
    }

    //주민번호 가져오기
    @Override
    public String getIdentity(String memberId) {
        return memberMapper.getIdentityByMemberId(memberId);
    }
}

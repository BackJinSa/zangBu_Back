package bjs.zangbu.member.service;

import bjs.zangbu.building.vo.Building;
import bjs.zangbu.building.vo.BuildingImg;
import bjs.zangbu.member.dto.join.BookmarkBuilding;
import bjs.zangbu.member.dto.request.MemberRequest.*;
import bjs.zangbu.member.dto.response.MemberResponse.*;

import java.util.List;

public interface MemberService {
    //찜한 매물 리스트 조회
    BookmarkList getBookmarks(String memberId);

    //찜한 매물 삭제
    void deleteBookmark(String memberId, Long buildingId);

    //회원정보 수정페이지
    EditMyPage getMyPageInfo(String memberId);

    //비밀번호 변경
    void editPassword(String memberId, EditPassword request);

    //닉네임 중복 체크하기
    boolean isNicknameDuplicated(String nickname);

    //닉네임 변경
    void editNickname(String memberId, EditNicknameRequest request);

    //탈퇴
    void removeMember(String memberId);

    //닉네임 가져오기
    String getNickname(String memberId);

    //생년월일 가져오기
    String getBirth(String memberId);

    //주민번호 뒷자리 가져오기
    String getIdentity(String memberId);

    //알림 수신 여부 변경
    void updateFcmConsent(String memberId, boolean consent);

    //알림 수신 여부 조회
    boolean getFcmConsent(String memberId);

    // 내가 등록한 매물 리스트 조회
    List<BuildingImg> getMyBuildings(String memberId);
}
package bjs.zangbu.member.mapper;

import bjs.zangbu.member.dto.join.BookmarkBuilding;
import bjs.zangbu.security.account.vo.Member;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberMapper {
    //마이페이지--------------------------------------------------------------------
    //1. 회원검색 - 마이페이지, 프로필 정보 보여줄 때
    Member get(String memberId);

    //2. member id별 북마크 되어있는 building 리스트 가져오기
    List<BookmarkBuilding> getBookmarksByMemberId(String memberId);

    //3. 북마크 삭제하기
    void deleteBookMark(String memberId, Long buildingId);

    //4. member id 받고 닉네임 가져오기
    String getNicknameByMemberId(String memberId);

    //회원 정보 수정----------------------------------------------------------------------
    //1. 비밀번호 변경
    //1-1. 기존 비밀번호 가져오기
    String findPasswordByMemberId(String memberId);
    //1-2. 새 비밀번호로 변경
    int updatePassword(String memberId, String newPassword);

    //2. 닉네임 변경
    //2-1. 닉네임 중복 확인
    int countByNickname(String nickname);
    //2-2. 새 닉네임으로 변경
    int updateNickname(String memberId, String newNickname);

    //3. 회원 탈퇴
    int deleteMemberId(String memberId);

    //알림-----------------------------------------------
    //1. 알림 수신 여부 변경
    void updateFcmConsent(@Param("memberId") String memberId, @Param("consent") boolean consent);

    //2. 알림 수신 여부 조회
    Boolean selectFcmConsentByMemberId(@Param("memberId") String memberId);

    Member findByEmail(String email);
}

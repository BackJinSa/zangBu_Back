package bjs.zangbu.member.mapper;

import bjs.zangbu.member.dto.response.MemberResponse.BookmarkBuilding;

import java.util.List;

public interface MemberMapper {

    //member id별 북마크 되어있는 building 리스트 가져오기
    List<BookmarkBuilding> findBookmarksByMemberId(String memberId);

    //북마크 삭제하기
    void deleteBookMark(String memberId, Long buildingId);

    //기존
}

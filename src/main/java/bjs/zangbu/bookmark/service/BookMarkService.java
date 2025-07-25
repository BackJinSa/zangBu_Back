package bjs.zangbu.bookmark.service;

import java.util.List;

public interface BookMarkService {

    // 특정 건물에 대해 찜하기 등록
    void insertBookMark(String memberId, Long buildingId);

    // 특정 건물에 대해 찜하기 해제
    void deleteBookMark(String memberId, Long buildingId);

    // 사용자가 찜한 모든 건물 ID 목록 조회
    List<Long> selectBookmarkedBuildingIdsByMember(String memberId);
}

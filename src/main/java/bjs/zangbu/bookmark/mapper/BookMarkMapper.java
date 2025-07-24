package bjs.zangbu.bookmark.mapper;

import bjs.zangbu.bookmark.vo.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMarkMapper {
    // 찜한 매물 전체 조회
    List<Bookmark> selectAllBookmarks();

    // buildingId를 통해 그 매물을 찜한 유저 모두 조회
    List<String> selectUserId(Long buildingId);

    // bookmark_id 기준 가격 업데이트
    int updateBookmarkPrice(Long bookmarkId, int newPrice);

    void insertBookMark(String memberId, Long buildingId);

    void deleteBookMark(String memberId, Long buildingId);

    List<Long> selectBookmarkedBuildingIdsByMember(String memberId);
}

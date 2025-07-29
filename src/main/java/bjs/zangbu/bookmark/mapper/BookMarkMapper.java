package bjs.zangbu.bookmark.mapper;

import bjs.zangbu.bookmark.vo.Bookmark;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Bookmark(찜) 관련 DB 매핑 인터페이스 (MyBatis Mapper)
 * 찜한 매물 관리, 조회, 추가, 삭제 기능을 담당합니다.
 */
@Mapper
public interface BookMarkMapper {

    /**
     * 모든 찜한 매물 정보를 조회합니다.
     *
     * @return 전체 찜 정보 리스트 (Bookmark 객체 리스트)
     */
    List<Bookmark> selectAllBookmarks();

    /**
     * 특정 매물을 찜한 모든 회원 ID를 조회합니다.
     *
     * @param buildingId 찜한 회원을 조회할 매물 ID
     * @return 해당 매물을 찜한 회원 ID 리스트
     */
    List<String> selectUserId(Long buildingId);

    /**
     * 특정 찜(Bookmark)의 가격 정보를 업데이트합니다.
     *
     * @param bookmarkId 가격을 변경할 찜 ID
     * @param newPrice 업데이트할 새로운 가격
     * @return 업데이트 성공 시 영향받은 행 수
     */
    int updateBookmarkPrice(Long bookmarkId, int newPrice);

    /**
     * 특정 회원이 특정 매물을 찜합니다. (찜 추가)
     *
     * @param memberId 찜을 추가할 회원 ID
     * @param buildingId 찜할 매물 ID
     */
    void insertBookMark(String memberId, Long buildingId);

    /**
     * 특정 회원이 특정 매물의 찜을 취소합니다. (찜 삭제)
     *
     * @param memberId 찜을 삭제할 회원 ID
     * @param buildingId 찜을 삭제할 매물 ID
     */
    void deleteBookMark(String memberId, Long buildingId);

    /**
     * 특정 회원이 찜한 모든 매물 ID 목록을 조회합니다.
     *
     * @param memberId 찜한 매물 목록을 조회할 회원 ID
     * @return 회원이 찜한 매물 ID 리스트
     */
    List<Long> selectBookmarkedBuildingIdsByMember(String memberId);

    /**
     * 특정 매물을 찜한 모든 회원 ID를 조회합니다.
     * (selectUserId와 동일 기능, 다만 메서드명 다름)
     *
     * @param buildingId 찜한 회원을 조회할 매물 ID
     * @return 해당 매물을 찜한 회원 ID 리스트
     */
    List<String> selectUserIdsByBuildingId(Long buildingId);
}

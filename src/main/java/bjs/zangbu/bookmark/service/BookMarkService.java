package bjs.zangbu.bookmark.service;

import bjs.zangbu.bookmark.vo.Bookmark;
import java.util.List;

/**
 * 찜하기 기능에 대한 서비스 인터페이스
 */
public interface BookMarkService {

    /**
     * 특정 사용자가 특정 건물을 찜 목록에 추가
     *
     * @param memberId 찜한 사용자 ID
     * @param buildingId 찜할 건물 ID
     */
    void insertBookMark(String memberId, Long buildingId);

    /**
     * 특정 사용자가 특정 건물에 대해 찜 해제
     *
     * @param memberId 찜 해제할 사용자 ID
     * @param buildingId 찜 해제할 건물 ID
     */
    void deleteBookMark(String memberId, Long buildingId);

    /**
     * 특정 사용자가 찜한 모든 건물 ID 목록 조회
     *
     * @param memberId 사용자 ID
     * @return 사용자가 찜한 건물 ID 리스트
     */
    List<Long> selectBookmarkedBuildingIdsByMember(String memberId);

    /**
     * 모든 찜한 매물 정보 조회
     *
     * @return 전체 찜 정보 리스트 (Bookmark 객체 리스트)
     */
    List<Bookmark> selectAllBookmarks();

    /**
     * 특정 매물을 찜한 모든 회원 ID 조회
     *
     * @param buildingId 찜한 회원을 조회할 매물 ID
     * @return 해당 매물을 찜한 회원 ID 리스트
     */
    List<String> selectUserId(Long buildingId);

    /**
     * 특정 찜(Bookmark)의 가격 정보를 업데이트
     *
     * @param bookmarkId 가격을 변경할 찜 ID
     * @param newPrice 업데이트할 새로운 가격
     * @return 업데이트 성공 시 영향받은 행 수
     */
    int updateBookmarkPrice(Long bookmarkId, int newPrice);

    /**
     * 특정 매물을 찜한 모든 회원 ID 조회 (메서드명만 다름)
     *
     * @param buildingId 찜한 회원을 조회할 매물 ID
     * @return 해당 매물을 찜한 회원 ID 리스트
     */
    List<String> selectUserIdsByBuildingId(Long buildingId);
}

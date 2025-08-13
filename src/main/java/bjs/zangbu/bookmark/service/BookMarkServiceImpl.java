package bjs.zangbu.bookmark.service;

import bjs.zangbu.bookmark.mapper.BookMarkMapper;
import bjs.zangbu.bookmark.vo.Bookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 찜하기 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class BookMarkServiceImpl implements BookMarkService {

    /** 찜하기 매퍼 객체 주입 */
    private final BookMarkMapper bookMarkMapper;

    /**
     * 특정 사용자가 특정 건물을 찜 목록에 추가합니다.
     *
     * @param memberId 찜한 사용자 ID
     * @param buildingId 찜할 건물 ID
     */
    @Override
    public void insertBookMark(String memberId, Long buildingId, Long complexId, Integer price) {
        bookMarkMapper.insertBookMark(memberId, buildingId, complexId, price);
    }

    /**
     * 특정 사용자가 특정 건물에 대해 찜을 해제합니다.
     *
     * @param memberId 찜 해제할 사용자 ID
     * @param buildingId 찜 해제할 건물 ID
     */
    @Override
    public void deleteBookMark(String memberId, Long buildingId) {
        bookMarkMapper.deleteBookMark(memberId, buildingId);
    }

    /**
     * 특정 사용자가 찜한 모든 건물 ID 목록을 조회합니다.
     *
     * @param memberId 사용자 ID
     * @return 사용자가 찜한 건물 ID 리스트
     */
    @Override
    public List<Long> selectBookmarkedBuildingIdsByMember(String memberId) {
        return bookMarkMapper.selectBookmarkedBuildingIdsByMember(memberId);
    }

    /**
     * 모든 찜한 매물 정보를 조회합니다.
     *
     * @return 전체 찜 정보 리스트 (Bookmark 객체 리스트)
     */
    @Override
    public List<Bookmark> selectAllBookmarks() {
        return bookMarkMapper.selectAllBookmarks();
    }

    /**
     * 특정 매물을 찜한 모든 회원 ID를 조회합니다.
     *
     * @param buildingId 찜한 회원을 조회할 매물 ID
     * @return 해당 매물을 찜한 회원 ID 리스트
     */
    @Override
    public List<String> selectUserId(Long buildingId) {
        return bookMarkMapper.selectUserId(buildingId);
    }

    /**
     * 특정 찜(Bookmark)의 가격 정보를 업데이트합니다.
     *
     * @param bookmarkId 가격을 변경할 찜 ID
     * @param newPrice 업데이트할 새로운 가격
     * @return 업데이트 성공 시 영향받은 행 수
     */
    @Override
    public int updateBookmarkPrice(Long bookmarkId, int newPrice) {
        return bookMarkMapper.updateBookmarkPrice(bookmarkId, newPrice);
    }

    /**
     * 특정 매물을 찜한 모든 회원 ID를 조회합니다. (selectUserId와 기능 동일)
     *
     * @param buildingId 찜한 회원을 조회할 매물 ID
     * @return 해당 매물을 찜한 회원 ID 리스트
     */
    @Override
    public List<String> selectUserIdsByBuildingId(Long buildingId) {
        return bookMarkMapper.selectUserIdsByBuildingId(buildingId);
    }

    @Override
    public boolean isBookmarked(Long buildingId, String memberId) {
        return bookMarkMapper.isBookmarked(memberId, buildingId);
    }
}

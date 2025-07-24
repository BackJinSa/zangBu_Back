package bjs.zangbu.bookmark.service;

import bjs.zangbu.bookmark.mapper.BookMarkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookMarkServiceImpl implements BookMarkService {
    private final BookMarkMapper bookMarkMapper;

    // 찜하기 등록 처리
    @Override
    public void insertBookMark(String memberId, Long buildingId) {
        bookMarkMapper.insertBookMark(memberId, buildingId);
    }

    // 찜하기 삭제 처리
    @Override
    public void deleteBookMark(String memberId, Long buildingId) {
        bookMarkMapper.deleteBookMark(memberId, buildingId);
    }

    // 사용자의 찜한 건물 ID 목록 반환
    @Override
    public List<Long> selectBookmarkedBuildingIdsByMember(String memberId) {
        return bookMarkMapper.selectBookmarkedBuildingIdsByMember(memberId);
    }
}

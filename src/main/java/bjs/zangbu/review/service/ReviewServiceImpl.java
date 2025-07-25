package bjs.zangbu.review.service;

import bjs.zangbu.review.dto.response.ReviewListResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewListResult listReviews(Long buildingId, int page, int size) {
        int offset = page * size;

        // 페이징된 리뷰 목록
        List<ReviewListResponse> list = reviewMapper.selectByBuilding(buildingId, offset, size);
        // 전체 리뷰 수
        long total = reviewMapper.countByBuilding(buildingId);
        // 다음 페이지 존재 여부 확인
        boolean hasNext = (offset + list.size()) < total;
        // 가장 최근 리류의 rank 확인(없으면 null 처리)
        Integer latestRank = reviewMapper.selectLatestReviewRank(buildingId);
        return new ReviewListResult(total, list, hasNext,latestRank);
    }
}

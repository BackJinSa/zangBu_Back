package bjs.zangbu.review.service;

import bjs.zangbu.review.dto.response.ReviewListResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.mapper.ReviewMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewListResult listReviews(Long buildingId, int page, int size) {
        int pageNum = page + 1;

        // 페이징 설정 (자동으로 sql에 limit, offset 추가)
        PageHelper.startPage(pageNum, size);

        // 매퍼 호출: 페이징 적용된 목록을 반환
        List<ReviewListResponse> list = reviewMapper.selectByBuilding(buildingId);

        // PageInfo로 메타데이터(전체 건수, 다음 페이지 존재 여부 등) 획득
        PageInfo<ReviewListResponse> pageInfo = new PageInfo<>(list);
        long total = pageInfo.getTotal();
        boolean hasNext = pageInfo.isHasNextPage();

        // 최신 리뷰 평점 조회
        Integer latestRank = reviewMapper.selectLatestReviewRank(buildingId);

        // 최종 결과 조립
        return new ReviewListResult(total, list, hasNext, latestRank);
    }
}

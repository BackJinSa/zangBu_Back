package bjs.zangbu.review.service;

import bjs.zangbu.notification.service.NotificationService;
import bjs.zangbu.review.dto.request.ReviewCreateRequest;
import bjs.zangbu.review.dto.response.ReviewCreateResponse;
import bjs.zangbu.review.dto.response.ReviewDetailResponse;
import bjs.zangbu.review.dto.response.ReviewListResponse;
import bjs.zangbu.review.dto.response.ReviewListResult;
import bjs.zangbu.review.exception.ReviewNotFoundException;
import bjs.zangbu.review.mapper.ReviewInsertParam;
import bjs.zangbu.review.mapper.ReviewMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewMapper reviewMapper;
    private final NotificationService notificationService;

    // 날짜 형식 설정
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    // 리뷰 상세보기
    @Override
    public ReviewDetailResponse getReviewDetail(Long reviewId) {
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException("존재하지 않는 리뷰 식별자입니다.");
        }
        ReviewDetailResponse detail = reviewMapper.selectById(reviewId);
        if (detail == null) {
            throw new ReviewNotFoundException(reviewId);
        }
        return detail;
    }

    // 리뷰 작성
    @Override
    public ReviewCreateResponse createReview(ReviewCreateRequest req, String userId, String nickname) {
        if (req.getBuildingId() == null ||
                req.getRank() == null ||
                req.getRank() < 1 ||
                req.getRank() > 5) {
            throw new IllegalArgumentException("리뷰 작성에 실패했습니다."); // 400
        }

        // complexId를 요청에서 받거나, buildingId로 조회하여 보정
        Long complexId = req.getComplexId();
        if (complexId == null) {
            complexId = reviewMapper.selectComplexIdByBuildingId(req.getBuildingId());
        }
        if (complexId == null) {
            throw new IllegalArgumentException("리뷰 작성에 실패했습니다.");
        }

        ReviewInsertParam param = new ReviewInsertParam();
        param.setBuildingId(req.getBuildingId());
        param.setMemberId(userId);
        param.setComplexId(complexId);
        param.setReviewerNickname(nickname);
        param.setRank(req.getRank());
        param.setContent(req.getContent());

        reviewMapper.insertReview(param);
        Long newId = param.getReviewId();

        String createdAt = DF.format(java.time.LocalDateTime.now());
        notificationService.notificationReviewRegisterd(req.getBuildingId());
        return new ReviewCreateResponse(
                newId,
                req.getBuildingId(),
                nickname,
                req.getFloor(),
                req.getRank(),
                req.getContent(),
                createdAt);
    }

    // 리뷰 삭제
    @Override
    public void deleteReview(Long reviewId) {
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException("리뷰 삭제에 실패했습니다.");
        }
        int deleted = reviewMapper.deleteReview(reviewId);
        if (deleted == 0) {
            throw new ReviewNotFoundException(reviewId);
        }
    }
}

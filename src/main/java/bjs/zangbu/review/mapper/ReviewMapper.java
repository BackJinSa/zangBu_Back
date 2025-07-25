package bjs.zangbu.review.mapper;

import bjs.zangbu.review.dto.response.ReviewListResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<ReviewListResponse> selectByBuilding(@Param("buildingId") long buildingId);

    Long countByBuilding(@Param("building") Long buildingId);

    // 최신 리뷰의 별점 가져오기
    Integer selectLatestReviewRank(@Param("buildingId") Long buildingId);
}

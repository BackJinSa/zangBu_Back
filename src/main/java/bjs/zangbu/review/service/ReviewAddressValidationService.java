package bjs.zangbu.review.service;

/**
 * 리뷰 작성 시 주소 검증을 위한 서비스 인터페이스
 */
public interface ReviewAddressValidationService {

    /**
     * 사용자가 리뷰를 작성하려는 건물의 주소와 주민등록초본에 기록된 주소가 일치하는지 검증
     * 
     * @param memberId   사용자 ID
     * @param buildingId 리뷰를 작성하려는 건물 ID
     * @return 주소가 일치하면 true, 일치하지 않으면 false
     */
    boolean validateAddressForReview(String memberId, Long buildingId);
}

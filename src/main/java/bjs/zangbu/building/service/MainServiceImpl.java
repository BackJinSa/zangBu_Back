package bjs.zangbu.building.service;

import bjs.zangbu.building.dto.response.MainResponse.*;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 메인 페이지 관련 비즈니스 로직을 구현한 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    /** 건물 관련 DB 작업을 처리하는 매퍼 */
    private final BuildingMapper buildingMapper;
    /** 멤버(회원) 정보 관련 서비스 */
    private final MemberService memberService;

    /**
     * 주어진 멤버 ID에 대한 메인 페이지 정보를 구성하여 반환합니다.
     * <ul>
     *     <li>회원 닉네임 조회</li>
     *     <li>리뷰가 많은 건물, 좋아요가 많은 건물, 신규 매물 조회</li>
     *     <li>조회 결과를 MainPageResponse 형태로 변환</li>
     * </ul>
     *
     * @param memberId 조회할 회원의 ID
     * @return 메인 페이지에 표시할 정보를 담은 DTO
     */
    @Override
    public MainPageResponse mainPage(String memberId) {
        String nickName = memberService.getNickname(memberId); // 닉네임 조회
        List<BuildingInfo> topReviewed = buildingMapper.selectTopReviewedBuildings(memberId); // 리뷰 최상위 건물
        List<BuildingInfo> topLiked = buildingMapper.selectTopLikedBuildings(memberId); // 좋아요 최상위 건물
        List<BuildingInfo> newRooms = buildingMapper.selectNewRooms(memberId); // 신규 매물 목록
        return MainPageResponse.toDto(nickName, topReviewed, topLiked, newRooms);
    }
}

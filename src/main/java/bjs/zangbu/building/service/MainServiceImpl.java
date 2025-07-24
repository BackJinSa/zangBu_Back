package bjs.zangbu.building.service;
import bjs.zangbu.building.dto.response.MainResponse.*;
import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.member.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final BuildingMapper buildingMapper;
    private final UserMapper userMapper;
    @Override
    public MainPageResponse mainPage(String memberId) {
        String nickName= userMapper.getNicknameByMemberId(memberId);
        List<BuildingInfo> topReviewed = buildingMapper.selectTopReviewedBuildings(memberId);
        List<BuildingInfo> topLiked = buildingMapper.selectTopLikedBuildings(memberId);
        List<BuildingInfo> newRooms = buildingMapper.selectNewRooms(memberId);
        return MainPageResponse.toDto(nickName, topReviewed, topLiked, newRooms);
    }
}

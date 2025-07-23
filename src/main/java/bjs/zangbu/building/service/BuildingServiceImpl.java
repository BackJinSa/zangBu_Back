package bjs.zangbu.building.service;
import bjs.zangbu.building.filter.BuildingFilter;
import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.*;
import bjs.zangbu.building.mapper.BuildingMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {
    private final CodefService codefService;
    private final BuildingMapper buildingMapper;
    private final BuildingFilter buildingFilter;
    @Override
    public ViewDetailResponse viewDetailService(ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {

        buildingFilter.validateBuildingExists(request.getBuildingId());
        // CODEF 서비스로부터 응답 JSON 문자열 받아오기
        String jsonResponse = codefService.priceInformation(request);
        // 받은 응답을 dto 값으로 변환하기 위해 toDto로 요청을 보낸다.
        ViewDetailResponse response = CodefConverter.parseDataToDto(jsonResponse, ViewDetailResponse.class);
        return response;
    }

    @Override
    public void bookMarkService(BookmarkRequest request) {
        buildingFilter.validateBuildingExists(request.getBuildingId());
        buildingMapper.insertBookMark( ,request.getBuildingId());
        buildingMapper.incrementBookmarkCount(request.getBuildingId());
    }

    @Override
    public void bookMarkServiceDelete(Long buildingId) {
        buildingFilter.validateBuildingExists(buildingId);
        buildingMapper.deleteBookMark(, buildingId);
    }
}

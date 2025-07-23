package bjs.zangbu.building.service;
import bjs.zangbu.building.dto.request.BuildingRequest.*;
import bjs.zangbu.building.dto.response.BuildingResponse.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UnsupportedEncodingException;

public interface BuildingService {
    ViewDetailResponse viewDetailService(ViewDetailRequest request) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;
}

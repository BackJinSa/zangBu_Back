package bjs.zangbu.codef.service;
import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.codef.encryption.CodefEncryption;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CodefServiceImpl implements CodefService {
    private final CodefEncryption codefEncryption;
    private EasyCodef codef;

    @PostConstruct
    public void init() {
        codef = codefEncryption.getCodefInstance();
    }

    @Override
    public String priceInformation(BuildingRequest.ViewDetailRequest request) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("organization", "0011");
        map.put("searchGbn", request.getSearchGbn());
        map.put("complexNo", request.getComplexNo());
        map.put("dong", request.getDong());
        map.put("ho", request.getHo());
        List<HashMap<String, Object>> buildingList = new ArrayList<>();
        buildingList.add(map);
        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("buildingList", buildingList);
        String url = "/v1/kr/public/lt/real-estate-board/market-price-information";
        String response = codef.requestProduct(url, EasyCodefServiceType.DEMO, parameterMap);
        return response;
    }
}

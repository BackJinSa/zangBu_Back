package bjs.zangbu.publicdata.service.managecost.publicuse;

import bjs.zangbu.publicdata.dto.managecost.publicuse.CleanCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.EduCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.EtcCost;
import bjs.zangbu.publicdata.dto.managecost.publicuse.VehicleCost;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PublicUseServiceImpl implements PublicUseService{
    private final RestTemplate rt;

    @Value("${publicdata.commonuse.serviceKey}")
    private String serviceKey;

    private Map<String,Object> fetchItem(String baseUrl, String kaptCode, String searchDate) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("kaptCode", kaptCode)
                .queryParam("searchDate", searchDate)
                .build().encode().toUri();

        @SuppressWarnings("unchecked")
        Map<String,Object> resp     = rt.getForObject(uri, Map.class);
        Map<String,Object> response = (Map<String,Object>) resp.get("response");
        Map<String,Object> body     = (Map<String,Object>) response.get("body");
        return (Map<String,Object>) body.get("item");
    }

    @Override
    public VehicleCost fetchVehicleCost(String kaptCode, String searchDate) {
        Map<String,Object> item = fetchItem(
                "https://apis.data.go.kr/1613000/AptCmnuseManageCostServiceV2/getHsmpVhcleMntncCostInfoV2",
                kaptCode, searchDate);

        VehicleCost v = new VehicleCost();
        v.setKaptCode((String)item.get("kaptCode"));
        v.setKaptName((String)item.get("kaptName"));
        v.setFuelCost    (Long.valueOf(item.get("fuelCost").toString()));
        v.setRefairCost  (Long.valueOf(item.get("refairCost").toString()));
        v.setCarInsurance(Long.valueOf(item.get("carInsurance").toString()));
        v.setCarEtc      (Long.valueOf(item.get("carEtc").toString()));
        return v;
    }

    @Override
    public EtcCost fetchEtcCost(String kaptCode, String searchDate) {
        Map<String,Object> item = fetchItem(
                "https://apis.data.go.kr/1613000/AptCmnuseManageCostServiceV2/getHsmpEtcCostInfoV2",
                kaptCode, searchDate);

        EtcCost e = new EtcCost();
        e.setKaptCode      ((String)item.get("kaptCode"));
        e.setKaptName      ((String)item.get("kaptName"));
        e.setCareItemCost  (Long.valueOf(item.get("careItemCost").toString()));
        e.setAccountingCost(Long.valueOf(item.get("accountingCost").toString()));
        e.setHiddenCost    (Long.valueOf(item.get("hiddenCost").toString()));
        return e;
    }

    @Override
    public EduCost fetchEduCost(String kaptCode, String searchDate) {
        Map<String,Object> item = fetchItem(
                "https://apis.data.go.kr/1613000/AptCmnuseManageCostServiceV2/getHsmpEduTraingCostInfoV2",
                kaptCode, searchDate);

        EduCost u = new EduCost();
        u.setKaptCode((String)item.get("kaptCode"));
        u.setKaptName((String)item.get("kaptName"));
        u.setEduCost (Long.valueOf(item.get("eduCost").toString()));
        return u;
    }

    @Override
    public CleanCost fetchCleanCost(String kaptCode, String searchDate) {
        Map<String,Object> item = fetchItem(
                "https://apis.data.go.kr/1613000/AptCmnuseManageCostServiceV2/getHsmpCleaningCostInfoV2",
                kaptCode, searchDate);

        CleanCost c = new CleanCost();
        c.setKaptCode((String)item.get("kaptCode"));
        c.setKaptName((String)item.get("kaptName"));
        c.setCleanCost(Long.valueOf(item.get("cleanCost").toString()));
        return c;
    }
}

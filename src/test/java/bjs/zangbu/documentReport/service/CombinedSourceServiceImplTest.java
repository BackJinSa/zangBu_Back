package bjs.zangbu.documentReport.service;

import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.mongo.Dao.ReportDocumentDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;




@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
@Log4j2 // 필요 시
class CombinedSourceServiceImplTest {

    @Autowired
    DocumentToMongoService documentToMongoService;
    @Autowired
    ReportDocumentDao reportDocumentDao;
    @Autowired CombinedSourceService combinedSourceService;
    @Autowired ReportGenerationService reportGenerationService;
    @Autowired
    ObjectMapper om;

    private String read(String name) throws Exception {
        var res = new ClassPathResource(name);
        return new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Mongo 원본 → Combined → Gemini 리포트 생성")
    void generateReport_fromMongo_success() throws Exception {
        // --- 준비: 테스트 식별자
        final String memberId  = "it-member-001";
        final Long   buildingId = 112233L;

        // --- 1) 리소스 JSON 로드 → DTO 파싱
        String regJson = read("등기부등본-원본포함-0811.json");
        String brJson  = read("건축물대장2.json");

        var regDto = CodefConverter.parseDataToDto(regJson, EstateRegistrationResponse.class);
        var brDto  = CodefConverter.parseDataToDto(brJson,  BuildingRegisterResponse.class);
        assertNotNull(regDto);
        assertNotNull(brDto);

        // --- 2) Mongo 업서트 (resOriGinalData 제거는 서비스 내부에서 처리됨)
        documentToMongoService.saveJson(memberId, buildingId, DocumentType.ESTATE,            regDto);
        documentToMongoService.saveJson(memberId, buildingId, DocumentType.BUILDING_REGISTER, brDto);

        // --- 3) Mongo 저장 확인(필수는 아님, sanity check)
        Document d1 = reportDocumentDao.findOne(memberId, buildingId, DocumentType.ESTATE.name());
        Document d2 = reportDocumentDao.findOne(memberId, buildingId, DocumentType.BUILDING_REGISTER.name());
        assertNotNull(d1);
        assertNotNull(d2);
        assertNotNull(d1.get("parsed"));
        assertNotNull(d2.get("parsed"));

        // --- 4) inputs 구성 (프롬프트 규칙에서 요구하는 값)
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("market_price",        700_000_000L); // 시세
        inputs.put("my_deposit",          100_000_000L); // 내 보증금
        inputs.put("other_senior_claims", 0L);           // 기타 선순위

        // --- 5) (선택) Combined JSON 확인
        Map<String, Object> combined = combinedSourceService.buildCombined(memberId, buildingId, inputs);
        assertNotNull(combined);
        System.out.println("combined = " + combined);
    }

}
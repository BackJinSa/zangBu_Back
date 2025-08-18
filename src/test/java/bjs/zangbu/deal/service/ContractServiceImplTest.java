package bjs.zangbu.deal.service;

import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.service.CodefTwoFactorService;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.dto.response.DealResponse;
import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import bjs.zangbu.deal.util.PdfUtil;
import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.documentReport.service.DocumentToMongoService;
import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.mongo.Dao.ReportDocumentDao;
import bjs.zangbu.ncp.service.Base64UploaderService;
import bjs.zangbu.ncp.service.BinaryUploaderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.BUCKET_NAME;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
@Log4j2
class ContractServiceImplTest {
    @Autowired
    private CodefTwoFactorService codefTwoFactorService;
    @Autowired
    private BinaryUploaderService binaryUploaderService;
    @Autowired
    private DocumentToMongoService documentToMongoService;
    @Autowired
    private ReportDocumentDao reportDocumentDao;

    @BeforeEach
    public void setUp() {
    }

    @Disabled
    @DisplayName("건축물대장 api 테스트")
    public String generateRegisterPdf() throws Exception {
        BuildingRegisterRequest request = BuildingRegisterRequest.builder()
                .userName("전경환")
                .identity("bQvUpPc1lO+khOzXaUXUwIZXddmE+dSpOT7JErdq11yUgpSoqte9/lG+HQZk7G1KPL5CTuywcUqPfHLHo7KmmPW47Rf7fUXWjbojl5ax1K7JTYYIq0dv0RAfRfNLVqR5EPYAbMXjOVN3zwLFdbELKEfs2c7BzFWyxt4mxXe3O8Srtjo0HgHmrzwuhcrfZIeAa/gH5FUyOoILyG7SfvvvipQqtLzCPwoIRUGUIscEZI78c8o9GUvdBEliPVapKzHZTgiEYYia45IL2Lq5giG0qrgmSthXU/HlO/eFjATE7dqzxEIbb85tScMyDiMC5oUqfB/c3RFAlV4gE3snl6I9Tg==")
                .birthDate("981207")
                .phoneNo("01093687950")
                .address("경기도 고양시 덕양구 오부자로 14")
                .dong("104")
                .ho("1201")
                .telecom("0")
                .zipCode("10583")
                .build();
        String result = codefTwoFactorService.generalBuildingLeader(request);
        assertNotNull(result, "API 응답은 null이 아니어야 합니다.");
        assertTrue(result != null && !result.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(result.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");
        System.out.println("통합 테스트 성공! Codef API 응답:\n");
        return result;
    }

    @Disabled
    @Test
    @DisplayName("건축물대장 response to ncp pdf url")
    public void brToNcp() throws Exception {
        String resultJson = generateRegisterPdf();

        // 1) JSON → DTO
        BuildingRegisterResponse dto =
                CodefConverter.parseDataToDto(resultJson, BuildingRegisterResponse.class);
        assertNotNull(dto, "DTO가 null이면 안 됩니다.");

        // (선택) 몇 개 필드 sanity check
        assertNotNull(dto.getResBuildingName(), "건물명 파싱 실패(Null).");
        // assertEquals("지축역북한산유보라", dto.getResBuildingName()); // 실제 값 고정 검증하고 싶으면

        // 2) ★ PDF Base64는 data 레벨의 resOriGinalData에서 바로 꺼낸다
        String base64Pdf = dto.getResOriGinalData();
        System.out.println("base64Pdf = " + base64Pdf);
        assertNotNull(base64Pdf, "data.resOriGinalData가 null이면 안 됩니다.");
        assertFalse(base64Pdf.isBlank(), "data.resOriGinalData가 비어 있으면 안 됩니다.");

        // 3) 디코드 & 업로드
        byte[] pdfBytes = PdfUtil.decodePdfBytes(base64Pdf);
        System.out.println("pdfBytes = " + pdfBytes);
        assertNotNull(pdfBytes, "디코딩 결과가 null이면 안 됩니다.");

        String objectName = "building-register/building_test12345/test_12345.pdf";
        String url = binaryUploaderService.putPdfObject(BUCKET_NAME, objectName, pdfBytes);
//        String url = base64UploaderService.uploadBase64Pdf(BUCKET_NAME, key, base64Pdf);
        assertNotNull(url, "업로드 URL이 null이면 안 됩니다.");
        System.out.println("url = " + url);
    }


    // 리소스 JSON 읽기 (파일명: src/test/resources/등기부등본-원본포함-0811.json)
    private String readJson(String name) {
        try (InputStream is = new ClassPathResource(name).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("테스트 리소스 로딩 실패: " + name, e);
        }
    }

    @Test
    @DisplayName("등기부 등본 mongo 테스트")
    public void EstateToMongo() throws Exception {
        // 1) 입력값 (테스트 전용 식별자)
        final String memberId = "test-member-json-only";
        final Long buildingId = 112233L;
        final DocumentType docType = DocumentType.ESTATE;

        // 2) 리소스 JSON 로드 → DTO 파싱
        String json = readJson("등기부등본-원본포함-0811.json");
        assertNotNull(json);
        EstateRegistrationResponse dto =
                CodefConverter.parseDataToDto(json, EstateRegistrationResponse.class);
        assertNotNull(dto, "DTO 파싱 실패(null)");

        // 3) Mongo에 JSON 업서트 (resOriGinalData는 내부에서 제거됨)
        documentToMongoService.saveJson(memberId, buildingId, docType, dto);

        // 4) 저장 검증 (parsed 존재, pdf는 아직 없음)
        Document found = reportDocumentDao.findOne(memberId, buildingId, docType.name());
        assertNotNull(found, "Mongo에 문서가 저장되어야 합니다.");
        assertEquals(memberId, found.getString("memberId"));
        assertEquals(buildingId, found.getLong("buildingId"));
        assertEquals(docType.name(), found.getString("docType"));

        // parsed(JSON) 저장 확인
        assertNotNull(found.get("parsed"), "parsed(JSON)가 저장되어야 합니다.");

        // pdf 메타는 아직 설정하지 않음 (업로드 안 했으므로 null 또는 미존재)
        Object pdfMeta = found.get("pdf");
        assertTrue(pdfMeta == null || pdfMeta instanceof Document && ((Document) pdfMeta).isEmpty(),
                "업로드를 안 했으니 pdf 메타가 없어야 정상");
    }
    @Test
    @DisplayName("건축물대장 mongo 테스트")
    public void BrToMongo() throws Exception {
        // 1) 입력값 (테스트 전용 식별자)
        final String memberId = "test-member-json-only";
        final Long buildingId = 112233L;
        final DocumentType docType = DocumentType.BUILDING_REGISTER;

        // 2) 리소스 JSON 로드 → DTO 파싱
        String json = readJson("건축물대장2.json");
        assertNotNull(json);
        BuildingRegisterResponse dto =
                CodefConverter.parseDataToDto(json, BuildingRegisterResponse.class);
        assertNotNull(dto, "DTO 파싱 실패(null)");

        // 3) Mongo에 JSON 업서트 (resOriGinalData는 내부에서 제거됨)
        documentToMongoService.saveJson(memberId, buildingId, docType, dto);

        // 4) 저장 검증 (parsed 존재, pdf는 아직 없음)
        Document found = reportDocumentDao.findOne(memberId, buildingId, docType.name());
        assertNotNull(found, "Mongo에 문서가 저장되어야 합니다.");
        assertEquals(memberId, found.getString("memberId"));
        assertEquals(buildingId, found.getLong("buildingId"));
        assertEquals(docType.name(), found.getString("docType"));

        // parsed(JSON) 저장 확인
        assertNotNull(found.get("parsed"), "parsed(JSON)가 저장되어야 합니다.");

        // pdf 메타는 아직 설정하지 않음 (업로드 안 했으므로 null 또는 미존재)
        Object pdfMeta = found.get("pdf");
        assertTrue(pdfMeta == null || pdfMeta instanceof Document && ((Document) pdfMeta).isEmpty(),
                "업로드를 안 했으니 pdf 메타가 없어야 정상");
    }
}



package bjs.zangbu.deal.service;

import bjs.zangbu.codef.converter.CodefConverter;
import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.codef.service.CodefTwoFactorService;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.deal.dto.request.EstateRegistrationRequest;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import bjs.zangbu.deal.util.PdfUtil;
import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.ncp.service.Base64UploaderService;
import bjs.zangbu.ncp.service.BinaryUploaderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static bjs.zangbu.ncp.auth.Holder.HeaderCreationHolder.BUCKET_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
@Log4j2
class ContractServiceImplTest {
    @Autowired
    private CodefTwoFactorService codefTwoFactorService;
    @Autowired
    private CodefService codefService;
    @Autowired
    private BinaryUploaderService binaryUploaderService;
    @Autowired
    private Base64UploaderService base64UploaderService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
    }


    @DisplayName("건축물대장 api 테스트")
    public String getBuildingRegisterPdf() throws Exception {
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

    @Test
    @DisplayName("건축물대장 response to ncp pdf url")
    public void brToNcp() throws Exception {
        String resultJson = getBuildingRegisterPdf();

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

        String objectName = "building-register/test_12345.pdf";
        String url = binaryUploaderService.putPdfObject(BUCKET_NAME, objectName, pdfBytes);
        assertNotNull(url, "업로드 URL이 null이면 안 됩니다.");
        System.out.println("url = " + url);
    }
//-------------------------------------------------------------------------------------------------------
    @Test
    @DisplayName("등기부등본 api 테스트")
    public void getEstateRegisterPdf() throws Exception {
        EstateRegistrationRequest request = EstateRegistrationRequest.builder()
                .phone("01093687950")
                .birth("981207")
                .sido("경기도")
                .address("경기도 고양시 덕양구 오부자로 14")
                .sigungu("고양시 덕양구")
                .roadName("오부자로")
                .dong("104")
                .ho("1201")
                .build();
        String rawJson = codefService.realEstateRegistrationLeader(request);

        assertNotNull(rawJson, "API 응답은 null이 아니어야 합니다.");
        assertTrue(rawJson != null && !rawJson.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(rawJson.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");

        System.out.println("통합 테스트 성공! \nrawJson = \n" + rawJson );


        // 2. data 노드만 DTO로 파싱
        EstateRegistrationResponse dto =
                CodefConverter.parseDataToDto(rawJson, EstateRegistrationResponse.class);

        // 3. 검증
        assertThat(dto).isNotNull();
        assertThat(dto.getResRegisterEntriesList()).isNotEmpty();

        System.out.println("등록부 항목 개수: " + dto.getResRegisterEntriesList().size());
        System.out.println("첫 번째 등기소명: " +
                dto.getResRegisterEntriesList().get(0).getResPublishRegistryOffice());

        // 2) ★ PDF Base64는 data 레벨의 resOriGinalData에서 바로 꺼낸다
        String base64Pdf = dto.getResOriGinalData();
        System.out.println("base64Pdf = " + base64Pdf);
        assertNotNull(base64Pdf, "data.resOriGinalData가 null이면 안 됩니다.");
        assertFalse(base64Pdf.isBlank(), "data.resOriGinalData가 비어 있으면 안 됩니다.");

        // 3) 디코드 & 업로드
        byte[] pdfBytes = PdfUtil.decodePdfBytes(base64Pdf);
        System.out.println("pdfBytes = " + pdfBytes);
        assertNotNull(pdfBytes, "디코딩 결과가 null이면 안 됩니다.");

        String objectName = "estate-register/test_11111.pdf";
        String url = binaryUploaderService.putPdfObject(BUCKET_NAME, objectName, pdfBytes);
        assertNotNull(url, "업로드 URL이 null이면 안 됩니다.");
        System.out.println("url = " + url);
    }


    @Test
    @DisplayName("목업 데이터로 pdf 업로드 테스트")
    void erToNcp() throws Exception {
        // 1. test_estate.json 읽기
        String resourceName = "등기부등본-원본포함-0811.json";

        InputStream is = getClass().getResourceAsStream("/등기부등본-원본포함-0811.json");


        if (is == null) {
            is = getClass().getResourceAsStream("/" + resourceName);
        }

        // C) 여전히 null이면 파일 경로 문제 — 즉시 실패하며 원인 출력
        assertNotNull(is, "클래스패스에서 " + resourceName + " 을(를) 찾지 못했습니다. "
                + "파일이 src/test/resources/" + resourceName + " 에 있는지 확인하세요.");

        String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        //---------------------------------미리 받아놓은 json으로 테스트 ------------------------
        // 2. data 노드만 DTO로 파싱
        EstateRegistrationResponse dto =
                CodefConverter.parseDataToDto(json, EstateRegistrationResponse.class);

        // 3. 검증
        assertThat(dto).isNotNull();
        assertThat(dto.getResRegisterEntriesList()).isNotEmpty();

        System.out.println("등록부 항목 개수: " + dto.getResRegisterEntriesList().size());
        System.out.println("첫 번째 등기소명: " +
                dto.getResRegisterEntriesList().get(0).getResPublishRegistryOffice());

        // 2) ★ PDF Base64는 data 레벨의 resOriGinalData에서 바로 꺼낸다
        String base64Pdf = dto.getResOriGinalData();
        System.out.println("base64Pdf = " + base64Pdf);
        assertNotNull(base64Pdf, "data.resOriGinalData가 null이면 안 됩니다.");
        assertFalse(base64Pdf.isBlank(), "data.resOriGinalData가 비어 있으면 안 됩니다.");

        // 3) 디코드 & 업로드
        byte[] pdfBytes = PdfUtil.decodePdfBytes(base64Pdf);
//        byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
        System.out.println("pdfBytes = " + pdfBytes);
        assertNotNull(pdfBytes, "디코딩 결과가 null이면 안 됩니다.");

        String objectName = "estate-register/test_11111.pdf";
        String url = binaryUploaderService.putPdfObject(BUCKET_NAME, objectName, pdfBytes);
//        String url = base64UploaderService.uploadBase64Pdf(BUCKET_NAME, objectName, base64Pdf);
        assertNotNull(url, "업로드 URL이 null이면 안 됩니다.");
        System.out.println("url = " + url);
    }
}
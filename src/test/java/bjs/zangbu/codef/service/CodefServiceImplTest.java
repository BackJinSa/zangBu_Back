package bjs.zangbu.codef.service;

import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.building.dto.request.BuildingRequest.BuildingDetails;
import bjs.zangbu.building.dto.request.BuildingRequest.ComplexDetails;
import bjs.zangbu.building.dto.request.BuildingRequest.SaleRegistrationRequest;
import bjs.zangbu.codef.converter.CodefConverter; // CodefConverter import 추가
import bjs.zangbu.global.config.RootConfig;
import bjs.zangbu.security.account.mapper.AuthMapper;
import bjs.zangbu.security.account.vo.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map; // Map 클래스 import 추가
import java.util.UUID;

import static bjs.zangbu.security.account.vo.MemberEnum.ROLE_MEMBER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
class CodefServiceImplTest {

    @Autowired
    private CodefService codefService;

    // 회원가입 테스트를 위한 의존성 추가
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void realEstateRegistrationAddressSearch_shouldReturnValidResponse_whenGivenValidRequest() throws Exception {
        // ComplexDetails 객체를 생성자에 모든 필드를 전달하여 생성
        // 생성자 파라미터 순서: resType, complexName, complexNo, sido, sigungu, siCode,
        // eupmyeondong, transactionId, address, zonecode, buildingName, bname, dong,
        // ho, roadName
        ComplexDetails complexDetails = new ComplexDetails(
                null,
                null,
                null,
                "서울특별시",
                "동대문구",
                null,
                "제기동",
                null,
                "서울특별시 동대문구 왕산로 23길 89",
                "02575",
                null,
                null,
                "101",
                "402",
                "왕산로23길");

        String identity = "0110203018419";
        // SaleRegistrationRequest 객체를 생성자에 BuildingDetails, ComplexDetails,
        // ImageDetails를 전달하여 생성
        SaleRegistrationRequest request = new SaleRegistrationRequest(
                new BuildingDetails(),
                complexDetails,
                null,
                identity);

        // 서비스 메서드 호출
        String result = codefService.realEstateRegistrationAddressSearch(request);

        // API 응답 검증 (기존 로직)
        assertNotNull(result, "API 응답은 null이 아니어야 합니다.");
        assertTrue(!result.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(result.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");

        // CODEF 응답 파싱 및 commUniqueNo 검증 (추가된 로직)
        Map<String, Object> dataMap = CodefConverter.parseDataToDto(result, Map.class);
        String commUniqueNo = (String) dataMap.get("commUniqueNo");

        assertNotNull(commUniqueNo, "응답 데이터에 'commUniqueNo' 필드가 존재해야 합니다.");
        assertFalse(commUniqueNo.isEmpty(), "'commUniqueNo' 필드 값은 비어있지 않아야 합니다.");

        System.out.println("통합 테스트 성공! Codef API 응답:\n" + result);
        System.out.println("추출된 commUniqueNo: " + commUniqueNo);
    }

    // ---

    @Test
    public void realEstateRegistrationRegister_shouldReturnValidResponse_whenGivenValidUniqueNo() throws Exception {
        // 테스트를 위한 더미 데이터
        String dummyUniqueNo = "26412005003057"; // 실제 동작하는 유효한 고유번호로 대체
        String dummyIdentity = "6706011018311";

        // 서비스 메서드 호출
        String response = codefService.RealEstateRegistrationRegister(dummyUniqueNo, dummyIdentity);

        // 응답 검증
        assertNotNull(response, "API 응답은 null이 아니어야 합니다.");
        assertFalse(response.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(response.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");

        // 응답을 파싱하여 특정 필드 값 검증 (선택적)
        // Map<String, Object> dataMap = CodefConverter.parseDataToDto(response,
        // Map.class);
        // String resState = (String) dataMap.get("resState");
        // assertEquals("열람", resState, "resState는 '열람'이어야 합니다.");

        System.out.println("통합 테스트 성공! CODEF API 응답:\n" + response);
    }

    @Test
    public void filterPriceInformation_shouldReturnValidResponse_whenGivenValidRequest() throws Exception {
        // given: ViewDetailRequest를 생성자만으로 생성
        // 생성자 파라미터 예시: buildingId, dong, ho
        Long buildingId = 1L;
        // 실제 DB에 존재하는 buildingId 사용 필요

        // when: 서비스 메서드 호출
        String response = codefService.getBuildingDetail(buildingId);
        System.out.println(response);
        // then: 응답 검증
        assertNotNull(response, "API 응답은 null이 아니어야 합니다.");
        assertFalse(response.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(response.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");

        // 응답 파싱 후 특정 필드 검증
        Map<String, Object> dataMap = CodefConverter.parseDataToDto(response, Map.class);
        System.out.println(dataMap);
        assertTrue(dataMap.containsKey("data"), "응답 데이터에 'data' 필드가 존재해야 합니다.");

        System.out.println("FilterpriceInformation API 응답:\n" + response);
    }

    @Test
    public void getComplexDetailByBuildingId_shouldReturnValidResponse_whenGivenValidBuildingId() throws Exception {
        // given
        Long buildingId = 1L; // 실제 DB에 존재하는 buildingId 사용

        // when
        String response = codefService.getComplexDetailByBuildingId(buildingId);

        // then
        assertNotNull(response, "API 응답은 null이 아니어야 합니다.");
        assertFalse(response.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(response.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");

        // 응답 파싱 후 특정 필드 검증
        Map<String, Object> dataMap = CodefConverter.parseDataToDto(response, Map.class);
        assertTrue(dataMap.containsKey("data"), "응답 데이터에 'data' 필드가 존재해야 합니다.");

        System.out.println("getComplexDetailByBuildingId API 응답:\n" + response);
    }

    @Test
    public void searchByAddress_shouldReturnValidResponse_whenGivenSpecificAddress() throws Exception {
        // given
        ComplexDetails complexDetails = new ComplexDetails(
                null,
                null,
                null,
                "서울특별시",
                "서초구",
                null,
                "신원동",
                null,
                "서울특별시 서초구 신원동 557",
                null,
                null,
                null,
                null,
                null,
                null);

        SaleRegistrationRequest request = new SaleRegistrationRequest(
                new BuildingDetails(),
                complexDetails,
                null,
                "0110203018419" // dummy identity
        );

        // when
        String result = codefService.realEstateRegistrationAddressSearch(request);

        // then
        assertNotNull(result, "API 응답은 null이 아니어야 합니다.");
        assertTrue(!result.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");

        System.out.println("======= 주소 검색 테스트 결과 =======");
        System.out.println(result);
        System.out.println("===================================");
    }

    @Test
    public void getMarketPriceInformation_shouldReturnValidResponse_whenGivenValidBuildingId() throws Exception {
        // given
        Long buildingId = 1L; // 실제 DB에 존재하는 buildingId

        // when
        String response = codefService.priceInformation(buildingId);

        // then
        assertNotNull(response, "API 응답은 null이 아니어야 합니다.");
        assertTrue(response.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");

        System.out.println("======= 시세 정보 조회 테스트 결과 =======");
        System.out.println(response);
        System.out.println("======================================");
    }

    // --- 회원가입 테스트 추가 ---
    @Test
    void insertMember_shouldSaveEncodedPassword() {
        // Given: 회원 정보와 암호화되지 않은 비밀번호를 준비합니다.
        String memberId = UUID.randomUUID().toString();
        String testEmail = "test" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        String testNickname = "testUser" + UUID.randomUUID().toString().substring(0, 8);
        String testPassword = "TestPassword123!";

        // PasswordEncoder를 사용하여 비밀번호를 암호화합니다.
        String encodedPassword = passwordEncoder.encode(testPassword);

        // 암호화된 비밀번호를 포함한 Member 객체를 생성합니다.
        Member member = new Member(
                memberId,
                testEmail,
                encodedPassword, // 암호화된 비밀번호 사용
                "01012345678",
                testNickname,
                "1234567890123",
                ROLE_MEMBER,
                "19900101",
                "홍길동",
                true,
                "SKT");

        // When: 매퍼를 통해 데이터베이스에 삽입합니다.
        int result = authMapper.insertMember(member);

        // Then: 삽입이 성공했는지 확인합니다.
        assertEquals(1, result, "회원 삽입 결과는 1이어야 합니다.");

        // Then: DB에서 회원 정보를 다시 조회하여 값이 올바르게 저장되었는지 확인합니다.
        Member savedMember = authMapper.findByEmail(testEmail);

        assertNotNull(savedMember, "저장된 회원은 null이 아니어야 합니다.");
        assertEquals(member.getEmail(), savedMember.getEmail(), "이메일이 일치해야 합니다.");

        // 원본 비밀번호와 DB에 저장된 암호화된 비밀번호가 일치하는지 검증합니다.
        assertTrue(passwordEncoder.matches(testPassword, savedMember.getPassword()),
                "저장된 비밀번호는 원본 비밀번호와 일치해야 합니다.");
    }
}
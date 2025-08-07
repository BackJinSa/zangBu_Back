package bjs.zangbu.codef.service;

import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.global.config.RootConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
public class CodefTwoFactorServiceImplTest {

    @Autowired
    private CodefTwoFactorService codefTwoFactorService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void generalBuildingLeader_shouldReturnValidResponse_whenGivenValidRequest() throws Exception {
        BuildingRegisterRequest request = BuildingRegisterRequest.builder()
                .userName("백현빈")
                .identity("실제 주민등록번호를 암호화한 값")
                .birthDate("011020")
                .phoneNo("01012345678")
                .address("서울특별시 강남구 테헤란로")
                .dong("101동")
                .ho("1004호")
                .telecom("0")
                .zipCode("06130")
                .build();
        String result = codefTwoFactorService.generalBuildingLeader(request);
        assertNotNull(result, "API 응답은 null이 아니어야 합니다.");
        assertTrue(result != null && !result.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(result.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");
        System.out.println("통합 테스트 성공! Codef API 응답:\n" + result);
    }
}
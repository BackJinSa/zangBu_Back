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
                .identity("FY1GqIOaqzlBwjl/+BJD6fnRm/TtlrR+BzT+VgVqFkBUrSEsVKByQSboZ987sAnoCisosPVyuyPENt6pgd24CFpW6NqQx84bj21QnBbZ+fEnRJ1DC7DtR7twOFMyEhucDcl5eGKFi7SZapjH9w2gL0rsY78aG1G+mBDyjwzelj7YzVV7KzLTSEUzOBmsH+kcX0Snqg5ByTPS8BNvwfC4ypwyF+0JHY8k7q4mSsjEILao2XwwEu7GCTUPzK8AUVev8ZmWPIJSm2+xntGOJDPGfyoZ/8kxR9Cl8aIYVr47ExUpF4YSkPsrloWpqxthy2JiVjGGi41eBSqcVAyIdZgzwg==")
                .birthDate("011020")
                .phoneNo("01075117975")
                .address("서울특별시 동대문구 왕산로 23길 89")
                .dong("101동")
                .ho("402호")
                .telecom("2")
                .zipCode("02575")
                .build();
        String result = codefTwoFactorService.generalBuildingLeader(request);
        assertNotNull(result, "API 응답은 null이 아니어야 합니다.");
        assertTrue(result != null && !result.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(result.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");
        System.out.println("통합 테스트 성공! Codef API 응답:\n" + result);
    }
}
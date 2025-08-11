package bjs.zangbu.addressChange.service;

import bjs.zangbu.addressChange.dto.request.ResRegisterCertRequest;
import bjs.zangbu.addressChange.dto.response.ResRegisterCertResponse;
import bjs.zangbu.addressChange.mapper.AddressChangeMapper;
import bjs.zangbu.addressChange.util.JusoClient;
import bjs.zangbu.addressChange.vo.AddressChange;
import bjs.zangbu.codef.service.CodefTwoFactorService;
import bjs.zangbu.global.config.RootConfig;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@ActiveProfiles("test")
class AddressChangeServiceImplTest {

    @Autowired
    private AddressChangeService addressChangeService;
    @Autowired
    private AddressChangeMapper addressChangeMapper;
    @Autowired
    private CodefTwoFactorService codefTwoFactorService;

    @Test
    @DisplayName("주민등록 초본 → 주소 변경 저장 통합 테스트")
    @Transactional // 테스트 후 롤백
    public void generateAddressChangeIntegrationTest() throws Exception {
        // given: 테스트용 memberId
        String testMemberId = "TEST_MEMBER_" + System.currentTimeMillis();

        ResRegisterCertRequest request = ResRegisterCertRequest.builder()
                .birth("981207")
                .identity("bQvUpPc1lO+khOzXaUXUwIZXddmE+dSpOT7JErdq11yUgpSoqte9/lG+HQZk7G1KPL5CTuywcUqPfHLHo7KmmPW47Rf7fUXWjbojl5ax1K7JTYYIq0dv0RAfRfNLVqR5EPYAbMXjOVN3zwLFdbELKEfs2c7BzFWyxt4mxXe3O8Srtjo0HgHmrzwuhcrfZIeAa/gH5FUyOoILyG7SfvvvipQqtLzCPwoIRUGUIscEZI78c8o9GUvdBEliPVapKzHZTgiEYYia45IL2Lq5giG0qrgmSthXU/HlO/eFjATE7dqzxEIbb85tScMyDiMC5oUqfB/c3RFAlV4gE3snl6I9Tg==")
                .phone("01093687950")
                .name("전경환")
                .telecom("0")
                .memberId("")
                .build();


        // when: 서비스 호출 (내부에서 CODEF 호출 → 파싱 → DB 저장)
        String rawJson = codefTwoFactorService.residentRegistrationCertificate(request);
        assertNotNull(rawJson, "API 응답은 null이 아니어야 합니다.");
        assertTrue(rawJson != null && !rawJson.isEmpty(), "응답 문자열은 비어 있지 않아야 합니다.");
        assertTrue(rawJson.contains("result"), "응답에 'result' 필드가 포함되어야 합니다.");
        System.out.println("통합 테스트 성공! Codef API 응답:\n" + rawJson);

        // and: 저장 로직 실행 (RAW JSON 사용)
        List<ResRegisterCertResponse> preview =
                addressChangeService.generateAddressChangeFromRaw(testMemberId, rawJson);
        // 여기까진 동일-----------------------------------------------------------------------

        // then: 결과 출력
        System.out.println("=== PREVIEW (DB 미저장) ===");
        for (ResRegisterCertResponse r : preview) {
            System.out.printf("[ID:%s] resNumber=%s, addr=%s, moveIn=%s, memberId=%s%n",
                    String.valueOf(r.getAddressChangeId()), // null일 것
                    r.getResNumber(),
                    r.getResUserAddr(),
                    r.getResMoveInDate(),
                    r.getMemberId());
        }
    }


}
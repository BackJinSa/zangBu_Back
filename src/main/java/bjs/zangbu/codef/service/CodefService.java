package bjs.zangbu.codef.service;
import bjs.zangbu.building.dto.request.BuildingRequest;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.deal.dto.request.EstateRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;

/**
 * CODEF 통합 본인확인/부동산/증명서 조회 API 서비스 인터페이스
 * - 각 메서드는 실제 API 호출 시 필요한 주요 파라미터, 예외, 반환 등에 대해 한글로 설명함
 */
public interface CodefService {

    /**
     * (아파트/오피스텔 등) 시세 정보 조회
     * @param request   단지/동/호 등 상세 입력 정보를 담는 DTO
     * @return          CODEF API에서 반환하는 응답(JSON 문자열)
     * @throws UnsupportedEncodingException 인코딩 처리 오류
     * @throws JsonProcessingException      JSON 파싱/변환 오류
     * @throws InterruptedException         스레드 중단 등 내부 오류
     */
    String priceInformation(BuildingRequest.ViewDetailRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    /**
     * 부동산 등기부등본 열람 또는 발급
     * @param request   등기부등본 발급/열람용 입력 파라미터 (실무에선 DTO 사용 권장)
     * @return          CODEF API 응답(JSON 문자열)
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     * @throws InterruptedException
     */
    String realEstateRegistrationLeader(EstateRegistrationRequest request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    // 건축물 대장 열람 및 발급
    String callBuildingRegister(BuildingRegisterRequest request) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;
    /**
     * 3차 인증(보안문자/캡차 등) 처리
     * @param sessionKey 2차 인증까지 완료된 인증/세션 식별자(프론트가 전달)
     * @param secureNo   사용자가 입력한 보안문자/캡차 값
     * @return           CODEF API 응답(JSON 문자열)
     */
    String processSecureNo(String sessionKey, String secureNo);

    /**
     * 부동산 등기부 실명(주민등록번호) 일치 확인
     * @param request   실명 확인용 입력 파라미터(일반적으로 소유자 주민번호 등)
     * @return          CODEF API 응답(JSON 문자열)
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     * @throws InterruptedException
     */
    String RealEstateRegistrationRegister(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    /**
     * 납부내역증명(국세/지방세 등) 발급
     * @param request   납부증명서 관련 요청 파라미터 객체
     * @return          CODEF API 응답(JSON 문자열)
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     * @throws InterruptedException
     */
    /*임시 비활성화*/
//    String certificateOfPayment(Object request)
//            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;
}

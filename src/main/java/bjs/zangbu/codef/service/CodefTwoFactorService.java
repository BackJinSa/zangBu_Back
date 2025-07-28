package bjs.zangbu.codef.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;

/**
 * CODEF 2차 인증 본인확인 기능 통합 서비스 인터페이스
 * - 주민등록 초본, 일반 건축물대장, 실명 진위확인, 지방세 증명 등
 * - 각 메서드는 API 호출에 필요한 파라미터와 체크 예외를 명확히 명시
 */
public interface CodefTwoFactorService {

    /**
     * 주민등록 초본 교부
     * @param request  주민등록 초본 교부를 위한 요청 DTO/파라미터 객체
     * @return         CODEF API 응답(JSON 문자열)
     * @throws UnsupportedEncodingException 인코딩 처리 오류
     * @throws JsonProcessingException      JSON 파싱 오류
     * @throws InterruptedException         내부 스레드 작업 중단 오류
     */
    String residentRegistrationCertificate(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    /**
     * 일반 건축물 대장(소유자 실명확인 포함)
     * @param request  건축물 대장 조회를 위한 요청 DTO/파라미터 객체
     * @return         CODEF API 응답(JSON 문자열)
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     * @throws InterruptedException
     */
    String generalBuildingLeader(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    /**
     * 주민등록증 진위 확인(본인 실명 인증)
     * @param request  진위 확인을 위한 요청 DTO/파라미터 객체
     * @return         CODEF API 응답(JSON 문자열)
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     * @throws InterruptedException
     */
    String residentRegistrationAuthenticityConfirmation(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    /**
     * 지방세 납세증명서(증명서/납부내역) 조회
     * @param request  지방세 증명서를 위한 요청 DTO/파라미터 객체
     * @return         CODEF API 응답(JSON 문자열)
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     * @throws InterruptedException
     */
    String localTaxProof(Object request)
            throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;
}

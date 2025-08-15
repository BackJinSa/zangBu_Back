package bjs.zangbu.documentReport.service;

import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;

/**
 * CODEF 응답 DTO를 받아, 정규화된 키 테이블(estate_keys, building_register_keys)과
 * 라인 테이블(estate_gap/estate_eul/building_register_changes)을 "덮어쓰기" 저장하는 서비스.
 *
 * 사용 타이밍:
 *  - PDF 발급/재발급 직후 (이미 CODEF 호출 + PDF 업로드 이후)
 */
public interface DocumentIngestService {

    /**
     * 등기부 등본: CODEF DTO -> estate_keys / estate_gap / estate_eul 덮어쓰기
     * @param buildingId 빌딩(매물) ID
     * @param dto        CODEF 등기부 DTO (data 노드 파싱 결과)
     */
    void overwriteEstateFromCodef(Long buildingId, EstateRegistrationResponse dto);

    /**
     * 건축물대장: CODEF DTO -> building_register_keys / building_register_changes 덮어쓰기
     * @param buildingId 빌딩(매물) ID
     * @param dto        CODEF 건축물대장 DTO (data 노드 파싱 결과)
     */
    void overwriteBuildingRegisterFromCodef(Long buildingId, BuildingRegisterResponse dto);
}

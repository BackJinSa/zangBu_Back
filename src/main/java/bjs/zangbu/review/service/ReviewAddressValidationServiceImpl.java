package bjs.zangbu.review.service;

import bjs.zangbu.addressChange.mapper.AddressChangeMapper;
import bjs.zangbu.complexList.mapper.ComplexListMapper;
import bjs.zangbu.complexList.vo.ComplexList;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReviewAddressValidationServiceImpl implements ReviewAddressValidationService {

    private final AddressChangeMapper addressChangeMapper;
    private final ComplexListMapper complexListMapper;

    @Override
    public boolean validateAddressForReview(String memberId, Long buildingId) {
        try {
            // 1. buildingId로 건물의 complexId 조회
            Long complexId = complexListMapper.selectComplexIdByBuildingId(buildingId);
            if (complexId == null) {
                log.warn("건물 ID {}에 해당하는 단지 정보를 찾을 수 없습니다.", buildingId);
                return false;
            }

            // 2. complexId로 단지 정보 조회 (주소 정보 포함)
            ComplexList complexInfo = complexListMapper.selectById(complexId);
            if (complexInfo == null) {
                log.warn("단지 ID {}에 해당하는 단지 정보를 찾을 수 없습니다.", complexId);
                return false;
            }

            // 3. memberId로 사용자의 주민등록초본 주소 정보 조회
            List<String> userAddresses = addressChangeMapper.selectUserAddresses(memberId);
            if (userAddresses == null || userAddresses.isEmpty()) {
                log.warn("사용자 {}의 주민등록초본 주소 정보를 찾을 수 없습니다.", memberId);
                return false;
            }

            // 4. 건물 주소와 사용자 주소 비교
            String buildingAddress = buildBuildingAddress(complexInfo);
            log.info("건물 주소: {}", buildingAddress);
            log.info("사용자 주소들: {}", userAddresses);

            return userAddresses.stream()
                    .anyMatch(userAddr -> isAddressMatch(buildingAddress, userAddr));

        } catch (Exception e) {
            log.error("주소 검증 중 오류 발생: memberId={}, buildingId={}", memberId, buildingId, e);
            return false;
        }
    }

    /**
     * 단지 정보를 기반으로 건물 주소 문자열 생성
     */
    private String buildBuildingAddress(ComplexList complexInfo) {
        StringBuilder address = new StringBuilder();

        if (complexInfo.getSido() != null) {
            address.append(complexInfo.getSido());
        }
        if (complexInfo.getSigungu() != null) {
            address.append(" ").append(complexInfo.getSigungu());
        }
        if (complexInfo.getEupmyeondong() != null) {
            address.append(" ").append(complexInfo.getEupmyeondong());
        }
        if (complexInfo.getAddress() != null) {
            address.append(" ").append(complexInfo.getAddress());
        }
        if (complexInfo.getBuildingName() != null) {
            address.append(" ").append(complexInfo.getBuildingName());
        }

        return address.toString().trim();
    }

    /**
     * 두 주소가 일치하는지 확인 (부분 일치 허용)
     */
    private boolean isAddressMatch(String buildingAddress, String userAddress) {
        if (buildingAddress == null || userAddress == null) {
            return false;
        }

        // 주소 정규화 (공백 제거, 소문자 변환)
        String normalizedBuilding = buildingAddress.replaceAll("\\s+", "").toLowerCase();
        String normalizedUser = userAddress.replaceAll("\\s+", "").toLowerCase();

        // 완전 일치
        if (normalizedBuilding.equals(normalizedUser)) {
            return true;
        }

        // 부분 일치 (시/군/구 + 읍/면/동 + 건물명)
        String[] buildingParts = normalizedBuilding.split(" ");
        String[] userParts = normalizedUser.split(" ");

        if (buildingParts.length >= 3 && userParts.length >= 3) {
            // 시/군/구 + 읍/면/동 + 건물명이 모두 일치하는지 확인
            boolean sigunguMatch = buildingParts.length > 0 && userParts.length > 0 &&
                    buildingParts[0].equals(userParts[0]);
            boolean eupmyeondongMatch = buildingParts.length > 1 && userParts.length > 1 &&
                    buildingParts[1].equals(userParts[1]);
            boolean buildingNameMatch = buildingParts.length > 2 && userParts.length > 2 &&
                    buildingParts[2].equals(userParts[2]);

            return sigunguMatch && eupmyeondongMatch && buildingNameMatch;
        }

        return false;
    }
}

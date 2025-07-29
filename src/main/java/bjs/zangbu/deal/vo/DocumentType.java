package bjs.zangbu.deal.vo;

/**
 * 다운로드 대상 서류 종류를 나타내는 단순 열거형
 *
 *  - BUILDING_REGISTER : 건축물대장
 *  - REGISTRY          : 등기부등본
 *  - TAX_CERT          : 납세증명서
 *  - RIGHT_REPORT      : 권리관계보고서
 */
public enum DocumentType {
    BUILDING_REGISTER,
    REGISTRY,
    TAX_CERT,
    RIGHT_REPORT
}
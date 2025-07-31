package bjs.zangbu.documentReport.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * CODEF 등기부 응답의 **data 노드** 중
 *  - 분석에 필요한 필드만 뽑아 놓은 “얇은” DTO.
 *
 * ⚑ 왜 이렇게 나눴나요?
 *  • 원본 JSON은 수백~수천 라인 → 전부 매핑하면 메모리 / GC 부담 ↑
 *  • 필요한 구조만 선언하면 Jackson 이 **해당 부분만** 역직렬화합니다.
 *
 * ⚑ 사용 예
 *  ```java
 *  EstateRegisterData data =
 *        CodefConverter.parseDataToDto(decodedJson, EstateRegisterData.class);
 *  ```
 */
@Getter @Setter
public class EstateRegisterData {

    /* ─────────────────────────────────────────
     *  [1] 단일 필드
     * ───────────────────────────────────────── */

    /** 부동산(건물/단지) 고유 식별번호 */
    private String commUniqueNo;


    /* ─────────────────────────────────────────
     *  [2] 계층 구조
     *      data → resRegisterEntriesList[0] → …
     * ───────────────────────────────────────── */

    /**
     * 등기부 항목(보통 1개) 배열
     *  - JSON 스펙상 배열이므로 List 로 선언
     */
    private List<RegisterEntry> resRegisterEntriesList;

    /* =================================================
     *  아래부터는 중첩(static) 클래스
     *  - Jackson 은 필드명이 JSON 키와 맞으면 자동 바인딩
     * ================================================= */

    /**
     * data.resRegisterEntriesList[*]
     */
    @Getter @Setter
    public static class RegisterEntry {

        /** 요약 테이블(“소유지분현황”, “근저당권 등”) 묶음 */
        private List<RegSum> resRegistrationSumList;

        /** 원본 등기 이력(갑구·을구·표제부) 묶음 */
        private List<RegHistory> resRegistrationHisList;
    }

    /* ───── 요약 테이블 ───── */
    @Getter @Setter
    public static class RegSum {
        /** 블록 제목 예: "(근)저당권 및 전세권 등 (을구)" */
        private String resType;

        /** 행(헤더 + 데이터) */
        private List<ContentsRow> resContentsList;
    }

    /* ───── 원본 등기 항목 ───── */
    @Getter @Setter
    public static class RegHistory {
        /** 구분 예: "갑구", "을구", "표제부" */
        private String resType;

        /** 행(헤더 + 데이터) */
        private List<ContentsRow> resContentsList;
    }

    /* ───── 행(ROW) ───── */
    @Getter @Setter
    public static class ContentsRow {
        /**
         * 행 유형
         *  • "1" → 헤더(제목)
         *  • "2" → 실제 데이터
         */
        private String resType2;

        /** 셀(Detail) 배열 */
        private List<Detail> resDetailList;
    }

    /* ───── 셀(CELL) ───── */
    @Getter @Setter
    public static class Detail {
        /**
         * 셀 값 (원본 그대로)
         *  - 일부 PDF 스캔형 등기부는 ‘&’ 또는 개행으로 값이 구분돼 있으니
         *    후처리 시 split 해야 합니다.
         */
        private String resContents;
    }
}

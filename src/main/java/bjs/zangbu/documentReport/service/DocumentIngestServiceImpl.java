package bjs.zangbu.documentReport.service;

import bjs.zangbu.documentReport.mapper.*;
import bjs.zangbu.documentReport.vo.*;
import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * CODEF 응답 DTO(등기부/건축물대장)를 받아
 *  - 요약 테이블(estate_keys, building_register_keys)
 *  - 라인 테이블(estate_gap, estate_eul, building_register_changes)
 * 에 "덮어쓰기(overwrite)" 저장하는 구현체입니다.
 *
 * 사용 타이밍:
 *  1) 등기/대장 PDF 발급이 끝난 직후 (CODEF 호출 → PDF 업로드 완료 이후)
 *  2) 최신화를 위해 재발급한 경우에도 동일하게 사용하면 됩니다.
 *
 * 설계 포인트(단순/안전):
 *  - 파싱은 '샘플 응답' 기준의 최소 규칙(정규식 몇 개)만 사용합니다.
 *  - 현장 데이터 편차가 큰 경우 if/regex를 추가해서 확장하면 됩니다.
 *  - 덮어쓰기 전략: 키 upsert(있으면 갱신, 없으면 삽입) + 라인 테이블은 buildingId 기준 전체 삭제 후 일괄 삽입(batch).
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class DocumentIngestServiceImpl implements DocumentIngestService {

    // MyBatis Mapper들 (DB 접근)
    private final EstateKeysMapper estateKeysMapper;
    private final EstateGapMapper estateGapMapper;
    private final EstateEulMapper estateEulMapper;
    private final BuildingRegisterKeysMapper buildingRegisterKeysMapper;
    private final BuildingRegisterChangeMapper buildingRegisterChangeMapper;

    // =========================================================
    // 공개 메서드: 외부 서비스에서 호출
    // =========================================================

    /**
     * 등기부 등본 DTO를 파싱하여
     *  - estate_keys (요약)
     *  - estate_gap (갑구)
     *  - estate_eul (을구)
     * 에 덮어씁니다.
     */
    @Override
    @Transactional
    public void overwriteEstateFromCodef(Long buildingId, EstateRegistrationResponse dto) {
        // 1) 키(요약) 1행
        EstateKeys keys = mapEstateKeys(buildingId, dto);

        // 2) 라인 테이블(갑구/을구) 목록 생성
        List<EstateGap> gapList = mapEstateGap(buildingId, dto);
        List<EstateEul> eulList = mapEstateEul(buildingId, dto);

        // 2-1) 리포트 편의를 위해 키 테이블에 선순위 채권최고액/건물만 여부를 요약 저장
        Long firstMaxClaim = calcFirstMaxClaim(eulList);                            // 가장 이른 순위의 채권최고액
        Boolean anyBuildingOnly = eulList.stream().anyMatch(e -> Boolean.TRUE.equals(e.getBuildingOnly())); // 건물만 부기 여부
        keys.setFirstMaxClaim(firstMaxClaim);
        keys.setBuildingOnlyOverall(Boolean.TRUE.equals(anyBuildingOnly));

        // 3) DB 반영 (덮어쓰기)
        //  - 키: upsert (insert or update)
        //  - 라인 테이블: buildingId 기준 전체 삭제 후 새 리스트 일괄 삽입
        estateKeysMapper.upsert(keys);

        estateGapMapper.deleteByBuildingId(buildingId);
        if (!gapList.isEmpty()) {
            estateGapMapper.batchInsert(gapList);
        }

        estateEulMapper.deleteByBuildingId(buildingId);
        if (!eulList.isEmpty()) {
            estateEulMapper.batchInsert(eulList);
        }

        log.info("[overwriteEstateFromCodef] buildingId={} upsert keys + {} gap + {} eul",
                buildingId, gapList.size(), eulList.size());
    }

    /**
     * 건축물대장 DTO를 파싱하여
     *  - building_register_keys (요약)
     *  - building_register_changes (변경이력)
     * 에 덮어씁니다.
     */
    @Override
    @Transactional
    public void overwriteBuildingRegisterFromCodef(Long buildingId, BuildingRegisterResponse dto) {
        // 1) 키(요약) 1행
        BuildingRegisterKeys keys = mapBRKeys(buildingId, dto);

        // 2) 변경 이력 라인
        List<BuildingRegisterChange> changes = mapBRChanges(buildingId, dto);

        // 3) DB 반영
        buildingRegisterKeysMapper.upsert(keys);

        buildingRegisterChangeMapper.deleteByBuildingId(buildingId);
        if (!changes.isEmpty()) {
            buildingRegisterChangeMapper.batchInsert(changes);
        }

        log.info("[overwriteBuildingRegisterFromCodef] buildingId={} upsert keys + {} changes",
                buildingId, changes.size());
    }

    // =========================================================
    // 등기부 매핑(키/갑구/을구)
    // =========================================================

    /**
     * 등기부 요약(estate_keys) 생성
     * - 발급일, 등기소, 주소, 호수, 전유면적, 대지권 정보, 현재 소유자 추정 등을 저장
     */
    private EstateKeys mapEstateKeys(Long buildingId, EstateRegistrationResponse dto) {
        EstateKeys k = new EstateKeys();
        k.setBuildingId(buildingId);

        // 발급일/등기소: entries의 첫 건 기준
        EstateRegistrationResponse.ResRegisterEntries first = safeFirst(dto.getResRegisterEntriesList());
        if (first != null) {
            k.setPublishDate(parseDate8(first.getResPublishDate()));                 // "20250730" → 2025-07-30
            k.setOffice(nullToEmpty(first.getCommCompetentRegistryOffice()));        // "의정부지방법원 ..."
        }

        // 표제부 묶음 추출 (전유부분/대지권/1동의 건물 표시 등)
        List<EstateRegistrationResponse.ResRegistrationHis> hisList = dto.getResRegisterEntriesList()
                .stream()
                .map(EstateRegistrationResponse.ResRegisterEntries::getResRegistrationHisList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // 전유부분의 건물의 표시 → 호수/전유면적
        EstateRegistrationResponse.ResRegistrationHis unitSec = findHis(hisList, "표제부", "전유부분의 건물의 표시");
        if (unitSec != null) {
            Map<String, String> m = flatDetail(unitSec);
            k.setUnit(trim(m.get("2")));                                // "제12층 제1201호"
            k.setExclusiveAreaM2(parseDecimalFromSquareMeter(m.get("3"))); // "84.9884㎡" → 84.9884
        }

        // 대지권의 표시 → 대지권종류/비율
        EstateRegistrationResponse.ResRegistrationHis landSec = findHis(hisList, "표제부", "대지권의 표시");
        if (landSec != null) {
            Map<String, String> m = flatDetail(landSec);
            k.setLandRightType(cleanRightType(m.get("1")));             // "1 소유권대지권" → "소유권대지권"
            k.setLandShareRatio(trim(m.get("2")));                      // "33260분의 60.0539"
        }

        // 1동의 건물의 표시 → 주소 요약(여러 줄 중 첫 줄만 저장)
        EstateRegistrationResponse.ResRegistrationHis addrSec = findHis(hisList, "표제부", "1동의 건물의 표시");
        if (addrSec != null) {
            Map<String, String> m = flatDetail(addrSec);
            String addressBlock = m.get("2");
            String shortAddr = pickFirstNonEmptyLine(addressBlock);     // 첫 번째 유의미한 줄
            k.setFormattedAddress(shortAddr);
        }

        // 현재 소유자명 추정: 갑구의 '소유권이전' 중 마지막 holder_name 우선
        List<EstateGap> gaps = mapEstateGap(buildingId, dto);           // 아래 메서드 재활용
        if (!gaps.isEmpty()) {
            String owner = null;
            for (EstateGap g : gaps) {
                if ("소유권이전".equals(trim(g.getPurpose()))) owner = g.getHolderName();
            }
            if (owner == null) owner = gaps.get(gaps.size() - 1).getHolderName(); // 없으면 가장 마지막 레코드
            k.setOwnerNameCurrent(owner);
        }

        // 등기 고유번호(있으면 저장)
        if (first != null) {
            k.setCommUniqueNo(first.getCommUniqueNo());
        }

        k.setCreatedAt(LocalDateTime.now());
        k.setUpdatedAt(LocalDateTime.now());
        return k;
    }

    /**
     * 갑구(소유권 변동) 리스트 생성
     * - 순위번호/목적/접수/원인/명의인/거래가액(있으면)/비고 등 저장
     * - 정렬을 위해 rank_no의 앞자리 숫자만 뽑아서 rank_order에 저장
     */
    private List<EstateGap> mapEstateGap(Long buildingId, EstateRegistrationResponse dto) {
        List<EstateGap> out = new ArrayList<>();

        for (EstateRegistrationResponse.ResRegisterEntries entry : safeList(dto.getResRegisterEntriesList())) {
            for (EstateRegistrationResponse.ResRegistrationHis his : safeList(entry.getResRegistrationHisList())) {
                if (!"갑구".equals(trim(his.getResType()))) continue; // 소유권에 관한 사항만

                for (EstateRegistrationResponse.ResContents row : safeList(his.getResContentsList())) {
                    if (!"2".equals(trim(row.getResType2()))) continue; // "2"가 실제 데이터 행

                    Map<String, String> m = flatDetail(row);
                    String rankNo = trim(m.get("0"));
                    String purpose = trim(m.get("1"));
                    String receipt = trim(m.get("2"));
                    String cause = trim(m.get("3"));
                    String others = trim(m.get("4"));

                    EstateGap g = new EstateGap();
                    g.setBuildingId(buildingId);
                    g.setRankNo(rankNo);
                    g.setRankOrder(parseRankOrder(rankNo));               // "1-1" → 1
                    g.setPurpose(purpose);
                    g.setReceipt(receipt);
                    g.setCause(cause);

                    // others에서 간단한 정보들 추출(정규식 기반)
                    g.setHolderName(extractHolderName(others));
                    g.setHolderRegnoMasked(extractMaskedRegno(others));
                    g.setHolderAddress(extractAddress(others));
                    g.setPriceIfAny(extractWon(others));                  // "거래가액 금504,980,000원"

                    g.setNotesRaw(others);
                    g.setCreatedAt(LocalDateTime.now());
                    out.add(g);
                }
            }
        }

        // rank_order 오름차순 정렬(null은 뒤로)
        out.sort(Comparator
                .comparing((EstateGap g) -> g.getRankOrder() == null)
                .thenComparing(EstateGap::getRankOrder, Comparator.nullsLast(Integer::compareTo)));
        return out;
    }

    /**
     * 을구(담보/전세권 등) 리스트 생성
     * - 선순위 판단을 위해 rank_order를 함께 저장합니다.
     * - "1-1" 같은 부기 행에서 "건물만" 문구를 만나면 바로 이전 건에 building_only=true 보정합니다.
     */
    private List<EstateEul> mapEstateEul(Long buildingId, EstateRegistrationResponse dto) {
        List<EstateEul> out = new ArrayList<>();

        for (EstateRegistrationResponse.ResRegisterEntries entry : safeList(dto.getResRegisterEntriesList())) {
            for (EstateRegistrationResponse.ResRegistrationHis his : safeList(entry.getResRegistrationHisList())) {
                if (!"을구".equals(trim(his.getResType()))) continue; // 소유권 이외의 권리

                for (EstateRegistrationResponse.ResContents row : safeList(his.getResContentsList())) {
                    String type2 = trim(row.getResType2());
                    Map<String, String> m = flatDetail(row);
                    String rankNo = trim(m.get("0"));

                    if ("2".equals(type2)) { // 정상 데이터 행
                        String purpose = trim(m.get("1"));
                        String receipt = trim(m.get("2"));
                        String cause = trim(m.get("3"));
                        String others = trim(m.get("4"));

                        EstateEul e = new EstateEul();
                        e.setBuildingId(buildingId);
                        e.setRankNo(rankNo);
                        e.setRankOrder(parseRankOrder(rankNo));
                        e.setPurpose(purpose);
                        e.setReceipt(receipt);
                        e.setCause(cause);
                        e.setCreditor(extractCreditor(others));         // "근저당권자 ..."
                        e.setDebtor(extractDebtor(others));             // "채무자 ..."
                        e.setMaxClaimAmount(extractMaxClaim(others));   // "채권최고액 금xxx원"
                        e.setRemarksRaw(others);
                        e.setBuildingOnly(extractBuildingOnly(others)); // "건물만" 문구(안전망)
                        e.setCreatedAt(LocalDateTime.now());
                        out.add(e);
                    }

                    // 부기/주석 행: "1-1" 같이 하이픈 포함 → "건물만" 문구가 있으면 직전 건에 반영
                    if ("2".equals(type2) && (rankNo != null && rankNo.contains("-"))) {
                        String others = trim(m.get("4"));
                        if (others != null && others.contains("건물만")) {
                            if (!out.isEmpty()) {
                                EstateEul prev = out.get(out.size() - 1);
                                prev.setBuildingOnly(Boolean.TRUE);
                                prev.setRemarksRaw(merge(prev.getRemarksRaw(), others));
                            }
                        }
                    }
                }
            }
        }

        // rank_order 오름차순 정렬(null은 뒤로)
        out.sort(Comparator
                .comparing((EstateEul e) -> e.getRankOrder() == null)
                .thenComparing(EstateEul::getRankOrder, Comparator.nullsLast(Integer::compareTo)));
        return out;
    }

    /**
     * 선순위 채권최고액 계산
     * - 정렬된 을구 리스트에서 max_claim_amount가 있는 첫 레코드 금액
     */
    private Long calcFirstMaxClaim(List<EstateEul> eulList) {
        for (EstateEul e : eulList) {
            if (e.getMaxClaimAmount() != null && e.getMaxClaimAmount() > 0) {
                return e.getMaxClaimAmount();
            }
        }
        return null;
    }

    // =========================================================
    // 건축물대장 매핑(키/변경이력)
    // =========================================================

    /**
     * 건축물대장 요약(building_register_keys) 생성
     * - 발급일/기관, 주소(지번/도로명), 단지명, 주용도, 사용승인일,
     *   대지/연면적, 건폐율/용적률, 세대수, 내진, 위반 상태 저장
     */
    private BuildingRegisterKeys mapBRKeys(Long buildingId, BuildingRegisterResponse dto) {
        BuildingRegisterKeys k = new BuildingRegisterKeys();
        k.setBuildingId(buildingId);

        // 발급일/발급기관
        k.setIssueDate(parseDateAny(dto.getResIssueDate()));        // "20250808" 같은 비정형도 처리
        k.setIssuer(nullToEmpty(dto.getResIssueOgzNm()));

        // 주소(지번/도로명) + 단지명
        String lot = joinWithSpace(dto.getResUserAddr(), dto.getCommAddrLotNumber()); // "경기도 ... 지축동 884"
        k.setAddressLot(trim(lot));
        k.setAddressRoad(trim(dto.getCommAddrRoadName()));
        k.setComplexName(trim(dto.getResBuildingName()));

        // 상세 값: resDetailList를 '이름 → 값' 맵으로 변환
        Map<String, String> detail = dto.getResDetailList() == null ? Collections.emptyMap()
                : dto.getResDetailList().stream().collect(Collectors.toMap(
                d -> trim(d.getResType()), d -> trim(d.getResContents()), (a, b) -> a));

        k.setUseMain(detail.getOrDefault("주용도", null));
        k.setUseApprovalDate(parseDateAny(detail.get("사용승인일자")));

        // 숫자/소수 파싱 (쉼표/단위 제거 후 BigDecimal로)
        k.setSiteAreaM2(parseDecimalSafe(detail.get("대지면적")));
        k.setTotalFloorAreaM2(parseDecimalSafe(detail.get("연면적")));
        k.setBuildingCoverageRatio(parseDecimalSafe(detail.get("건폐율")));
        BigDecimal far = parseDecimalSafe(detail.get("용적율"));   // '용적율' 오기 케이스 우선
        if (far == null) far = parseDecimalSafe(detail.get("용적률")); // '용적률' 대체
        k.setFloorAreaRatio(far);

        k.setHouseholdSummary(detail.get("총호수"));
        k.setSeismicApplied(detail.get("내진설계적용여부"));
        k.setSeismicCapacity(detail.get("내진능력"));

        // 위반건축물 상태(빈 값이면 없음으로 간주, LLM에서 "없음"으로 표현)
        k.setViolationStatus(trim(dto.getResViolationStatus()));

        k.setCreatedAt(LocalDateTime.now());
        k.setUpdatedAt(LocalDateTime.now());
        return k;
    }

    /**
     * 건축물대장 변경 이력(building_register_changes) 리스트 생성
     * - 날짜 파서는 8자리/7자리("2024328")/6자리("YYYYMM")까지 방어적으로 처리
     */
    private List<BuildingRegisterChange> mapBRChanges(Long buildingId, BuildingRegisterResponse dto) {
        List<BuildingRegisterChange> out = new ArrayList<>();
        for (BuildingRegisterResponse.Change c : safeList(dto.getResChangeList())) {
            BuildingRegisterChange r = new BuildingRegisterChange();
            r.setBuildingId(buildingId);
            r.setChangeDate(parseDateAny(c.getResChangeDate()));   // "20191031", "2024328" 등 비정형 허용
            r.setChangeReason(trim(c.getResChangeReason()));
            r.setCreatedAt(LocalDateTime.now());
            out.add(r);
        }
        // 변경일 오름차순(날짜 없는 건 뒤로)
        out.sort(Comparator.comparing(
                (BuildingRegisterChange c) -> c.getChangeDate() == null).thenComparing(BuildingRegisterChange::getChangeDate,
                Comparator.nullsLast(LocalDate::compareTo)));
        return out;
    }

    // =========================================================
    // 공통 유틸 (NPE 방어, 파서/정규식)
    // =========================================================

    private <T> T safeFirst(List<T> list) { return (list == null || list.isEmpty()) ? null : list.get(0); }
    private <T> List<T> safeList(List<T> list) { return list == null ? Collections.emptyList() : list; }
    private String trim(String s) { return s == null ? null : s.trim(); }
    private String nullToEmpty(String s) { return s == null ? "" : s; }

    /** 두 문자열을 ' / ' 로 합치되, 중복/빈값 방지 */
    private String merge(String a, String b) {
        if (a == null || a.isEmpty()) return b;
        if (b == null || b.isEmpty()) return a;
        if (a.contains(b)) return a;
        return a + " / " + b;
    }

    /** 표제부 섹션 찾기: resType, resType1 모두 매칭 */
    private EstateRegistrationResponse.ResRegistrationHis findHis(List<EstateRegistrationResponse.ResRegistrationHis> list,
                                                                  String type, String type1) {
        for (EstateRegistrationResponse.ResRegistrationHis h : safeList(list)) {
            if (type.equals(trim(h.getResType())) && type1.equals(trim(h.getResType1()))) {
                return h;
            }
        }
        return null;
    }

    /** 섹션 → Map(resNumber → resContents) 평탄화 (행 전체) */
    private Map<String, String> flatDetail(EstateRegistrationResponse.ResRegistrationHis his) {
        Map<String, String> m = new HashMap<>();
        for (EstateRegistrationResponse.ResContents c : safeList(his.getResContentsList())) {
            if (!"2".equals(trim(c.getResType2()))) continue; // 데이터 행만
            m.putAll(flatDetail(c));
        }
        return m;
    }
    /** 행 → Map(resNumber → resContents) 평탄화 (각 셀) */
    private Map<String, String> flatDetail(EstateRegistrationResponse.ResContents row) {
        Map<String, String> m = new HashMap<>();
        for (EstateRegistrationResponse.ResDetail d : safeList(row.getResDetailList())) {
            m.put(trim(d.getResNumber()), cleanLines(d.getResContents()));
        }
        return m;
    }

    /** 여러 줄 문자열에서 첫 번째 유의미한(빈칸 아님) 줄만 반환 */
    private String pickFirstNonEmptyLine(String block) {
        if (block == null) return null;
        String[] lines = block.split("\\r?\\n");
        for (String line : lines) {
            String t = line.replace("&", "").trim();
            if (!t.isEmpty()) return t;
        }
        return block.replace("&", "").trim();
    }

    /** "84.9884㎡" 같은 문자열에서 숫자만 추출하여 BigDecimal로 반환 */
    private BigDecimal parseDecimalFromSquareMeter(String s) {
        if (s == null) return null;
        Matcher m = Pattern.compile("([0-9]+(?:\\.[0-9]+)?)\\s*㎡").matcher(s.replace(",", ""));
        if (m.find()) {
            try { return new BigDecimal(m.group(1)); } catch (Exception ignore) {}
        }
        // 위 패턴 실패 시, 일반 숫자 파싱 시도
        return parseDecimalSafe(s);
    }

    /** "1,234.56" 등 숫자/소수만 남기고 BigDecimal 변환 (실패 시 null) */
    private BigDecimal parseDecimalSafe(String s) {
        if (s == null) return null;
        String flat = s.replaceAll("[^0-9.]", "");
        if (flat.isEmpty()) return null;
        try { return new BigDecimal(flat); } catch (Exception e) { return null; }
    }

    /**
     * 날짜 파서 (방어적)
     * - "YYYYMMDD"(8자리), "YYYYMDD"(7자리, 앞자리 1자리 월), "YYYYMM"(6자리) 지원
     * - 그 외는 null
     */
    private LocalDate parseDateAny(String s) {
        if (s == null) return null;
        String digits = s.replaceAll("[^0-9]", "");
        if (digits.length() == 8) {
            return LocalDate.of(
                    Integer.parseInt(digits.substring(0, 4)),
                    Integer.parseInt(digits.substring(4, 6)),
                    Integer.parseInt(digits.substring(6, 8))
            );
        }
        if (digits.length() == 7) { // ex) 2024328 → 2024-03-28 가정
            int year = Integer.parseInt(digits.substring(0, 4));
            int month = Integer.parseInt(digits.substring(4, 5));
            int day = Integer.parseInt(digits.substring(5, 7));
            return LocalDate.of(year, month, day);
        }
        if (digits.length() == 6) { // YYYYMM → YYYY-MM-01 가정
            int year = Integer.parseInt(digits.substring(0, 4));
            int month = Integer.parseInt(digits.substring(4, 6));
            return LocalDate.of(year, month, 1);
        }
        return null;
    }
    private LocalDate parseDate8(String s8) { return parseDateAny(s8); }

    /** 순위번호 "1", "2", "1-1" 등을 정렬용 숫자로 변환 ("1-1" → 1) */
    private Integer parseRankOrder(String rankNo) {
        if (rankNo == null) return null;
        Matcher m = Pattern.compile("^(\\d+)").matcher(rankNo);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception ignore) {}
        }
        return null;
    }

    /** "거래가액 금504,980,000원" 같은 한글 금액을 Long으로 파싱 */
    private Long extractWon(String block) {
        Long won = extractMaxClaim(block);
        if (won != null) return won;
        Matcher m = Pattern.compile("([0-9][0-9,]{2,})\\s*원").matcher(block == null ? "" : block);
        if (m.find()) {
            String digits = m.group(1).replaceAll("[^0-9]", "");
            try { return Long.parseLong(digits); } catch (Exception ignore) {}
        }
        return null;
    }

    /** "채권최고액 금384,000,000원" 패턴에서 금액만 Long으로 파싱 */
    private Long extractMaxClaim(String block) {
        if (block == null) return null;
        Matcher m = Pattern.compile("채권최고액\\s*금\\s*([0-9,]+)원").matcher(block);
        if (m.find()) {
            String digits = m.group(1).replaceAll("[^0-9]", "");
            try { return Long.parseLong(digits); } catch (Exception ignore) {}
        }
        return null;
    }

    /** "근저당권자 XXX", "전세권자 YYY"에서 권리자명 추출 (간단) */
    private String extractCreditor(String block) {
        if (block == null) return null;
        Matcher m = Pattern.compile("(근저당권자|전세권자)\\s*([\\p{IsHangul}A-Za-z0-9()\\-\\s]+)").matcher(block);
        if (m.find()) {
            return trim(m.group(2));
        }
        return null;
    }

    /** "채무자 ZZZ"에서 채무자명 추출 */
    private String extractDebtor(String block) {
        if (block == null) return null;
        Matcher m = Pattern.compile("채무자\\s*([\\p{IsHangul}A-Za-z0-9*\\-\\s]+)").matcher(block);
        if (m.find()) {
            return trim(m.group(1));
        }
        return null;
    }

    /** "건물만" 문구 포함 여부 */
    private Boolean extractBuildingOnly(String block) {
        if (block == null) return null;
        if (block.contains("건물만") || block.contains("건물만에 관한")) return Boolean.TRUE;
        return null;
    }

    /** "소유자 홍길동" 패턴에서 명의인 추출 */
    private String extractHolderName(String block) {
        if (block == null) return null;
        Matcher m = Pattern.compile("소유자\\s*([\\p{IsHangul}A-Za-z0-9\\-\\s]+)").matcher(block);
        if (m.find()) return trim(m.group(1));
        return null;
    }

    /** "700606-*****" 같이 마스킹된 주민번호 패턴 추정 */
    private String extractMaskedRegno(String block) {
        if (block == null) return null;
        Matcher m = Pattern.compile("(\\d{6}-\\*+)").matcher(block);
        if (m.find()) return m.group(1);
        return null;
    }

    /** 괄호 속 주소 같은 문구를 간단 추출 "(주엽동,강선마을)" → "주엽동,강선마을" */
    private String extractAddress(String block) {
        if (block == null) return null;
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(block);
        if (m.find()) return m.group(1);
        return null;
    }

    /** 두 문자열을 공백 한 칸으로 합치기 (null 안전) */
    private String joinWithSpace(String a, String b) {
        if (a == null && b == null) return null;
        if (a == null) return b;
        if (b == null) return a;
        String t = (a + " " + b).replaceAll("\\s+", " ").trim();
        return t;
    }

    /** CODEF 표기 특성('&' 구분자 등) 정리 + trim */
    private String cleanLines(String s) {
        if (s == null) return null;
        return s.replace("&", "").trim();
    }

    /** "1 소유권대지권" 같은 값에서 앞번호/구두점을 제거 → "소유권대지권" */
    private String cleanRightType(String s) {
        if (s == null) return null;
        String t = s.replace("&", "").trim();
        // 맨 앞의 숫자 + 선택적 '.' + 공백 제거
        t = t.replaceAll("^\\d+\\.?\\s*", "");
        return t.isEmpty() ? null : t;
    }
}

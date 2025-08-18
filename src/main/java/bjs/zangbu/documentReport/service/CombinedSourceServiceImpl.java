// src/main/java/bjs/zangbu/report/service/CombinedSourceServiceImpl.java
package bjs.zangbu.documentReport.service;

import bjs.zangbu.deal.vo.DocumentType;
import bjs.zangbu.mongo.Dao.ReportDocumentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class CombinedSourceServiceImpl implements CombinedSourceService {

    private final ReportDocumentDao dao;

    @Override
    public Map<String, Object> buildCombined(String memberId, Long buildingId, Map<String, Object> inputs) {
        // 1) Mongo에서 원본 가져오기
        Document regDoc = dao.findOne(memberId, buildingId, DocumentType.ESTATE.name());
        if (regDoc == null) {
            throw new NoSuchElementException("등기부 문서를 찾을 수 없습니다.");
        }

        Document brDoc = dao.findOne(memberId, buildingId, DocumentType.BUILDING_REGISTER.name());
        if (brDoc == null) {
            throw new NoSuchElementException("건축물대장 문서를 찾을 수 없습니다.");
        }

        Map<String, Object> regData = safeMap(regDoc.get("parsed"));
        Map<String, Object> brData  = safeMap(brDoc.get("parsed"));

        // 2) 각각 정규화 → target 스키마로 매핑
        Map<String, Object> registry = normalizeRegistry(regData);
        Map<String, Object> buildingRegister = normalizeBuildingRegister(brData);

        // 3) 최종 합치기
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("registry", registry);
        source.put("building_register", buildingRegister);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("source", source);
        root.put("inputs", inputs == null ? Collections.emptyMap() : inputs);
        return root;
    }

    // --------------------
    // 등기부등본 → registry
    // --------------------
    @SuppressWarnings("unchecked")
    private Map<String, Object> normalizeRegistry(Map<String, Object> data) {
        Map<String, Object> out = new LinkedHashMap<>();

        List<Map<String, Object>> entries = listOfMap(data.get("resRegisterEntriesList"));
        if (!entries.isEmpty()) {
            Map<String, Object> first = entries.get(0);
            out.put("publish_date", toDateDash((String) first.get("resPublishDate"))); // "YYYYMMDD" → "YYYY-MM-DD"
            out.put("office", first.get("commCompetentRegistryOffice"));
            out.put("summary", new LinkedHashMap<>()); // 아래서 채움
        }

        // 모든 섹션 모으기
        List<Map<String, Object>> hisAll = new ArrayList<>();
        for (Map<String, Object> e : entries) {
            hisAll.addAll(listOfMap(e.get("resRegistrationHisList")));
        }

        // 표제부 - 주소/호/전유면적/대지권
        Map<String, Object> prop = new LinkedHashMap<>();
        Map<String, Object> unitSec = findHis(hisAll, "표제부", "전유부분의 건물의 표시");
        if (unitSec != null) {
            Map<String, String> flat = flatDetail(unitSec);
            prop.put("unit", trim(flat.get("2")));
            prop.put("exclusive_area_m2", parseDoubleFromAny(flat.get("3")));
        }
        Map<String, Object> landSec = findHis(hisAll, "표제부", "대지권의 표시");
        if (landSec != null) {
            Map<String, String> flat = flatDetail(landSec);
            prop.put("land_share_ratio", trim(flat.get("2")));
            prop.put("land_right_type", cleanRightType(flat.get("1")));
        }
        Map<String, Object> addrSec = findHis(hisAll, "표제부", "1동의 건물의 표시");
        if (addrSec != null) {
            Map<String, String> flat = flatDetail(addrSec);
            prop.put("formatted_address", pickFirstNonEmptyLine(flat.get("2")));
        }
        out.put("property", prop);

        // 갑구/을구 → ownership_history / encumbrances
        List<Map<String, Object>> gapList = new ArrayList<>();
        List<Map<String, Object>> eulList = new ArrayList<>();
        extractGapEul(hisAll, gapList, eulList);

        out.put("ownership_history", gapList);
        out.put("encumbrances", eulList);

        // summary
        String owner = pickOwnerCandidate(gapList);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("ownership", (owner == null) ? null : ("현 소유자: " + owner));
        if (!eulList.isEmpty()) {
            Object cred = eulList.get(0).get("creditor");
            summary.put("liabilities", "근저당권 " + eulList.size() + "건" + (cred != null ? "(" + cred + ")" : ""));
        }
        out.put("summary", summary);

        return out;
    }

    // --------------------
    // 건축물대장 → building_register
    // --------------------
    private Map<String, Object> normalizeBuildingRegister(Map<String, Object> data) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("issue_date", toDateDash((String) data.get("resIssueDate")));
        out.put("issuer", data.get("resIssueOgzNm"));

        Map<String, Object> address = new LinkedHashMap<>();
        address.put("lot", joinWithSpace((String) data.get("resUserAddr"), (String) data.get("commAddrLotNumber")));
        address.put("road", data.get("commAddrRoadName"));
        out.put("address", address);

        Map<String, Object> complex = new LinkedHashMap<>();
        complex.put("name", data.get("resBuildingName"));
        out.put("complex", complex);

        Map<String, Object> use = new LinkedHashMap<>();
        Map<String, String> detail = toDetailMap(listOfMap(data.get("resDetailList")));
        use.put("main", detail.get("주용도"));
        out.put("use", use);

        Map<String, Object> approval = new LinkedHashMap<>();
        approval.put("use_approval_date", toDateDash(detail.get("사용승인일자")));
        out.put("approval", approval);

        Map<String, Object> scale = new LinkedHashMap<>();
        scale.put("site_area_m2", parseDoubleFromAny(detail.get("대지면적")));
        scale.put("total_floor_area_m2", parseDoubleFromAny(detail.get("연면적")));
        scale.put("building_coverage_ratio", parseDoubleFromAny(detail.get("건폐율")));
        Double far = firstNonNull(parseDoubleFromAny(detail.get("용적율")), parseDoubleFromAny(detail.get("용적률")));
        scale.put("floor_area_ratio", far);
        scale.put("household_summary", detail.get("총호수"));
        out.put("scale", scale);

        Map<String, Object> seismic = new LinkedHashMap<>();
        seismic.put("applied", detail.get("내진설계적용여부"));
        seismic.put("capacity", detail.get("내진능력"));
        out.put("seismic", seismic);

        Map<String, Object> violation = new LinkedHashMap<>();
        violation.put("status", data.get("resViolationStatus"));
        out.put("violation", violation);

        List<Map<String, Object>> changesRaw = listOfMap(data.get("resChangeList"));
        List<Map<String, Object>> changes = new ArrayList<>();
        for (Map<String, Object> c : changesRaw) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", toDateDash((String) c.get("resChangeDate")));
            row.put("reason", c.get("resChangeReason"));
            changes.add(row);
        }
        out.put("changes", changes);

        return out;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> toDetailMap(List<Map<String, Object>> detailList) {
        Map<String, String> out = new LinkedHashMap<>();
        if (detailList == null) return out;

        for (Map<String, Object> row : detailList) {
            // CODEF 건축물대장 detail 구조: { resType: "주용도", resContents: "공동주택(아파트)" }
            Object k = row.get("resType");
            Object v = row.get("resContents");
            if (k != null) {
                String key = String.valueOf(k).trim();
                String val = (v == null) ? null : String.valueOf(v).trim();
                // 같은 키가 여러 번 오면 '첫 값 유지' 또는 '덮어쓰기' 중 선택. 여기선 첫 값 유지.
                out.putIfAbsent(key, val);
            }
        }
        return out;
    }
    // --------------------
    // 공통 유틸
    // --------------------
    private Map<String, Object> safeMap(Object o) {
        if (o instanceof Map<?, ?> m) return (Map<String, Object>) m;
        return Collections.emptyMap();
    }
    private List<Map<String, Object>> listOfMap(Object o) {
        if (o instanceof List<?> l) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object e : l) if (e instanceof Map<?, ?> m) out.add((Map<String, Object>) m);
            return out;
        }
        return Collections.emptyList();
    }
    private String trim(String s) { return s == null ? null : s.trim(); }

    private String toDateDash(String ymd) {
        if (ymd == null) return null;
        String digits = ymd.replaceAll("[^0-9]", "");
        if (digits.length() == 8) {
            return digits.substring(0,4) + "-" + digits.substring(4,6) + "-" + digits.substring(6,8);
        }
        return ymd; // 형식 불명은 원본 유지
    }
    private String joinWithSpace(String a, String b) {
        if (a == null && b == null) return null;
        if (a == null) return b;
        if (b == null) return a;
        return (a + " " + b).replaceAll("\\s+", " ").trim();
    }
    private Double parseDoubleFromAny(String s) {
        if (s == null) return null;
        String flat = s.replaceAll("[^0-9.]", "");
        if (flat.isEmpty()) return null;
        try { return Double.parseDouble(flat); } catch (Exception e) { return null; }
    }
    private <T> T firstNonNull(T a, T b) { return a != null ? a : b; }

    // 등기부 섹션/셀 평탄화
    private Map<String, Object> findHis(List<Map<String, Object>> hisList, String type, String type1) {
        for (Map<String, Object> h : hisList) {
            if (Objects.equals(trim((String) h.get("resType")), type)
                    && Objects.equals(trim((String) h.get("resType1")), type1)) return h;
        }
        return null;
    }
    private Map<String, String> flatDetail(Map<String, Object> hisOrRow) {
        Map<String, String> out = new LinkedHashMap<>();
        for (Map<String, Object> row : listOfMap(hisOrRow.get("resContentsList"))) {
            if (!"2".equals(String.valueOf(row.get("resType2")))) continue;
            for (Map<String, Object> cell : listOfMap(row.get("resDetailList"))) {
                String k = String.valueOf(cell.get("resNumber"));
                String v = cleanLines((String) cell.get("resContents"));
                out.put(k, v);
            }
        }
        return out;
    }
    private String cleanLines(String s) { return s == null ? null : s.replace("&", "").trim(); }
    private String pickFirstNonEmptyLine(String block) {
        if (block == null) return null;
        for (String line : block.split("\\r?\\n")) {
            String t = line.replace("&", "").trim();
            if (!t.isEmpty()) return t;
        }
        return block.replace("&", "").trim();
    }
    private String cleanRightType(String s) {
        if (s == null) return null;
        String t = s.replace("&", "").trim();
        return t.replaceAll("^\\d+\\.?\\s*", "");
    }

    // 갑구/을구 추출 → ownership_history / encumbrances
    private void extractGapEul(List<Map<String, Object>> hisAll,
                               List<Map<String, Object>> gapOut,
                               List<Map<String, Object>> eulOut) {
        Pattern wonPat = Pattern.compile("([0-9][0-9,]{2,})\\s*원");
        Pattern maxClaimPat = Pattern.compile("채권최고액\\s*금\\s*([0-9,]+)원");

        for (Map<String, Object> his : hisAll) {
            String sec = trim((String) his.get("resType"));
            if (!"갑구".equals(sec) && !"을구".equals(sec)) continue;

            for (Map<String, Object> row : listOfMap(his.get("resContentsList"))) {
                if (!"2".equals(String.valueOf(row.get("resType2")))) continue;

                // ★ 여기 한 줄만 변경
                Map<String, String> m = flatDetailRow(row);

                String rankNo = m.get("0");
                String purpose = m.get("1");
                String receipt = m.get("2");
                String cause   = m.get("3");
                String others  = m.get("4");

                if ("갑구".equals(sec)) {
                    Map<String, Object> o = new LinkedHashMap<>();
                    o.put("rank_no", rankNo);
                    o.put("purpose", purpose);
                    o.put("receipt", receipt);
                    o.put("cause", cause);
                    o.put("holder_name", extractRegex(others, "소유자\\s*([\\p{IsHangul}A-Za-z0-9\\-\\s]+)"));
                    o.put("holder_regno_masked", extractRegex(others, "(\\d{6}-\\*+)"));
                    o.put("holder_address", extractParen(others));
                    o.put("price_if_any", parseWon(others, wonPat));
                    o.put("notes_raw", others);
                    gapOut.add(o);
                } else {
                    Map<String, Object> o = new LinkedHashMap<>();
                    o.put("rank_no", rankNo);
                    o.put("purpose", purpose);
                    o.put("receipt", receipt);
                    o.put("cause", cause);
                    o.put("creditor", extractRegex(others, "(근저당권자|전세권자)\\s*([\\p{IsHangul}A-Za-z0-9()\\-\\s]+)", 2));
                    o.put("debtor",   extractRegex(others, "채무자\\s*([\\p{IsHangul}A-Za-z0-9*\\-\\s]+)"));
                    o.put("max_claim_amount", parseMaxClaim(others, maxClaimPat));
                    o.put("remarks_raw", others);

                    Map<String,Object> flags = new LinkedHashMap<>();
                    flags.put("building_only", others != null && others.contains("건물만"));
                    o.put("flags", flags);

                    eulOut.add(o);
                }
            }
        }
        Comparator<Map<String, Object>> cmp = Comparator.comparing(
                m -> parseRankOrder((String) m.get("rank_no")), Comparator.nullsLast(Integer::compareTo));
        gapOut.sort(cmp);
        eulOut.sort(cmp);
    }
    private Integer parseRankOrder(String rankNo) {
        if (rankNo == null) return null;
        Matcher m = Pattern.compile("^(\\d+)").matcher(rankNo);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }
    private String extractRegex(String text, String pattern) {
        if (text == null) return null;
        Matcher m = Pattern.compile(pattern).matcher(text);
        return m.find() ? trim(m.group(1)) : null;
    }
    private String extractRegex(String text, String pattern, int group) {
        if (text == null) return null;
        Matcher m = Pattern.compile(pattern).matcher(text);
        return m.find() ? trim(m.group(group)) : null;
    }
    private String extractParen(String text) {
        if (text == null) return null;
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(text);
        return m.find() ? m.group(1) : null;
    }
    private Long parseWon(String text, Pattern wonPat) {
        if (text == null) return null;
        Matcher m = wonPat.matcher(text);
        if (m.find()) {
            String digits = m.group(1).replaceAll("[^0-9]", "");
            try { return Long.parseLong(digits); } catch (Exception ignore) {}
        }
        return null;
    }
    private Long parseMaxClaim(String text, Pattern pat) {
        if (text == null) return null;
        Matcher m = pat.matcher(text);
        if (m.find()) {
            String digits = m.group(1).replaceAll("[^0-9]", "");
            try { return Long.parseLong(digits); } catch (Exception ignore) {}
        }
        return null;
    }
    private String pickOwnerCandidate(List<Map<String, Object>> gapList) {
        String owner = null;
        for (Map<String, Object> g : gapList) {
            if ("소유권이전".equals(trim((String) g.get("purpose")))) {
                owner = (String) g.get("holder_name");
            }
        }
        if (owner == null && !gapList.isEmpty()) {
            owner = (String) gapList.get(gapList.size() - 1).get("holder_name");
        }
        return owner;
    }

    // 행(row) 전용: resDetailList만 평탄화
    private Map<String, String> flatDetailRow(Map<String, Object> row) {
        Map<String, String> out = new LinkedHashMap<>();
        for (Map<String, Object> cell : listOfMap(row.get("resDetailList"))) {
            String k = String.valueOf(cell.get("resNumber"));
            String v = cleanLines((String) cell.get("resContents"));
            out.put(k, v);
        }
        return out;
    }
}

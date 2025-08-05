package bjs.zangbu.documentReport.util;

import bjs.zangbu.documentReport.dto.request.EstateRegisterData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CodefCalcUtil {

    private static final Pattern MONEY  = Pattern.compile("채권최고액\\s*금([0-9,]+)원");
    private static final Pattern OWNER  = Pattern.compile("소유자\\s*([^\\s]+)");
    private static final Pattern JUMIN  = Pattern.compile("(\\d{6}-\\*{7}|\\d{6}-\\d{7})");
    private static final Pattern TRUST  = Pattern.compile("신탁[^\\n]*");

    /* ─── 1) 선순위 채권액 ─── */
    public static long calcPriorityDebt(EstateRegisterData data) {

        long sum = 0;
        EstateRegisterData.RegisterEntry entry = data.getResRegisterEntriesList().get(0);

        /* ① 요약 테이블 우선 */
        for (EstateRegisterData.RegSum sumRow : entry.getResRegistrationSumList()) {
            if (sumRow.getResType().contains("(근)저당권")) {
                String cell = sumRow.getResContentsList()
                        .get(1).getResDetailList()   // 첫 데이터 행
                        .get(3).getResContents();    // index 3 : 주요등기사항
                Matcher m = MONEY.matcher(cell);
                if (m.find()) sum += parseMoney(m.group(1));
            }
        }

        /* ② 원본 을구 보조 */
        if (sum == 0) {
            for (EstateRegisterData.RegHistory sec : entry.getResRegistrationHisList()) {
                if (!"을구".equals(sec.getResType())) continue;
                for (EstateRegisterData.ContentsRow row : sec.getResContentsList()) {
                    if (!"2".equals(row.getResType2())) continue;
                    String purpose = row.getResDetailList().get(1).getResContents();
                    if (purpose.contains("근저당권설정") || purpose.contains("전세권설정") ||
                            purpose.contains("저당권설정")) {

                        String cell = row.getResDetailList().get(4).getResContents();
                        Matcher m = MONEY.matcher(cell);
                        if (m.find()) sum += parseMoney(m.group(1));
                    }
                }
            }
        }
        return sum;
    }

    /* ─── 2) 소유자 정보 ─── */
    public static OwnerInfo extractOwner(EstateRegisterData data) {
        EstateRegisterData.RegisterEntry entry = data.getResRegisterEntriesList().get(0);

        // 요약 소유지분현황
        for (EstateRegisterData.RegSum sum : entry.getResRegistrationSumList()) {
            if (sum.getResType().startsWith("소유지분현황")) {
                String name = sum.getResContentsList()
                        .get(1).getResDetailList().get(0).getResContents();
                String reg  = sum.getResContentsList()
                        .get(1).getResDetailList().get(1).getResContents();
                return new OwnerInfo(name, reg);
            }
        }
        // (필요하면 갑구 최신 행 로직 추가)
        return new OwnerInfo("", "");
    }

    /* ─── 3) 신탁 여부 ─── */
    public static TrustInfo detectTrust(EstateRegisterData data) {
        EstateRegisterData.RegisterEntry entry = data.getResRegisterEntriesList().get(0);
        for (EstateRegisterData.RegHistory sec : entry.getResRegistrationHisList()) {
            for (EstateRegisterData.ContentsRow row : sec.getResContentsList()) {
                if (!"2".equals(row.getResType2())) continue;
                for (EstateRegisterData.Detail d : row.getResDetailList()) {
                    Matcher m = TRUST.matcher(d.getResContents());
                    if (m.find()) return new TrustInfo(true, m.group());
                }
            }
        }
        return new TrustInfo(false, null);
    }

    /* ─── helpers ─── */
    private static long parseMoney(String num) {
        return Long.parseLong(num.replace(",", ""));
    }

    @Getter
    @AllArgsConstructor
    public static class OwnerInfo {
        private final String name;
        private final String maskedRegNo;
    }
    @Getter @AllArgsConstructor
    public static class TrustInfo {
        private final boolean isTrustee;
        private final String  trustType;
    }
}

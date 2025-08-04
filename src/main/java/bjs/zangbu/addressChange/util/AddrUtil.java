package bjs.zangbu.addressChange.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 초본 원문 주소 처리 유틸
 * - 검색용 문자열 정규화(개행/공백)
 * - 동/호 추출 (res_number 저장용)
 * - 2차 검색용: 동/호 제거
 * - 메모성 라인([ ... ]) 식별
 */
public final class AddrUtil {

    private AddrUtil() {}

    /* =========================
       정규식 패턴 정의 (미리 컴파일)
       ========================= */

    /**
     * "1606동 902호", "1504 동 1202 호" 등 동·호 세트
     *  - 숫자 1~4자리 + "동" + 숫자 1~4자리 + "호"
     *  - 동/호 사이 공백 허용
     */
    private static final Pattern DONG_HO_PAIR =
            Pattern.compile("(\\d{1,4}\\s*동\\s*\\d{1,4}\\s*호)");

    /**
     * "1208-511" 같은 하이픈 호수(구형 표기나 주공식 표기)
     */
    private static final Pattern HYPHEN_HO =
            Pattern.compile("(\\d{1,4}-\\d{1,4})");

    /**
     * "12층 1202호"와 같이 층 표기가 함께 오는 경우
     */
    private static final Pattern FLOOR_HO_PAIR =
            Pattern.compile("(\\d{1,4}\\s*층\\s*\\d{1,4}\\s*호)");

    /**
     * "1606동"처럼 동만 표기된 경우(호수 없음)
     *  - 주소 본문 식별에 사용되지만 제거 시 주의 필요
     */
    private static final Pattern DONG_ONLY =
            Pattern.compile("(\\d{1,4}\\s*동)");

    /**
     * 메모성 라인 식별: 문자열이 "[" 로 시작해서 "]" 로 끝나는 경우
     *  - 예: "[고양시 조례 제1156호에 의거 통반 변경]"
     */
    public static boolean isMemoLine(String addr) {
        if (addr == null) return false;
        String s = addr.trim();
        return s.startsWith("[") && s.endsWith("]");
    }

    /* =========================
       1) 검색용 문자열 정규화 (동·호 보존)
       ========================= */

    /**
     * 검색 1차 시도에 사용할 문자열로 정리
     * - 개행/캐리지리턴을 공백으로 치환
     * - 다중 공백을 단일 공백으로 축약
     * - 앞뒤 공백 제거
     * - 동/호는 "보존"
     *
     * 예)
     *  입력: "경기도 고양시 일산서구 주엽동 86\n강선마을 1606-902"
     *  출력: "경기도 고양시 일산서구 주엽동 86 강선마을 1606-902"
     */
    public static String normalizeForSearchKeepDongHo(String src) {
        if (src == null) return null;
        return src
                .replace("\n", " ")
                .replace("\r", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /* =========================
       2) 동/호 추출 (res_number 저장용)
       ========================= */

    /**
     * 주소 문자열에서 동/호를 "원문 그대로" 하나 추출
     * - 우선순위:
     *   (1) DONG_HO_PAIR  : "1606동 902호"
     *   (2) FLOOR_HO_PAIR : "12층 1202호"
     *   (3) HYPHEN_HO     : "1208-511"
     *   (4) DONG_ONLY     : "1606동"
     * - 첫 매칭 1건만 반환
     *
     * 예)
     *  입력: "강선마을 1606동 902호"
     *  출력: "1606동 902호"
     */
    public static Optional<String> extractDongHo(String src) {
        String s = src == null ? "" : src;

        for (Pattern p : new Pattern[]{DONG_HO_PAIR, FLOOR_HO_PAIR, HYPHEN_HO, DONG_ONLY}) {
            Matcher m = p.matcher(s);
            if (m.find()) {
                return Optional.ofNullable(m.group(1));
            }
        }
        return Optional.empty();
    }

    /* =========================
       3) 2차 검색용: 동/호 제거 (매칭 실패 시 폴백)
       ========================= */

    /**
     * 도로명주소 API 1차(동·호 포함) 검색에서 결과가 없을 때,
     * 2차로 "동/호"를 제거한 문자열로 재시도하기 위한 메서드.
     *
     * 주의:
     * - 동/호 제거는 주소 매칭 성공률을 높이지만, 정보 손실 우려가 있으므로
     *   "검색 재시도"에서만 사용하고, 저장용 원문/동·호는 따로 보존하세요.
     *
     * 예)
     *  입력: "경기도 고양시 일산서구 강선로 30, 1504동 1202호 (주엽동,강선마을)"
     *  출력: "경기도 고양시 일산서구 강선로 30, (주엽동,강선마을)"
     */
    public static String removeDongHo(String src) {
        if (src == null) return null;
        String s = src;

        // (1) "1606동 902호" 제거
        s = DONG_HO_PAIR.matcher(s).replaceAll(" ");

        // (2) "12층 1202호" 제거
        s = FLOOR_HO_PAIR.matcher(s).replaceAll(" ");

        // (3) "1208-511" 제거
        s = HYPHEN_HO.matcher(s).replaceAll(" ");

        // (4) "1606동" 제거 (마지막에 수행)
        s = DONG_ONLY.matcher(s).replaceAll(" ");

        // 공백 축약
        return s.replaceAll("\\s+", " ").trim();
    }

    /* =========================
       4) (선택) 간단 타입 추정
       ========================= */

    /**
     * 매우 러프한 판별. 저장시 참고용(정확 판별은 외부 API 결과를 따르는 것이 좋음)
     * - 괄호 안 단지명/도로명 키워드가 있으면 ROAD로 가정
     * - "리", "동" 기반 지번성 패턴이 많으면 JIBUN 추정
     */
    public static String guessAddressType(String s) {
        if (s == null) return "UNKNOWN";
        String text = s.replaceAll("\\s+", "");
        if (text.contains("로") || text.contains("길")) {
            return "ROAD";
        }
        if (text.contains("동") || text.contains("리")) {
            return "JIBUN";
        }
        return "UNKNOWN";
    }
}

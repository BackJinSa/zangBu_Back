package bjs.zangbu.addressChange.dto.abstractResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResidentAbstractResponse {
    public Result result;    // 최상위 result 블록 (코드/메시지/트랜잭션ID)
    public Data data;        // 최상위 data 블록 (실데이터)

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        public String code;          // 예: "CF-00000"
        public String message;       // 예: "성공"
        public String transactionId; // 요청 트랜잭션 ID
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        @JsonProperty("resAddrChangeList")
        public List<AddrChange> addrChanges; // 주소변동 리스트(핵심)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddrChange {
        public String resNumber;            // 일련번호(“1”, “2”…). 호수가 아니라 “변동 레코드 번호” 성격
        public String resUserAddr;          // 주소 원문(개행 \n 포함 가능)
        public String resMoveInDate;        // 전입일(YYYYMMDD) or 빈 문자열
        public String resChangeDate;        // 변경일(YYYYMMDD) or 빈 문자열
        public String resChangeReason;      // 변경 사유: "전입", "세대주변경", "통반변경", "도로명주소" 등
        public String resHHRelation;        // 세대주와의 관계(있을 수 있음)
        public String resRegistrationStatus;// 등록 상태(예: “거주자” 등)
    }
}

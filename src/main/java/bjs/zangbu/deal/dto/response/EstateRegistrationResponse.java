package bjs.zangbu.deal.dto.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EstateRegistrationResponse {

    private String commIssueCode;
    private String resIssueYN;
    private String resTotalPageCount;
    private String commStartPageNo;
    private String resEndPageNo;
    private String resWarningMessage;
    private String resOriGinalData; // JSON 키 그대로 사용
    private List<ResAddr> resAddrList;
    private List<ResSearch> resSearchList;
    private List<ResRegisterEntries> resRegisterEntriesList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResAddr {
        private String resUserNm;
        private String commUniqueNo;
        private String commAddrLotNumber;
        private String resState;
        private String resType;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResSearch {
        private String resType;
        private String resNumber;
        private String commUniqueNo;
        private String commListNumber;
        private String resListType;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResRegisterEntries {
        private String resIssueNo;
        private String commUniqueNo;
        private String resDocTitle;
        private String resRealty;
        private String commCompetentRegistryOffice;
        private String resPublishNo;
        private String resPublishDate;
        private String resPublishRegistryOffice;
        private List<ResPrecaution> resPrecautionsList;
        private List<ResRegistrationSum> resRegistrationSumList;
        private List<ResRegistrationHis> resRegistrationHisList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResPrecaution {
        private String resNumber;
        private String resContents;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResRegistrationSum {
        private String resType;
        private String resType1;
        private List<ResContents> resContentsList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResRegistrationHis {
        private String resType;
        private String resType1;
        private List<ResContents> resContentsList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResContents {
        private String resNumber;
        private String resType2;
        private List<ResDetail> resDetailList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResDetail {
        private String resNumber;
        private String resContents;
    }
}

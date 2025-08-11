package bjs.zangbu.deal.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * CODEF 건축물대장 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingRegisterResponse {

    @JsonProperty("resUserAddr")
    private String resUserAddr;

    @JsonProperty("resOriGinalData")
    private String resOriGinalData;

    @JsonProperty("resIssueDate")
    private String resIssueDate;

    @JsonProperty("resIssueOgzNm")
    private String resIssueOgzNm;

    @JsonProperty("resNote")
    private String resNote;

    @JsonProperty("resDocNo")
    private String resDocNo;

    @JsonProperty("resReceiptNo")
    private String resReceiptNo;

    @JsonProperty("commUniqeNo")
    @JsonAlias({"commUniqueNo"})
    private String commUniqueNo;

    @JsonProperty("commAddrRoadName")
    private String commAddrRoadName;

    @JsonProperty("commAddrLotNumber")
    private String commAddrLotNumber;

    @JsonProperty("resNote1")
    private String resNote1;

    @JsonProperty("resBuildingName")
    private String resBuildingName;

    @JsonProperty("resParkingLotStatusList")
    private List<ParkingLotStatus> resParkingLotStatusList;

    @JsonProperty("resAuthStatusList")
    private List<AuthStatus> resAuthStatusList;

    @JsonProperty("resLicenseClassList")
    private List<LicenseClass> resLicenseClassList;

    @JsonProperty("resDetailList")
    private List<Detail> resDetailList;

    @JsonProperty("resChangeList")
    private List<Change> resChangeList;

    @JsonProperty("resBuildingStatusList")
    private List<BuildingStatus> resBuildingStatusList;

    @JsonProperty("resViolationStatus")
    private String resViolationStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParkingLotStatus {
        @JsonProperty("resType")
        private String resType;
        @JsonProperty("resNumber")
        private String resNumber;
        @JsonProperty("resArea")
        private String resArea;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthStatus {
        @JsonProperty("resType")
        private String resType;
        @JsonProperty("resType1")
        private String resType1;
        @JsonProperty("resContents")
        private String resContents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LicenseClass {
        @JsonProperty("resUserNm")
        private String resUserNm;
        @JsonProperty("resType")
        private String resType;
        @JsonProperty("resLicenseNo")
        private String resLicenseNo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detail {
        @JsonProperty("resType")
        private String resType;
        @JsonProperty("resContents")
        private String resContents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Change {
        @JsonProperty("resChangeDate")
        private String resChangeDate;
        @JsonProperty("resChangeReason")
        private String resChangeReason;
        @JsonProperty("resNote")
        private String resNote;
        @JsonProperty("resIssueDate")
        private String resIssueDate;
        @JsonProperty("resIssueOgzNm")
        private String resIssueOgzNm;
        @JsonProperty("resOriGinalData")
        private String resOriGinalData;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BuildingStatus {
        @JsonProperty("resUseType")
        private String resUseType;
        @JsonProperty("commAddrRoadName")
        private String commAddrRoadName;
        @JsonProperty("resArea")
        private String resArea;
        @JsonProperty("resFloor")
        private String resFloor;
        @JsonProperty("resStructure")
        private String resStructure;
        @JsonProperty("resBuildingName")
        private String resBuildingName;
        @JsonProperty("resRoof")
        private String resRoof;
        @JsonProperty("resType")
        private String resType;
        @JsonProperty("resChangeDate")
        private String resChangeDate;
        @JsonProperty("resChangeReason")
        private String resChangeReason;
    }
}

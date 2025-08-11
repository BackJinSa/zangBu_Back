package bjs.zangbu.addressChange.dto.abstractResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResidentDataResponse {
    @JsonProperty("resAddrChangeList")
    public List<AddrChange> addrChanges;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddrChange {
        public String resNumber;
        public String resUserAddr;
        public String resMoveInDate;
        public String resChangeDate;
        public String resChangeReason;
        public String resHHRelation;
        public String resRegistrationStatus;
    }
}

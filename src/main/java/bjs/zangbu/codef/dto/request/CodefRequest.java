package bjs.zangbu.codef.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CodefRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class secureNoRequest {
        private String sessionKey;
        private String secureNo;
    }
}

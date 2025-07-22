package bjs.zangbu.deal.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DealRequest {

    // /deal/consumer/intent Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Intent{
        private Long buildingId;
    }

    //  /deal/status Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status{
        private String dealId;
        private String status;
    }
}

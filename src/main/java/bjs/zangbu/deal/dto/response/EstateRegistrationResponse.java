package bjs.zangbu.deal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class EstateRegistrationResponse {
    private String resOriginalData;
    private String commUniqueNo; //todo : 가져올 데이터 정해야함
}

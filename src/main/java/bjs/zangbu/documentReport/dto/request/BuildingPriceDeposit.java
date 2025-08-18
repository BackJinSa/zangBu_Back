package bjs.zangbu.documentReport.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingPriceDeposit {
    private Integer price;   // 시세
    private Integer deposit; // 내 보증금
}
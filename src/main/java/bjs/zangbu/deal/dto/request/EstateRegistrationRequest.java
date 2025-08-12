package bjs.zangbu.deal.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EstateRegistrationRequest {

  private Long dealId;

  //    member table
  private String phone;
  private String birth;


  //    complex_list table
  private String sido;
  private String address;
  private String sigungu;
  private String dong;
  private String ho;
  private String roadName; // 도로명, todo : complexList 테이블에 추가해야함

}

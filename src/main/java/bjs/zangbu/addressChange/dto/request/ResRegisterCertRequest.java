package bjs.zangbu.addressChange.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 주민등록 초본 api 호출 인자 dto
public class ResRegisterCertRequest {
    private String birth;
    private String identity;
    private String phone;
    private String name;
    private String telecom;
    //memberid로 조회
    private String memberId;
}

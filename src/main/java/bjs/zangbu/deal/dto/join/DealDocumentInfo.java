package bjs.zangbu.deal.dto.join;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 건축물대장에 필요한 정보
public class DealDocumentInfo {
    /* ----- member 테이블 ----- */
    private String identity;   // 암호화된 주민번호
    private String birth;      // 생년월일(YYMMDD)
    private String phone;      // 휴대폰
    private String name;       // 이름

    /* ----- complex_list 테이블 ----- */
    private String address;    // 도로명 주소(원 테이블이 여러 칼럼이면 CONCAT 처리)
    private String zonecode;   // 우편번호
}

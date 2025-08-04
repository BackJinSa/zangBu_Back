package bjs.zangbu.addressChange.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddressChange {
    // 주소 식별자 id
    private Long addressChangeId;
    // 동호수
    private String resNumber;
    // 주소(지번 -> 도로명 으로 변환해야 함)
    // 저장용 주소
    private String resUserAddr;
    // 전입일
    private LocalDateTime resMoveInDate;
    //외래 키 : 멤버 id
    private String memberId;
}

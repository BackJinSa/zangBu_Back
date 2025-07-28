package bjs.zangbu.fcm.vo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Fcm {
    // 디바이스 토큰 식별 id
    private Long fcmTokensId;

    // 토큰
    private String token;

    // 디바이스 유형
    private String deviceType;

    // 디바이스 이름
    private String deviceName;

    // 토큰 생성(등록) 날짜
    private Date createdAt;

    // ==== foreign key

    // 유저 식별 id
    private String memberId;
}

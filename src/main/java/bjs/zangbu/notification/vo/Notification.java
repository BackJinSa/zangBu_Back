package bjs.zangbu.notification.vo;

import bjs.zangbu.notification.dto.response.NotificationResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notification {

    // 알림 식별 id
    private Long notificationId;

    // 알림 내용
    private String message;

    // 읽음 여부
    private boolean isRead;

    // 알림 유형(BUILDING, TRADE, REVIEW)
    private Type type;

    // 알림 생성 날짜
    private Date createdAt;

    // 매물 종류(MONTHLY, CHARTER, TRADING)
    private SaleType saleType;

    // 가격
    private int price;

    // 주소(구, 동)
    private String address;

    // 평점
    private int rank;

    // ==== foreign key

    // 유저 식별 id
    private String memberId;

    // 매물 식별 id
    private Long buildingId;

}

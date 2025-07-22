package bjs.zangbu.notification.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notification {

    private Long notificationId;
    private UUID userId;
    private String message;
    private boolean isRead;
    private Type type;
    private TypeDetail typeDetail;
    private Date createdAt;
    private SaleType saleType;
    private int price;
    private String address;
    private int rank;

}

package bjs.zangbu.building.vo;
import java.math.BigInteger;
import java.time.LocalDateTime;

public class Building {
    private BigInteger buildingId;
    private String sellerNickname;
    private SaleType saleType;
    private Integer price;
    private BigInteger deposit;
    private Integer bookmarkCount;
    private LocalDateTime createdAt;
    private String buildingName;
    private SellerType sellerType;
    private PropertyType propertyType;
    private LocalDateTime moveDate;
    private String infoOneline;
    private String infoBuilding;
    private String imageUrl;
    private String contactName;
    private String contactPhone;
    private String facility;
}

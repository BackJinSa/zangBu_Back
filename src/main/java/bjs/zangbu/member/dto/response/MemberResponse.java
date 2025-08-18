package bjs.zangbu.member.dto.response;

import bjs.zangbu.building.vo.Building;
import bjs.zangbu.member.dto.join.BookmarkBuilding;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponse {

  // /member/mypage/favorites?page={page}&size={size}
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "즐겨찾기 매물 목록 응답 DTO")
  public static class BookmarkList {

    @ApiModelProperty(value = "현재 페이지 번호", example = "1")
    private int pageNum;     // 현재 페이지

    @ApiModelProperty(value = "페이지당 항목 수", example = "10")
    private int pageSize;    // 페이지당 항목 수

    @ApiModelProperty(value = "전체 항목 수", example = "56")
    private long total;      // 전체 항목 수

    @ApiModelProperty(value = "전체 페이지 수", example = "6")
    private int pages;       // 전체 페이지 수
    //

    @ApiModelProperty(value = "즐겨찾기한 매물 리스트")
    private List<BookmarkBuilding> bookmarkBuildings;

    public static BookmarkList toDto(PageInfo<BookmarkBuilding> dtoList) {

      return new BookmarkList( // 페이지 네이션된 값을 담아서 response 로 만든다.
          dtoList.getPageNum(),
          dtoList.getPageSize(),
          dtoList.getTotal(),
          dtoList.getPages(),
          dtoList.getList()
      );
    }
  }

  // /member/mypage/edit Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "개인 정보 수정 페이지 응답 DTO")
  public static class EditMyPage {

    @ApiModelProperty(value = "현재 닉네임", example = "zangbuUser01")
    private String nickName;
    //
    @ApiModelProperty(value = "현재 비밀번호", example = "NewPassword123!")
    private String password;
  }

  // /member/mypage/edit/nickname Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "닉네임 변경 응답 DTO")
  public static class EditNicknameResponse {

    @ApiModelProperty(value = "변경된 닉네임", example = "zangbuUser02")
    private String nickName;
  }

  // /member/mypage/edit/notification/consent
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "알림 수신 동의 변경 응답 DTO")
  public static class EditNotificationConsentResponse {

    @ApiModelProperty(value = "알림 수신 동의 여부", example = "true")
    private Boolean consent;
  }

  // /member/mypage/notification/consent
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "알림 수신 동의 여부 조회 응답 DTO")
  public static class NotificationConsentCheck {

    @ApiModelProperty(value = "알림 수신 동의 여부", example = "true")
    private Boolean consent;
  }

  // 내가 등록한 매물 리스트 dto

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MyBuildingList {
    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
    private List<MyBuildingElement> buildings;

    public static MyBuildingList toDto(PageInfo<Building> pageInfo) {
      List<MyBuildingElement> list = pageInfo.getList().stream()
              .map(MyBuildingElement::fromEntity)
              .collect(Collectors.toList());

      return new MyBuildingList(
              pageInfo.getPageNum(),
              pageInfo.getPageSize(),
              pageInfo.getTotal(),
              pageInfo.getPages(),
              list
      );
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MyBuildingElement {
    private Long buildingId;
    private String sellerNickname;
    private String saleType;
    private Integer price;
    private Long deposit;
    private Integer bookmarkCount;
    private String createdAt;
    private String buildingName;
    private String sellerType;
    private String propertyType;
    private String moveDate;
    private String infoOneline;
    private String infoBuilding;
    private String contactName;
    private String contactPhone;
    private String facility;
    private float size;
    private String memberId;
    private Long complexId;

    public static MyBuildingElement fromEntity(Building b) {
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      return new MyBuildingElement(
              b.getBuildingId(),
              b.getSellerNickname(),
              b.getSaleType() != null ? b.getSaleType().name() : null,
              b.getPrice(),
              b.getDeposit(),
              b.getBookmarkCount(),
              b.getCreatedAt() != null ? b.getCreatedAt().format(fmt) : null,
              b.getBuildingName(),
              b.getSellerType() != null ? b.getSellerType().name() : null,
              b.getPropertyType() != null ? b.getPropertyType().name() : null,
              b.getMoveDate() != null ? b.getMoveDate().format(fmt) : null,
              b.getInfoOneline(),
              b.getInfoBuilding(),
              b.getContactName(),
              b.getContactPhone(),
              b.getFacility(),
              b.getSize(),
              b.getMemberId(),
              b.getComplexId()
      );
    }
  }

}

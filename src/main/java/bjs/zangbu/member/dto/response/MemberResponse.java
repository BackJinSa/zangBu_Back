package bjs.zangbu.member.dto.response;

import bjs.zangbu.building.vo.Building;
import bjs.zangbu.member.dto.join.BookmarkBuilding;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
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
  @ApiModel(description = "내가 등록한 매물 목록 응답 DTO")
  public static class MyBuildingList {

    @ApiModelProperty(value = "현재 페이지 번호", example = "1")
    private int pageNum;     // 현재 페이지

    @ApiModelProperty(value = "페이지당 항목 수", example = "10")
    private int pageSize;    // 페이지당 항목 수

    @ApiModelProperty(value = "전체 항목 수", example = "35")
    private long total;      // 전체 항목 수

    @ApiModelProperty(value = "전체 페이지 수", example = "4")
    private int pages;       // 전체 페이지 수

    @ApiModelProperty(value = "내가 등록한 매물 리스트")
    private List<MyBuilding> buildings;

    public static MyBuildingList toDto(PageInfo<Building> voList) {
      List<MyBuilding> dtoList = voList.getList().stream()
              .map(MyBuilding::toDto) // VO → DTO 변환
              .collect(Collectors.toList());

      return new MyBuildingList(
              voList.getPageNum(),
              voList.getPageSize(),
              voList.getTotal(),
              voList.getPages(),
              dtoList
      );
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "내가 등록한 매물 단일 응답 DTO")
  public static class MyBuilding {

    @ApiModelProperty(value = "건물 ID", example = "101")
    private Long buildingId;

    @ApiModelProperty(value = "판매자 닉네임", example = "홍길동")
    private String sellerNickname;

    @ApiModelProperty(value = "매물 판매 유형", example = "MONTHLY_RENT")
    private String saleType;

    @ApiModelProperty(value = "매매가", example = "50000")
    private Integer price;

    @ApiModelProperty(value = "보증금", example = "1000")
    private Long deposit;

    @ApiModelProperty(value = "이 매물을 찜한 사용자 수", example = "12")
    private Integer bookmarkCount;

    @ApiModelProperty(value = "등록 일시", example = "2025-08-01T12:30:00")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "매물명", example = "강남 오피스텔")
    private String buildingName;

    @ApiModelProperty(value = "판매자 유형", example = "AGENCY")
    private String sellerType;

    @ApiModelProperty(value = "부동산 유형", example = "OFFICETEL")
    private String propertyType;

    @ApiModelProperty(value = "입주 가능일", example = "2025-09-01T00:00:00")
    private LocalDateTime moveDate;

    @ApiModelProperty(value = "한 줄 소개", example = "역세권 풀옵션")
    private String infoOneline;

    @ApiModelProperty(value = "상세 설명", example = "풀옵션, 관리비 저렴")
    private String infoBuilding;

    @ApiModelProperty(value = "담당자 이름", example = "홍길동")
    private String contactName;

    @ApiModelProperty(value = "담당자 연락처", example = "010-1234-5678")
    private String contactPhone;

    @ApiModelProperty(value = "시설 정보", example = "엘리베이터, 주차장")
    private String facility;

    @ApiModelProperty(value = "면적", example = "22.5")
    private Float size;

    @ApiModelProperty(value = "회원 ID(UUID)", example = "user-1234-abcd")
    private String memberId;

    @ApiModelProperty(value = "단지 ID", example = "77")
    private Long complexId;

    // VO → DTO 변환
    public static MyBuilding toDto(Building vo) {
      return new MyBuilding(
              vo.getBuildingId(),
              vo.getSellerNickname(),
              vo.getSaleType() != null ? vo.getSaleType().name() : null,
              vo.getPrice(),
              vo.getDeposit(),
              vo.getBookmarkCount(),
              vo.getCreatedAt(),
              vo.getBuildingName(),
              vo.getSellerType() != null ? vo.getSellerType().name() : null,
              vo.getPropertyType() != null ? vo.getPropertyType().name() : null,
              vo.getMoveDate(),
              vo.getInfoOneline(),
              vo.getInfoBuilding(),
              vo.getContactName(),
              vo.getContactPhone(),
              vo.getFacility(),
              vo.getSize(),
              vo.getMemberId(),
              vo.getComplexId()
      );
    }
  }

}

package bjs.zangbu.member.dto.response;

import bjs.zangbu.member.dto.join.BookmarkBuilding;
import com.github.pagehelper.PageInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponse {

  // /member/mypage/favorites?page={page}&size={size}
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(name = "BookmarkList", description = "즐겨찾기 매물 목록 응답 DTO")
  public static class BookmarkList {

    //         @Schema(description = "현재 페이지 번호", example = "1")
    private int pageNum;     // 현재 페이지
    //         @Schema(description = "페이지당 항목 수", example = "10")
    private int pageSize;    // 페이지당 항목 수
    //         @Schema(description = "전체 항목 수", example = "56")
    private long total;      // 전체 항목 수
    //         @Schema(description = "전체 페이지 수", example = "6")
    private int pages;       // 전체 페이지 수
    //
//     @Schema(description = "즐겨찾기한 매물 리스트")
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
//     @Schema(name = "EditMyPage", description = "개인 정보 수정 페이지 응답 DTO")
  public static class EditMyPage {

    //         @Schema(description = "현재 닉네임", example = "zangbuUser01")
    private String nickName;
    //
//     @Schema(description = "현재 비밀번호", example = "NewPassword123!")
    private String password;
  }

  // /member/mypage/edit/nickname Response
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(name = "EditNicknameResponse", description = "닉네임 변경 응답 DTO")
  public static class EditNicknameResponse {

    //         @Schema(description = "변경된 닉네임", example = "zangbuUser02")
    private String nickName;
  }

  // /member/mypage/edit/notification/consent
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(name = "EditNotificationConsentResponse", description = "알림 수신 동의 변경 응답 DTO")
  public static class EditNotificationConsentResponse {

    //         @Schema(description = "알림 수신 동의 여부", example = "true")
    private Boolean consent;
  }

  // /member/mypage/notification/consent
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(name = "NotificationConsentCheck", description = "알림 수신 동의 여부 조회 응답 DTO")
  public static class NotificationConsentCheck {

    //         @Schema(description = "알림 수신 동의 여부", example = "true")
    private Boolean consent;
  }
}

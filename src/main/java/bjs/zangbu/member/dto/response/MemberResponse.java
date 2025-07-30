package bjs.zangbu.member.dto.response;

import bjs.zangbu.member.dto.join.BookmarkBuilding;
import com.github.pagehelper.PageInfo;
import com.google.type.DateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class MemberResponse {

    // /member/mypage/favorites?page={page}&size={size}
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkList{
        private int pageNum;     // 현재 페이지
        private int pageSize;    // 페이지당 항목 수
        private long total;      // 전체 항목 수
        private int pages;       // 전체 페이지 수

        private List<BookmarkBuilding> bookmarkBuildings;

        public static BookmarkList toDto(PageInfo<BookmarkBuilding> dtoList){

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
    public static class EditMyPage{
        private String nickName;
        private String password;
    }

    // /member/mypage/edit/nickname Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNicknameResponse{
        private String nickName;
    }

    // /member/mypage/edit/notification/consent
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditNotificationConsentResponse{
        private Boolean consent;
    }

    // /member/mypage/notification/consent
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationConsentCheck{
        private Boolean consent;
    }
}

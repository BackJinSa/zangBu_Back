package bjs.zangbu.notification.dto.response;

import bjs.zangbu.notification.vo.Notification;
import bjs.zangbu.notification.vo.Type;
import com.github.pagehelper.PageInfo;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationResponse {

  /** 타입별 전체 개수용 내부 DTO (Mapper에서 바로 매핑받음) */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TypeCounts {
    private long all;       // 전체
    private long building;  // BUILDING
    private long trade;     // TRADE
    private long review;    // REVIEW

    /** 프론트에 주기 편하게 Map 형태로 변환 */
    public Map<String, Long> toMap() {
      Map<String, Long> m = new HashMap<>();
      m.put("ALL", all);
      m.put("BUILDING", building);
      m.put("TRADE", trade);
      m.put("REVIEW", review);
      return m;
    }
  }

  /**
   * 전체 알림 리스트 응답 Response /notification/all
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NotificationAll {

    // @Schema(description = "요청 페이지 번호 (1부터 시작)", example = "1")
    private int pageNum;
    // @Schema(description = "페이지당 알림 개수", example = "10")
    private int pageSize;
    // @Schema(description = "전체 알림 개수", example = "42")
    private long totalElements;
    // @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;
    // @Schema(description = "알림 목록")
    private List<NotificationElement> notifications;

    // 타입별 전체 개수 (항상 전체 테이블 기준)
    private Map<String, Long> filterCounts;

    // PageInfo<Notification> → DTO로 변환
    // 목록 + 타입별 전체 카운트 동시 포함
    public static NotificationAll toDto(PageInfo<Notification> pageInfo, TypeCounts counts) {
      List<NotificationElement> list = pageInfo.getList().stream()
              .map(NotificationElement::toDto)
              .collect(Collectors.toList());
      return new NotificationAll(
              pageInfo.getPageNum(),
              pageInfo.getPageSize(),
              pageInfo.getTotal(),
              pageInfo.getPages(),
              list,
              counts != null ? counts.toMap() : null
      );
    }
  }



  /* ========================================================= */

  /**
   * 개별 알림 요소
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NotificationElement {

    //         @Schema(description = "알림 식별자", example = "13")
    private Long notificationId;      // 알림 식별 id ex) 13
    //
//     @Schema(description = "알림 제목", example = "시세 변동 알림")
    private String title;             // 알림 제목 ex) "찜한 매물 거래가 업데이트"
    //
//     @Schema(description = "알림 메시지", example = "역삼동 아파트의 실거래가가 업데이트되었습니다.")
    private String message;          // 알림 메시지 ex) "서초동 아파트 실거래가가 업데이트되었습니다."
    //
//     @Schema(description = "알림 읽음 여부", example = "false")
    private boolean isRead;           // 알림 읽음 여부 ex) false
    //
//     @Schema(description = "알림 유형", example = "TRADE")
    private String type;              // 알림 유형 ex) "TRADE"
    //
//     @Schema(description = "생성 시간(상대 표현)", example = "1시간 전")
    private String createdAt;         // 알림 등록 시간 ex) "1시간 전"
    //
//     @Schema(description = "가격 라벨", example = "전세 5억")
    private String priceLabel;        // 알림 등록한 찜한 매물의 가격 ex) "전세 5억"
    //
//     @Schema(description = "주소", example = "강남구 역삼동")
    private String address;           // 알림 등록한 찜한 매물의 주소 ex) "강남구 역삼동"
    //
//     @Schema(description = "리뷰 평점", example = "3")
    private int rank;                 // 알림 등록한 찜한 매물의 리뷰 평점 ex) 2

    // VO → DTO 변환 (프론트에 보내줄 필요한 데이터만 추출해서 담아줌)
    public static NotificationElement toDto(Notification vo) {
      return new NotificationElement(
          vo.getNotificationId(), // 알림 식별 id
          generateTitle(vo), // 알림의 제목을 가공해줌 generateTitle 메서드 호출
          vo.getMessage(), // 알림 메시지
          vo.isRead(), // 알림 읽음 여부
          vo.getType().name(), // 알림 유형
          formatTimeAgo(vo.getCreatedAt()), // 알림의 시간을 가공해줌 formatTimeAgo 메서드 호출
          formatPriceLabel(vo), // 알림의 가격 라벨을 만들어줌 formatPriceLabel 메서드 호출
          vo.getAddress(), // 알림 등록한 찜한 매물의 주소
          vo.getRank() // 알림 등록한 찜한 매물의 리뷰
      );
    } // toDto

    // 알림의 제목을 생성해주는 메서드
    private static String generateTitle(Notification vo) {
      // type에 따라 총 3종류의 제목이 생성됨
      if (vo.getType() == Type.TRADE) {
        return "실거래 발생 알림";
      }
      if (vo.getType() == Type.BUILDING) {
        return "시세 변동 알림";
      }
      if (vo.getType() == Type.REVIEW) {
        return "리뷰 등록 알림";
      }
      return "기타 알림";
      // 생성된 제목을 return
    } // generateTitle

    // 시간 포맷팅 ex) "3시간 전", "2일 전" 등
    private static String formatTimeAgo(Date createdAt) {
      // 단위는 밀리초(1초 = 1000밀리초)
      // 얼마나 시간이 지났는지 =      현재 시간       -  알림이 만들어진 시간
      long diffMillis = System.currentTimeMillis() - createdAt.getTime();
      // 밀리초를 '초'로 바꿔준다
      long diffSeconds = diffMillis / 1000;

      // 1분보다 덜 지났으면, 그냥 "x초전" 으로 나타냄
      if (diffSeconds < 60) {
        return diffSeconds + "초 전";
      }
      long diffMinutes = diffSeconds / 60;

      // 1시간보다 덜 지났으면, 그냥 "x분전" 으로 나타냄
      if (diffMinutes < 60) {
        return diffMinutes + "분 전";
      }
      long diffHours = diffMinutes / 60;

      // 24시간(하루)보다 덜 지났으면, 그냥 "x시간전" 으로 나타냄
      if (diffHours < 24) {
        return diffHours + "시간 전";
      }
      long diffDays = diffHours / 24;

      // 24시간보다 많이 지났으면, "x일 전" 으로 나타냄
      return diffDays + "일 전";
    } // formatTimeAgo

    // 가격 라벨 포맷팅 ("전세 5억", "월세 500만", "매매 3.5억" 등)
    private static String formatPriceLabel(Notification vo) {
      String label = "";
      // saleType에 따라 전세, 월세, 매매 구분
      switch (vo.getSaleType()) {
        case CHARTER:
          label = "전세 " + formatMoney(vo.getPrice());
          break;
        case MONTHLY:
          label = "월세 " + formatMoney(vo.getPrice());
          break;
        case TRADING:
          label = "매매 " + formatMoney(vo.getPrice());
          break;
      }
      return label;
    } // formatPriceLabel

    // 숫자 → 단위 변환 (ex: 35000 → 3.5억) db에 가격이 1이면 1만원임
    public static String formatMoney(int price) {
      // 만약 금액이 10000 이상이면 "억" 단위로 바꿔줘야됨
      // price = 35000 이라고 가정하면
      if (price >= 10000) {
        //     price/10000 = 3          price%10000= 5000/1000 = 5
        return (price / 10000) + "." + ((price % 10000) / 1000) + "억";
        // 결과 3.5억
      } else {
        return price + "만";
        // price가 10000을 넘지 안으면 억단위가 아니면
        // 그냥 "8000만" 이런식으로 보여주면됨
      }
    } // formatMoney
  } // NotificationElement


  // 읽음 처리된 알림 개수 -> NotificationService의 markAllAsRead 리턴값으로 사용
  // /notification/read/all
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MarkAllReadResult {

    //         @Schema(description = "읽음 처리된 알림 개수", example = "5")
    private int processedCount;
  }


}

package bjs.zangbu.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import bjs.zangbu.building.vo.SellerType;
import bjs.zangbu.deal.vo.DealEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatResponse {

  // /pub/chat.message
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @ApiModel(description = "메시지 전송 응답 DTO")
  public static class SendMessageResponse {

    @ApiModelProperty(value = "메시지 타입", example = "SYSTEM") // USER | SYSTEM
    private String type;            // 추가 (없으면 프론트에서 'USER'로 간주)
    @ApiModelProperty(value = "보낸 메시지 내용", example = "안녕")
    private String message;          //보낸 메시지 내용
    @ApiModelProperty(value = "보낸 사람 닉네임", example = "김부동산")
    private String sendNickname;     //보낸 사람 닉네임
    @ApiModelProperty(value = "메시지 전송 시간", example = "14:32")
    private String createdAt;        //메시지 보낸 시간
    @ApiModelProperty(value = "보낸 사람 ID", example = "user-uuid-123")
    private String senderId;        //프론트에서 isMine(우측, 좌측 구분 위해서)
  }


  // /chat/room/{roomId}?lastMessageId={lastMessageId}&limit={limit}
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @ApiModel(description = "채팅방 내 메시지 목록 응답 DTO")
  public static class ChatMessageListResponse {

    //roomId에 해당하는 채팅방의 메시지들 마지막부터 limit개 불러오기
    @ApiModelProperty(value = "건물 이름", example = "힐스테이트 서초")
    private String buildingName;

    @ApiModelProperty(value = "판매자 닉네임", example = "홍길동")
    private String sellerNickname;

    @ApiModelProperty(value = "판매자 유형", example = "집주인")
    private String sellerType;

    @ApiModelProperty(value = "거래 상태", example = "거래 진행 중")
    private DealEnum status;

    @ApiModelProperty(value = "채팅방 내 메시지 목록")
    private List<Message> messageList;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @ApiModel(description = "채팅 메시지 응답 DTO")
  public static class Message {

    @ApiModelProperty(value = "메시지 ID", example = "123")
    private Long messageId;

    @ApiModelProperty(value = "메시지 내용", example = "안녕하세요")
    private String message;

    @ApiModelProperty(value = "보낸 사람 닉네임", example = "보낸사람닉네임")
    private String senderNickname;

    @ApiModelProperty(value = "메시지 작성 시간", example = "15:20")
    private String createdAt;
  }

  //     /chat/room/enter/{roomId}
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ApiModel(description = "채팅방 상세 정보 응답 DTO")
  public static class ChatRoomDetailResponse {
    @ApiModelProperty(value = "채팅방 ID", example = "room-uuid-123")
    private String chatRoomId;

    @ApiModelProperty(value = "건물 ID", example = "101")
    private Long buildingId;

    @ApiModelProperty(value = "사용자 ID", example = "user-001")
    private String userId;

    @ApiModelProperty(value = "판매자 닉네임", example = "판매자 닉네임")
    private String sellerNickname;

    @ApiModelProperty(value = "채팅방 생성 시간", example = "2025-07-10T17:32:00")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "입장 안내 메시지", example = "김부동산님이 입장하셨습니다.")
    private String info;
    //nickname + "님이 입장하셨습니다." // 201 Created
    //"작성자는 입장할 수 없습니다." // 409 Conflict
    //"잘못된 접근입니다." // 400 Bad Request
  }

  //    /chat/list?page={page}&size={size}&type={type}
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ApiModel(description = "채팅방 목록 응답 DTO")
  public static class ChatRoomListResponse {

    @ApiModelProperty(value = "채팅방 ID", example = "room-uuid-123")
    private String chatRoomId;

    @ApiModelProperty(value = "건물 이름", example = "푸르지오 오피스텔")
    private String buildingName;

    @ApiModelProperty(value = "마지막 메시지", example = "언제 입주 가능할까요?")
    private String lastMessage;

    @ApiModelProperty(value = "마지막 메시지 시간", example = "2025-07-10 11:58")
    private String lastMessageTime;

    @ApiModelProperty(value = "상대방 닉네임", example = "김구매자")
    private String otherUserNickname;

    @ApiModelProperty(value = "거래 상태", example = "완료")
    private DealEnum status;

    @ApiModelProperty(value = "판매자 타입", example = "세입자")
    private SellerType sellerType;

    @ApiModelProperty(value = "안 읽은 메시지 개수", example = "3")
    private int unreadCount;

    @ApiModelProperty(value = "사용자 기준 대화 타입", example = "BUY")
    private String type;

    @ApiModelProperty(value = "매물 가격", example = "100000000")
    private Integer price;

    // 표시용(파생) 필드: DB 미저장 - 메시지 아직 보내지 않은 채팅방일 때
    public String getLastMessagePreview() {
      return (lastMessage != null && !lastMessage.trim().isEmpty())
              ? lastMessage
              : "채팅을 시작해보세요";
    }
  }


  //페이지 래퍼 DTO
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @ApiModel(description = "채팅방 목록 페이지 응답")
  public static class ChatRoomListPage {
    @ApiModelProperty(value = "채팅방 목록")
    private List<ChatRoomListResponse> items;

    @ApiModelProperty(value = "전체 개수", example = "37")
    private long total;

    @ApiModelProperty(value = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;

    @ApiModelProperty(value = "탭 별 카운트")
    private Counts counts;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(description = "탭별 카운트 정보")
    public static class Counts {

      @ApiModelProperty(value = "전체 탭 카운트")
      private SimpleCount ALL;

      @ApiModelProperty(value = "구매 탭 카운트")
      private SimpleCount BUY;

      @ApiModelProperty(value = "판매 탭 카운트")
      private SimpleCount SELL;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(description = "탭 카운트 단일 객체")
    public static class SimpleCount {

      @ApiModelProperty(value = "총 개수", example = "10")
      private int count;

      @ApiModelProperty(value = "읽지 않은 방 개수", example = "3")
      private int unread;
    }
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @ApiModel(description = "채팅방 존재 여부 응답 객체")
  public static class ChatRoomExistResponse {
    @ApiModelProperty(value = "존재 여부", example = "true")
    private boolean exists;
    @ApiModelProperty(value = "채팅방 id")
    private String chatRoomId;   // 없으면 null
  }

}

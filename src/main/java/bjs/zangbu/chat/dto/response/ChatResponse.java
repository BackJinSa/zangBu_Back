package bjs.zangbu.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;
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
//     @Schema(description = "메시지 전송 응답 DTO")
  public static class SendMessageResponse {

    //
//     @Schema(description = "보낸 메시지 내용", example = "안녕")
    private String message;          //보낸 메시지 내용
    //         @Schema(description = "보낸 사람 닉네임", example = "김부동산")
    private String sendNickname;     //보낸 사람 닉네임
    //         @Schema(description = "메시지 전송 시간", example = "14:32")
    private String createdAt;        //메시지 보낸 시간
  }


  // /chat/room/{roomId}?lastMessageId={lastMessageId}&limit={limit}
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
//     @Schema(description = "채팅방 내 메시지 목록 응답 DTO")
  public static class ChatMessageListResponse {

    //roomId에 해당하는 채팅방의 메시지들 마지막부터 limit개 불러오기
//         @Schema(description = "건물 이름", example = "힐스테이트 서초")
    private String buildingName;    //건물 이름
    //         @Schema(description = "판매자 닉네임", example = "홍길동")
    private String sellerNickname;  //판매자 닉네임
    //         @Schema(description = "판매자 유형", example = "집주인")
    private String sellerType;      //세입자, 집주인
    //         @Schema(description = "거래 상태", example = "거래 진행 중")
    private String status;          //거래 상태
    //         @Schema(description = "채팅방 내 메시지들")
    private List<Message> messageList;      //채팅방 내 메시지들
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
//     @Schema(description = "채팅 메시지 응답 DTO")
  public static class Message {

    //         @Schema(description = "메시지 ID", example = "123")
    private Long messageId;     //메시지 id
    //         @Schema(description = "메시지 내용", example = "안녕하세요")
    private String message;     //메시지 내용
    //         @Schema(description = "보낸 사람 닉네임", example = "보낸사람닉네임")
    private String senderNickname;      //보낸 사람 닉네임
    //         @Schema(description = "메시지 작성 시간", example = "15:20")
    private String createdAt;           //메시지 생성 날짜
    //없는 경우 : ""   오늘인 경우 : "HH:mm"  올해인 경우 : "MM/dd"   이외 : "yyyy/MM/dd"
  }

  //     /chat/room/enter/{roomId}
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
//     @Schema(description = "채팅방 상세 정보 응답 DTO")
  public static class ChatRoomDetailResponse {

    //채팅방의 상세정보를 응답할 때 사용
//         @Schema(description = "채팅방 ID", example = "room-uuid-123")
    private String chatRoomId;         //채팅방 id
    //         @Schema(description = "건물 ID", example = "101")
    private Long buildingId;           //매물 id
    //         @Schema(description = "사용자 ID", example = "user-001")
    private String userId;            //사용자 id
    //         @Schema(description = "판매자 닉네임", example = "판매자 닉네임")
    private String sellerNickname;     //판매자 닉네임
    //         @Schema(description = "채팅방 생성 시간", example = "17:32")
    private LocalDateTime createdAt;   //메시지 생성 시각
    //         @Schema(description = "입장 안내 메시지", example = "김부동산님이 입장하셨습니다.")
    private String info;             //결과 메시지
    //nickname + "님이 입장하셨습니다." // 201 Created
    //"작성자는 입장할 수 없습니다." // 409 Conflict
    //"잘못된 접근입니다." // 400 Bad Request
  }

  //    /chat/list?page={page}&size={size}&type={type}
  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
//     @Schema(description = "채팅방 목록 응답 DTO")
  public static class ChatRoomListResponse {

    //         @Schema(description = "채팅방 ID", example = "room-uuid-123")
    private String chatRoomId;              //채팅방 id
    //         @Schema(description = "건물 이름", example = "푸르지오 오피스텔")
    private String buildingName;            //매물 이름
    //         @Schema(description = "마지막 메시지", example = "언제 입주 가능할까요?")
    private String lastMessage;             //해당 채팅방의 마지막 메시지
    //         @Schema(description = "마지막 메시지 시간", example = "2025-07-10 11:58")
    private String lastMessageTime;         //해당 채팅방의 마시막 메시지 보낸 시각
    //         @Schema(description = "상대방 닉네임", example = "김구매자")
    private String otherUserNickname;       //대화 상대방 닉네임
    //         @Schema(description = "거래 상태", example = "완료")
    private String status;                  //거래 상태
    //         @Schema(description = "판매자 타입", example = "세입자")
    private String sellerType;              //판매자 타입 : 집주인 or 세입자
    //         @Schema(description = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;                //페이지네이션 다음 여부
    //         @Schema(description = "안 읽은 메시지 개수", example = "3")
    private int unreadCount;                //채팅방에서 안 읽은 메시지 개수
  }

}

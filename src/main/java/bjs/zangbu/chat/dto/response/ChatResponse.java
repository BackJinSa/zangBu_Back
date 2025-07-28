package bjs.zangbu.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ChatResponse {

    // /pub/chat.message
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendMessageResponse {
        private String message;          //보낸 메시지 내용
        private String sendNickname;     //보낸 사람 닉네임
        private String createdAt;        //메시지 보낸 시간
    }


    // /chat/room/{roomId}?lastMessageId={lastMessageId}&limit={limit}
    public static class ChatMessageListResponse {
        //roomId에 해당하는 채팅방의 메시지들 마지막부터 limit개 불러오기
        private String buildingName;    //건물 이름
        private String sellerNickname;  //판매자 닉네임
        private String sellerType;      //세입자, 집주인
        private String status;          //거래 상태
        private List<Message> messageList;      //채팅방 내 메시지들
    }

    public static class Message {
        private Long messageId;     //메시지 id
        private String message;     //메시지 내용
        private String senderNickname;      //보낸 사람 닉네임
        private String createdAt;           //메시지 생성 날짜
        //없는 경우 : ""   오늘인 경우 : "HH:mm"  올해인 경우 : "MM/dd"   이외 : "yyyy/MM/dd"
    }

    //     /chat/room/enter/{roomId}
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomDetailResponse {
        //채팅방의 상세정보를 응답할 때 사용
        private String chatRoomId;         //채팅방 id
        private Long buildingId;           //매물 id
        private String userId;            //사용자 id
        private String sellerNickname;     //판매자 닉네임
        private LocalDateTime createdAt;   //메시지 생성 시각
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
    public static class ChatRoomListResponse {
        private String chatRoomId;              //채팅방 id
        private String buildingName;            //매물 이름
        private String lastMessage;             //해당 채팅방의 마지막 메시지
        private String lastMessageTime;         //해당 채팅방의 마시막 메시지 보낸 시각
        private String otherUserNickname;       //대화 상대방 닉네임
        private String status;                  //거래 상태
        private String sellerType;              //판매자 타입 : 집주인 or 세입자
        private boolean hasNext;                //페이지네이션 다음 여부
        private int unreadCount;                //채팅방에서 안 읽은 메시지 개수
    }

}

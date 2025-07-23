package bjs.zangbu.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChatRoomResponse {

    //     /chat/room/enter/{roomId}
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class EnterChatRoomResponse {
        //이미 존재하는 채팅방의 상세정보를 응답할 때 사용
        private String chatRoomId;         //채팅방 id
        private Long buildingId;           //매물 id
        private String buyerId;            //구매자 id
        private String sellerNickname;     //판매자 닉네임
        private LocalDateTime createdAt;   //메시지 생성 시각
    }


    //    /chat/list?page={page}&size={size}&type={type}
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class ChatRoomListDto {
        private String chatRoomId;      //채팅방 id
        private String buildingName;    //매물 이름
        private String lastMessage;     //해당 채팅방의 마지막 메시지
        private String lastMessageTime;     //해당 채팅방의 마시막 메시지 보낸 시각
        private String buyerNickname;       //구매자 닉네임
        private String sellerNickname;      //판매자 닉네임
        private String status;              //거래 상태
        private String sellerType;          //판매자 타입 : 집주인 or 세입자
        private boolean hasNext;            //페이지네이션 다음 여부

    }

}

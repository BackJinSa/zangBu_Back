package bjs.zangbu.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomRequest {

    //  /chat/list?page={page}&size={size}&type={type}
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ChatRoomListRequest {
        private int page;       //페이지
        private int size;       //한 페이지에 보여줄 채팅방 개수
        private String type; // "BUYER" or "SELLER"
    }

}

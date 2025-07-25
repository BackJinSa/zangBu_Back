package bjs.zangbu.chat.service;

import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;

import java.util.List;

public interface ChatService {

    //메시지 전송
    void sendMessage(ChatMessage message);

    //chatRoomId로 해당 채팅방의 메시지들 limit개 불러오기
    List<ChatMessage> getMessages(String chatRoomId, Long lastMessageId, int limit);

    //chatRoomId로 해당 채팅방의 상세정보 가져오기
    ChatRoom getChatRoomDetail(String chatRoomId);

    //userId를 기준으로 사용자가 참여한 채팅방 리스트 가져옴, page랑 size는 페이지네이션용
    List<ChatRoom> getChatRoomList(String userId, String type, int page, int size);

    //채팅방 중복 생성 방지를 위해 채팅방 존재 유무 확인 후 있으면 해당 채팅방 리턴, 없으면 생성 후 채팅방 리턴
    ChatRoom existsChatRoom(Long buildingId, String consumerId);

    //채팅방 생성
    ChatRoom createChatRoom(ChatRoom chatRoom);

    //채팅방 삭제
    void deleteChatRoom(String chatRoomId);
}

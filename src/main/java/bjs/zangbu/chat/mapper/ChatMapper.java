package bjs.zangbu.chat.mapper;

import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;

import java.util.List;

public interface ChatMapper {

    //메시지 전송 시 DB에 메시지 저장
    void insertMessage(ChatMessage chatMessage);

    //chatRoomId에 해당하는 메시지들 조회(제일 마지막 메시지부터 limit개 -> 더보기 클릭 시 limit개씩 추가 조회)
    List<ChatMessage> selectMessagesByRoomId(String chatRoomId, Long lastMessageId, int limit);

    //chatRoomId에 해당하는 채팅방 상세 조회
    ChatRoom selectChatRoomById(String chatRoomId);

    //userId에 해당하는 채팅방 목록 조회
    List<ChatRoom> selectChatRoomList(String userId, String type, int offset, int size);

    //채팅방 유무 확인(하나의 매물 + 구매자 당 하나의 채팅방이므로 중복 생성 방지용)
    boolean existsChatRoom(Long buildingId, String consumerId);

    //채팅방 생성
    void insertChatRoom(ChatRoom chatRoom);

    //채팅방 삭제 전 해당 채팅방의 메시지 삭제
    void deleteMessagesByRoomId(String chatRoomId);

    //채팅방 삭제
    void deleteChatRoom(String chatRoomId);
}

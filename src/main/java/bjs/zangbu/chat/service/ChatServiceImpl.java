package bjs.zangbu.chat.service;

import bjs.zangbu.chat.dto.request.ChatRequest;
import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.mapper.ChatMapper;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static bjs.zangbu.global.formatter.LocalDateFormatter.CreatedAt.formattingCreatedAt;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatMapper chatMapper;

    //메시지 전송
    @Override
    public ChatResponse.SendMessageResponse sendMessage(String chatRoomId, ChatRequest.SendMessageRequest request) {
        String senderId = "SENDER_ID";    // TODO: 인증 도입 시 변경
        LocalDateTime createdAt = LocalDateTime.now();

        ChatMessage message = request.toEntity(chatRoomId, senderId, createdAt);
        chatMapper.insertMessage(message);

        return ChatResponse.SendMessageResponse.builder()
                .message(message.getMessage())
                .sendNickname("보낸사람 닉네임")   //TODO: 보낸 사람 닉네임 조회 로직 추가
                .createdAt(formattingCreatedAt(message.getCreatedAt()))
                .build();
    }

    //chatRoomId 기준으로 해당 채팅방의 메시지들 가져오기
    @Override
    public List<ChatMessage> getMessages(String chatRoomId, Long lastMessageId, int limit) {
        return chatMapper.selectMessagesByRoomId(chatRoomId, lastMessageId, limit);
    }

    //chatRoomId 기준으로 채팅방 상세정보 가져오기
    @Override
    public ChatRoom getChatRoomDetail(String chatRoomId) {
        return chatMapper.selectChatRoomById(chatRoomId);
    }

    //사용자(userId)가 참여하고 있는 채팅방 목록 가져오기
    @Override
    public List<ChatRoom> getChatRoomList(String userId, String type, int page, int size) {
        int offset = (page - 1) * size;
        return chatMapper.selectChatRoomList(userId, type, offset, size);
    }

    //채팅방 유무 확인 - 채팅방 중복 생성 방지
    @Override
    public ChatRoom existsChatRoom(Long buildingId, String consumerId) {
        return chatMapper.existsChatRoom(buildingId, consumerId);
    }

    //채팅방 생성
    @Override
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        Long buildingId = chatRoom.getBuildingId();
        String consumerId = chatRoom.getConsumerId();

        ChatRoom existsedChatRoom = chatMapper.existsChatRoom(buildingId, consumerId);

        if (existsedChatRoom == null) { //buildingId, consumerId으로 채팅방이 없을 때만 채팅방 생성
            chatMapper.insertChatRoom(chatRoom);
            return chatRoom;
        } else {
            //존재하는 경우에 어떻게 처리할지 나중에 적절히 코드 변경
            return existsedChatRoom;
        }
    }

    //채팅방 삭제
    @Override
    @Transactional
    public void deleteChatRoom(String chatRoomId) {
        //chatRoomId의 ChatMessage들 삭제
        chatMapper.deleteMessagesByRoomId(chatRoomId);
        //chatRoomId의 ChatRoom 삭제
        chatMapper.deleteChatRoom(chatRoomId);
    }
}

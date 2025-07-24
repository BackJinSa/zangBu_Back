package bjs.zangbu.chat.service;

import bjs.zangbu.chat.mapper.ChatMapper;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatMapper chatMapper;

    @Override
    public void sendMessage(ChatMessage message) {
        chatMapper.insertMessage(message);
    }

    @Override
    public List<ChatMessage> getMessages(String chatRoomId, Long lastMessageId, int limit) {
        return chatMapper.selectMessagesByRoomId(chatRoomId, lastMessageId, limit);
    }

    @Override
    public ChatRoom getChatRoomDetail(String chatRoomId) {
        return chatMapper.selectChatRoomById(chatRoomId);
    }

    @Override
    public List<ChatRoom> getChatRoomList(String userId, String type, int page, int size) {
        int offset = (page - 1) * size;
        return chatMapper.selectChatRoomList(userId, type, offset, size);
    }

    @Override
    public boolean existsChatRoom(Long buildingId, String consumerId) {
        return chatMapper.existsChatRoom(buildingId, consumerId);
    }

    @Override
    public void createChatRoom(ChatRoom chatRoom) {
        chatMapper.insertChatRoom(chatRoom);
    }

    @Override
    @Transactional
    public void deleteChatRoom(String chatRoomId) {
        //chatRoomId의 ChatMessage들 삭제
        chatMapper.deleteMessagesByRoomId(chatRoomId);
        //chatRoomId의 ChatRoom 삭제
        chatMapper.deleteChatRoom(chatRoomId);
    }
}

package bjs.zangbu.chat.mapper;

import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;
import bjs.zangbu.global.config.RootConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Transactional
class ChatMapperTest {

    @Autowired
    private ChatMapper chatMapper;

    @Test
    void insertMessage() {
        ChatMessage message = ChatMessage.builder()
                .chatRoomId("test-room-123")
                .buildingId(1L)
                .senderId("user123")
                .complexId(101L)
                .message("Hello!")
                .createdAt(LocalDateTime.now())
                .build();

        int result = chatMapper.insertMessage(message);

        assertEquals(1, result);
        assertNotNull(message.getChatMessageId());
    }

    @Test
    void selectMessagesByRoomId() {
        List<ChatMessage> messages = chatMapper.selectMessagesByRoomId("test-room-123", 1, 10);
        assertNotNull(messages);
        assertTrue(messages.size() <= 10);
    }

    @Test
    void selectChatRoomById() {
        ChatRoom chatRoom = chatMapper.selectChatRoomById("test-room-123");
        assertNotNull(chatRoom);
        assertEquals("test-room-123", chatRoom.getChatRoomId());
    }

    @Test
    void selectChatRoomList() {
        List<ChatRoom> list = chatMapper.selectChatRoomList("user123", "ALL", 0, 20);
        assertNotNull(list);
    }

    @Test
    void existsChatRoom() {
        ChatRoom chatRoom = chatMapper.existsChatRoom(1L, "user123");
        if (chatRoom != null) {
            assertEquals(1L, chatRoom.getBuildingId());
            assertEquals("user123", chatRoom.getConsumerId());
        }
    }

    @Test
    void insertChatRoom() {
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId("test-room-456")
                .buildingId(1L)
                .consumerId("user123")
                .complexId("12345")
                .sellerNickname("판매자")
                .consumerNickname("구매자")
                .sellerVisible(true)
                .consumerVisible(true)
                .build();

        chatMapper.insertChatRoom(chatRoom);
        ChatRoom inserted = chatMapper.selectChatRoomById("test-room-456");

        assertNotNull(inserted);
        assertEquals("test-room-456", inserted.getChatRoomId());
    }

    @Test
    void updateSellerVisible() {
        chatMapper.updateSellerVisible("test-room-123");
        ChatRoom chatRoom = chatMapper.selectChatRoomById("test-room-123");
        assertFalse(chatRoom.getSellerVisible());
    }

    @Test
    void updateConsumerVisible() {
        chatMapper.updateConsumerVisible("test-room-123");
        ChatRoom chatRoom = chatMapper.selectChatRoomById("test-room-123");
        assertFalse(chatRoom.getConsumerVisible());
    }

    @Test
    void deleteMessagesByRoomId() {
        chatMapper.deleteMessagesByRoomId("test-room-123");
        List<ChatMessage> messages = chatMapper.selectMessagesByRoomId("test-room-123", 1, 10);
        assertTrue(messages.isEmpty());
    }

    @Test
    void deleteChatRoom() {
        chatMapper.deleteMessagesByRoomId("test-room-123"); // 메시지 먼저 삭제
        chatMapper.deleteChatRoom("test-room-123");
        ChatRoom deleted = chatMapper.selectChatRoomById("test-room-123");
        assertNull(deleted);
    }

    @Test
    void countUnreadMessages() {
        int count = chatMapper.countUnreadMessages("test-room-123", "user123");
        assertTrue(count >= 0);
    }

    @Test
    void selectLastMessageByRoomId() {
        ChatMessage lastMessage = chatMapper.selectLastMessageByRoomId("test-room-123");
        if (lastMessage != null) {
            assertEquals("test-room-123", lastMessage.getChatRoomId());
        }
    }

    @Test
    void selectMemberIdByNickname() {
        String memberId = chatMapper.selectMemberIdByNickname("구매자");
        assertNotNull(memberId);
    }
}
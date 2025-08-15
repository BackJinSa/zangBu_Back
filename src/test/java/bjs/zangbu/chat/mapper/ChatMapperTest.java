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
                .chatRoomId("room-001")
                .buildingId(1L)
                .senderId("user-001")
                .complexId(1L)
                .message("Hello!")
                .createdAt(LocalDateTime.now())
                .build();

        int result = chatMapper.insertMessage(message);

        assertEquals(1, result);
        assertNotNull(message.getChatMessageId());
    }

    @Test
    void selectMessagesByRoomId() {
        List<ChatMessage> messages = chatMapper.selectMessagesByRoomId("room-001", 1L, 10);
        assertNotNull(messages);
        assertTrue(messages.size() <= 10);
    }

    @Test
    void selectChatRoomById() {
        ChatRoom chatRoom = chatMapper.selectChatRoomById("room-001");
        assertNotNull(chatRoom);
        assertEquals("room-001", chatRoom.getChatRoomId());
    }

    @Test
    void selectChatRoomList() {
        List<ChatRoom> list = chatMapper.selectChatRoomList("user-001", "ALL", 0, 20);
        assertNotNull(list);
    }

    @Test
    void existsChatRoom() {
        ChatRoom chatRoom = chatMapper.existsChatRoom(1L, "user-001");
        if (chatRoom != null) {
            assertEquals(1L, chatRoom.getBuildingId());
            assertEquals("user-001", chatRoom.getConsumerId());
        }
    }

    @Test
    void insertChatRoom() {
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId("room-001")
                .buildingId(5L)
                .consumerId("user-001")
                .complexId(2L)
                .sellerNickname("유저6")
                .consumerNickname("유저8")
                .sellerVisible(true)
                .consumerVisible(true)
                .build();

        chatMapper.insertChatRoom(chatRoom);
        ChatRoom inserted = chatMapper.selectChatRoomById("room-001");

        assertNotNull(inserted);
        assertEquals("room-001", inserted.getChatRoomId());
    }

    @Test
    void updateSellerVisible() {
        chatMapper.updateSellerVisible("room-001");
        ChatRoom chatRoom = chatMapper.selectChatRoomById("room-001");
        assertFalse(chatRoom.getSellerVisible());
    }

    @Test
    void updateConsumerVisible() {
        chatMapper.updateConsumerVisible("room-001");
        ChatRoom chatRoom = chatMapper.selectChatRoomById("room-001");
        assertFalse(chatRoom.getConsumerVisible());
    }

    @Test
    void deleteMessagesByRoomId() {
        chatMapper.deleteMessagesByRoomId("room-001");
        List<ChatMessage> messages = chatMapper.selectMessagesByRoomId("room-001", 1L, 10);
        assertTrue(messages.isEmpty());
    }

    @Test
    void deleteChatRoom() {
        chatMapper.deleteMessagesByRoomId("room-001"); // 메시지 먼저 삭제
        chatMapper.deleteChatRoom("room-001");
        ChatRoom deleted = chatMapper.selectChatRoomById("room-001");
        assertNull(deleted);
    }

    @Test
    void countUnreadMessages() {
        int count = chatMapper.countUnreadMessages("room-001", "user-001");
        assertTrue(count >= 0);
    }

    @Test
    void selectLastMessageByRoomId() {
        ChatMessage lastMessage = chatMapper.selectLastMessageByRoomId("room-001");
        if (lastMessage != null) {
            assertEquals("room-001", lastMessage.getChatRoomId());
        }
    }

    @Test
    void selectMemberIdByNickname() {
        String memberId = chatMapper.selectMemberIdByNickname("유저2");
        assertNotNull(memberId);
    }
}
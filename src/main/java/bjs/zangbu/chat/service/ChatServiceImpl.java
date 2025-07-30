package bjs.zangbu.chat.service;

import bjs.zangbu.chat.dto.request.ChatRequest;
import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.mapper.ChatMapper;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;
import bjs.zangbu.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static bjs.zangbu.global.formatter.LocalDateFormatter.CreatedAt.formattingCreatedAt;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatMapper chatMapper;
    private final MemberMapper memberMapper;

    //메시지 전송
    @Override
    public ChatResponse.SendMessageResponse sendMessage(String senderId, ChatRequest.SendMessageRequest request){
        LocalDateTime createdAt = LocalDateTime.now();

        if (request == null) {
            throw new NullPointerException("request(SendMessageRequest)의 값이 null입니다.");
        }

        ChatMessage message = request.toEntity(senderId, createdAt);
        int result = chatMapper.insertMessage(message);
        if (result == 0) {
            throw new IllegalStateException("메시지 저장에 실패했습니다.");
        }

        //보낸 사람 닉네임 조회
        String senderNickname = memberMapper.getNicknameByMemberId(senderId);
        if (senderNickname == null) {
            throw new IllegalArgumentException(senderId + "님의 닉네임을 찾을 수 없습니다. ");
        }

        return ChatResponse.SendMessageResponse.builder()
                .message(message.getMessage())
                .sendNickname(senderNickname)
                .createdAt(formattingCreatedAt(message.getCreatedAt()))
                .build();
    }

    //chatRoomId 기준으로 해당 채팅방의 메시지들 가져오기
    @Override
    public List<ChatMessage> getMessages(String chatRoomId, long lastMessageId, int limit) {
        return chatMapper.selectMessagesByRoomId(chatRoomId, lastMessageId, limit);
    }

    //chatRoomId 기준으로 채팅방 상세정보 가져오기
    @Override
    public ChatRoom getChatRoomDetail(String chatRoomId) {
        ChatRoom room = chatMapper.selectChatRoomById(chatRoomId);
        if (room == null) {
            throw new IllegalArgumentException(chatRoomId+ "를 id로 하는 채팅방이 존재하지 않습니다.");
        }
        return room;
    }

    //사용자(userId)가 참여하고 있는 채팅방 목록 가져오기
    @Override
    public List<ChatResponse.ChatRoomListResponse> getChatRoomList(String userId, String type, int page, int size) {
        int offset = (page - 1) * size;

        //채팅방 목록 조회
        List<ChatRoom> chatRooms = chatMapper.selectChatRoomList(userId, type, offset, size);

        //응답 DTO 리스트 생성
        List<ChatResponse.ChatRoomListResponse> result = new ArrayList<>();

        for (ChatRoom room : chatRooms) {
            String chatRoomId = room.getChatRoomId();

            //채팅방의 안 읽은 메시지 수 조회
            int unreadCount = chatMapper.countUnreadMessages(chatRoomId, userId);

            //마지막 메시지 조회
            ChatMessage lastMessage = chatMapper.selectLastMessageByRoomId(chatRoomId);

            //대화 상대방 닉네임 조회
            String otherNickname = userId.equals(room.getConsumerId())
                    ? room.getSellerNickname()
                    : room.getConsumerNickname();

            result.add(ChatResponse.ChatRoomListResponse.builder()
                    .chatRoomId(chatRoomId)
                    .buildingName(room.getBuildingName())
                    .lastMessage(lastMessage != null ? lastMessage.getMessage() : null)
                    .lastMessageTime(lastMessage != null ? formattingCreatedAt(lastMessage.getCreatedAt()) : null)
                    .otherUserNickname(otherNickname)
                    .status(room.getStatus())
                    .sellerType(room.getSellerType())
                    .hasNext(chatRooms.size() == size) // 페이지 사이즈와 같으면 다음 있음
                    .unreadCount(unreadCount)
                    .build());
        }
        return result;
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
            String uuid = UUID.randomUUID().toString();
            chatRoom = ChatRoom.builder()
                    .chatRoomId(uuid)
                    .buildingId(chatRoom.getBuildingId())
                    .consumerId(chatRoom.getConsumerId())
                    .complexId(chatRoom.getComplexId())
                    .sellerNickname(chatRoom.getSellerNickname())
                    .consumerNickname(chatRoom.getConsumerNickname())
                    .sellerVisible(true)    // 초기값 true
                    .consumerVisible(true)  // 초기값 true
                    .build();

            chatMapper.insertChatRoom(chatRoom);
            return chatRoom;
        } else {
            //존재하는 경우에 어떻게 처리할지 나중에 적절히 코드 변경
            return existsedChatRoom;
        }
    }

    //채팅방 나가기
    @Override
    @Transactional
    public void leaveChatRoom(String chatRoomId, String userId) {

        ChatRoom chatRoom = chatMapper.selectChatRoomById(chatRoomId);
        if (chatRoom == null) {
            throw new IllegalArgumentException(chatRoomId+ "를 id로 하는 채팅방이 존재하지 않습니다.");
        }

        //나가려는 사용자가 판매자인지 구매자인지 확인
        boolean isSeller = userId.equals(chatMapper.selectMemberIdByNickname(userId));
        boolean isBuyer = userId.equals(chatRoom.getConsumerId());

        //나가려는 사용자의 채팅방 목록에서 해당 채팅방이 보이지 않도록 DB에서 변경
        if (isSeller) {
            chatMapper.updateSellerVisible(chatRoomId);
        } else if (isBuyer) {
            chatMapper.updateConsumerVisible(chatRoomId);
        } else {
            throw new IllegalStateException("채팅방 참여자가 아닙니다.");
        }

        //일대일 채팅에 참여한 둘 모두 나간 경우(visible = false인 경우)에 DB에서 완전 삭제
        if (!chatRoom.getSellerVisible() && !chatRoom.getConsumerVisible()) {
            //chatRoomId의 ChatMessage들 삭제
            chatMapper.deleteMessagesByRoomId(chatRoomId);
            //chatRoomId의 ChatRoom 삭제
            chatMapper.deleteChatRoom(chatRoomId);
        }
    }

    @Override
    public String getUserIdByNickname(String userId) {
        String nickname = chatMapper.selectMemberIdByNickname(userId);
        if (nickname == null) {
            throw new IllegalArgumentException(nickname+ "을 닉네임으로 하는 userId를 찾지 못했습니다.");
        }

        return nickname;
    }
}

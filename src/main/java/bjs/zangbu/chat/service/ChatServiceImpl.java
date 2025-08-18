package bjs.zangbu.chat.service;

import bjs.zangbu.building.mapper.BuildingMapper;
import bjs.zangbu.building.vo.Building;
import bjs.zangbu.chat.dto.request.ChatRequest;
import bjs.zangbu.chat.dto.response.ChatResponse;
import bjs.zangbu.chat.mapper.ChatMapper;
import bjs.zangbu.chat.vo.ChatMessage;
import bjs.zangbu.chat.vo.ChatRoom;
import bjs.zangbu.deal.vo.DealEnum;
import bjs.zangbu.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static bjs.zangbu.global.formatter.LocalDateFormatter.CreatedAt.formattingCreatedAt;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatMapper chatMapper;
    private final MemberMapper memberMapper;
    private final BuildingMapper buildingMapper;

    //메시지 전송
    @Override
    public ChatResponse.SendMessageResponse sendMessage(String senderId, String chatRoomId, ChatRequest.SendMessageRequest request){
        LocalDateTime createdAt = LocalDateTime.now();

        ChatRoom room = chatMapper.selectChatRoomById(chatRoomId);
        if (room == null) {
            throw new IllegalArgumentException(chatRoomId + "에 해당하는 채팅방이 존재하지 않습니다.");
        }

        if (request == null) {
            throw new NullPointerException("request(SendMessageRequest)의 값이 null입니다.");
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .buildingId(room.getBuildingId())
                .senderId(senderId)
                .complexId(room.getComplexId())
                .message(request.getMessage())
                .createdAt(createdAt)
                .isRead(false)
                .build();

       //db에 메시지 저장
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
                .senderId(senderId)
                .build();
    }

    //chatRoomId 기준으로 해당 채팅방의 메시지들 가져오기
    @Override
    public List<ChatMessage> getMessages(String chatRoomId, Long lastMessageId, int limit) {
        log.info("ChatServiceImpl");
        List<ChatMessage> messages =  chatMapper.selectMessagesByRoomId(chatRoomId, lastMessageId, limit);

        return messages;
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
        int offset = Math.max(0, (page - 1) * size);

        List<ChatRoom> chatRooms = chatMapper.selectChatRoomList(userId, type, offset, size);
        List<ChatResponse.ChatRoomListResponse> result = new ArrayList<>();

        for (ChatRoom room : chatRooms) {
            String chatRoomId = room.getChatRoomId();

            int unreadCount = chatMapper.countUnreadMessages(chatRoomId, userId);
            ChatMessage lastMessage = chatMapper.selectLastMessageByRoomId(chatRoomId);

            // 현재 사용자 관점의 반대편 닉네임
            String otherNickname = userId.equals(room.getConsumerId())
                    ? room.getSellerNickname()
                    : room.getConsumerNickname();

            // BUY/SELL 판정
            String roomType = userId.equals(room.getConsumerId()) ? "BUY" : "SELL";

            result.add(ChatResponse.ChatRoomListResponse.builder()
                    .chatRoomId(chatRoomId)
                    .buildingName(room.getBuildingName())
                    .lastMessage(lastMessage != null ? lastMessage.getMessage() : null)
                    .lastMessageTime(lastMessage != null ? formattingCreatedAt(lastMessage.getCreatedAt()) : null)
                    .otherUserNickname(otherNickname)    // 프론트 명세
                    .status(room.getStatus())
                    .sellerType(room.getSellerType())
                    .unreadCount(unreadCount)
                    .type(roomType)                   // 프론트 색 구분
                    .price(room.getPrice())           // SQL에서 SELECT 필요 매물 가격 필요
                    .build());
        }
        return result;
    }

    @Override
    public long countChatRoomList(String userId, String type) {
        return chatMapper.countChatRoomList(userId, type);
    }

    @Override
    public int countUnreadRooms(String userId, String type) {
        return chatMapper.countUnreadRooms(userId, type);
    }

    //채팅방 유무 확인 - 채팅방 중복 생성 방지
    @Override
    public ChatRoom existsChatRoom(Long buildingId, String consumerId) {
        return chatMapper.existsChatRoom(buildingId, consumerId);
    }

    //채팅방 생성
    @Override
    @Transactional
    public ChatRoom createChatRoom(Long buildingId, String consumerId) {
        ChatRoom existingChatRoom = chatMapper.existsChatRoom(buildingId, consumerId);

        Building building = buildingMapper.getBuildingById(buildingId);

        if (building == null) {
            log.info("building이 null");
            throw new IllegalArgumentException("존재하지 않는 매물입니다.");
        }
        log.info("building 조회 성공: {}", building);

        // 구매자, 판매자 닉네임 조회
        String consumerNickname = memberMapper.getNicknameByMemberId(consumerId);
        String sellerNickname = memberMapper.getNicknameByMemberId(building.getMemberId());
        log.info("ChatServiceImpl - createChatRoom: 구매자닉네임: " + consumerNickname + ", 판매자: " + sellerNickname);

        if(consumerId.equals(building.getMemberId())) {
            throw new IllegalArgumentException("본인 소유의 건물입니다.");
        }

        //채팅방 이미 존재하는지 확인
        if (existingChatRoom != null) {
            log.info("ChatServiceImpl - createChatRoom: 이미 존재: " + existingChatRoom.getChatRoomId());
            ChatRoom existingChatRoomDetail = chatMapper.selectChatRoomById(existingChatRoom.getChatRoomId());
            return existingChatRoomDetail;
        }

        // 모든 정보가 확인되었을 때만 채팅방 생성
        if (consumerNickname != null && sellerNickname != null) {
            String uuid = UUID.randomUUID().toString();
            ChatRoom newChatRoom = ChatRoom.builder()
                    .chatRoomId(uuid)
                    .buildingId(buildingId)
                    .buildingName(building.getBuildingName())
                    .consumerId(consumerId)
                    .complexId(building.getComplexId())
                    .sellerNickname(sellerNickname)
                    .consumerNickname(consumerNickname)
                    .sellerId(building.getMemberId())
                    .sellerVisible(true)    // 초기값 true
                    .consumerVisible(true)  // 초기값 true
                    .sellerType(building.getSellerType())
                    .status(DealEnum.BEFORE_OWNER)
                    .build();

            chatMapper.insertChatRoom(newChatRoom);
            return newChatRoom;
        } else {
            throw new IllegalStateException("사용자 정보를 찾을 수 없어 채팅방을 생성할 수 없습니다.");
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
        boolean isSeller = userId.equals(chatRoom.getSellerId());
        boolean isBuyer = userId.equals(chatRoom.getConsumerId());

        boolean otherPartyAlreadyLeft = false;

        //나가려는 사용자의 채팅방 목록에서 해당 채팅방이 보이지 않도록 DB에서 변경
        if (isSeller) {
            chatMapper.updateSellerVisible(chatRoomId);
            // 내가 나가기 전, 상대방(구매자)이 이미 나가 있었는지 확인
            otherPartyAlreadyLeft = !chatRoom.getConsumerVisible();
        } else if (isBuyer) {
            chatMapper.updateConsumerVisible(chatRoomId);
            // 내가 나가기 전, 상대방(판매자)이 이미 나가 있었는지 확인
            otherPartyAlreadyLeft = !chatRoom.getSellerVisible();
        } else {
            throw new IllegalStateException("채팅방 참여자가 아닙니다.");
        }

        // 상대방이 이미 나간 상태에서 내가 나간 것이라면, 이제 둘 다 나갔으므로 채팅방을 완전히 삭제
        if (otherPartyAlreadyLeft) {
            //chatRoomId의 ChatMessage들 삭제
            chatMapper.deleteMessagesByRoomId(chatRoomId);
            //chatRoomId의 ChatRoom 삭제
            chatMapper.deleteChatRoom(chatRoomId);
        }
    }

    @Override
    public String getUserIdByNickname(String nickname) {
        String memberId = chatMapper.selectMemberIdByNickname(nickname);
        if (memberId == null) {
            throw new IllegalArgumentException(nickname + "을(를) 닉네임으로 하는 사용자를 찾지 못했습니다.");
        }

        return memberId;
    }

    @Override
    public String getUserIdByEmail(String email) {
        String memberId = chatMapper.selectMemberIdByEmail(email);
        if (memberId == null) {
            throw new IllegalArgumentException(email + "을(를) 이메일로 하는 사용자를 찾지 못했습니다.");
        }

        return memberId;
    }

    @Override
    public void markAsRead(String chatRoomId, String userId) {
        ChatRoom room = chatMapper.selectChatRoomById(chatRoomId);
        if (room == null) {
            throw new IllegalArgumentException(chatRoomId+ "을 id로 하는 채팅방을 찾지 못했습니다.");
        }

        if (!userId.equals(room.getSellerId()) && !userId.equals(room.getConsumerId())) {
            throw new IllegalStateException("채팅방 참여자가 아닙니다.");
        }

        log.info("ChatServiceImpl - markAsRead");
        // 현재 사용자가 보낸 메시지가 아닌 것만 읽음 처리
        chatMapper.markMessagesAsRead(chatRoomId, userId);
    }
}

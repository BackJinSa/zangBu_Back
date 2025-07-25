package bjs.zangbu.deal.dto.join;

import bjs.zangbu.deal.vo.DealEnum;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Deal, ChatRoom 조인 Dto
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DealWithChatRoom {

  private Long dealId;// Deal 식별 id
  private Long buildingId; // Build 식별 id
  private String chatRoomId; // ChatRoom 식별 id
  private DealEnum status; // 거래 상태
  private LocalDateTime createdAt; // 생성 날짜

  private int price; // 가격
  private String buildingName;// 주소
  private String houseType;// 부동산 타입
  private String saleType;// 매물 종류
  private String imageUrl; // 이미지
  private String address; // 주소

  private String consumerNickname; // 구매자 닉네임
  private String sellerNickname; // 판매자 닉네임
}


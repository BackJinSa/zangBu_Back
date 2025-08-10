package bjs.zangbu.deal.dto.join;

import bjs.zangbu.deal.vo.DealEnum;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Deal + ChatRoom 조인 DTO
 *
 * <p>거래 기본 정보, 매물 정보, 채팅방 정보, 참여자 닉네임을 함께 담는 DTO</p>
 *
 * <p>주로 거래 대기 목록 조회 API에서 사용되며, {@link bjs.zangbu.deal.vo.DealEnum}을 통해 거래 상태를 표현</p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DealWithChatRoom {

  /**
   * 거래 식별 ID
   */
  private Long dealId;

  /**
   * 매물(건물) 식별 ID
   */
  private Long buildingId;

  /**
   * 채팅방 식별 ID
   */
  private String chatRoomId;

  /**
   * 거래 상태 ({@link DealEnum})
   */
  private DealEnum status;

  /**
   * 거래 생성 일시
   */
  private LocalDateTime createdAt;

  /**
   * 매물 가격
   */
  private int price;

  /**
   * 매물 이름
   */
  private String buildingName;

  /**
   * 부동산 유형 (예: APARTMENT, OFFICETEL, VILLA, HOUSE)
   */
  private String propertyType;

  /**
   * 매물 거래 유형 (예: MONTHLY, CHARTER, TRADING)
   */
  private String saleType;

  /**
   * 매물 주소
   */
  private String address;

  /**
   * 구매자 닉네임
   */
  private String consumerNickname;

  /**
   * 판매자 닉네임
   */
  private String sellerNickname;
}


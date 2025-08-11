package bjs.zangbu.deal.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 정보를 나타내는 VO
 *
 * <p>거래 상태, 생성일, 연관된 채팅방/매물/유저/단지 등의 식별자를 포함</p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Deal {

  /**
   * 거래 식별 ID
   */
  private Long dealId;

  /**
   * 거래 상태 {@link DealEnum}
   */
  private DealEnum status;

  /**
   * 거래 생성 날짜
   */
  private LocalDateTime createdAt;

  // ==== foreign key ====

  /**
   * 채팅방 식별 ID
   */
  private String chatRoomId;

  /**
   * 매물 식별 ID
   */
  private Long buildingId;

  /**
   * 유저 식별 ID
   */
  private String memberId;

  /**
   * 단지 식별 ID
   */
  private Long complexId;
}

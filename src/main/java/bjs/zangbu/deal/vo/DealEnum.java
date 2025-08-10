package bjs.zangbu.deal.vo;

/**
 * 거래 상태를 나타내는 Enum
 *
 * <p>거래 진행 순서:</p>
 * <ol>
 *   <li>{@link #BEFORE_TRANSACTION} 거래 전</li>
 *   <li>{@link #BEFORE_OWNER} 판매자 수락 전</li>
 *   <li>{@link #BEFORE_CONSUMER} 구매자 수락 전</li>
 *   <li>{@link #MIDDLE_DEAL} 거래 중</li>
 *   <li>{@link #CLOSE_DEAL} 거래 성사</li>
 * </ol>
 *
 * <p>예시 진행 흐름:</p>
 * <pre>
 * BEFORE_TRANSACTION → BEFORE_OWNER → BEFORE_CONSUMER → MIDDLE_DEAL → CLOSE_DEAL
 * </pre>
 */
public enum DealEnum {

  /**
   * 거래 전 채팅이 시작되지 않았거나, 거래 요청이 이루어지지 않은 단계
   */
  BEFORE_TRANSACTION,

  /**
   * 판매자 수락 전 채팅이 시작되었으나, 판매자가 거래 요청을 수락하지 않은 단계
   */
  BEFORE_OWNER,

  /**
   * 구매자 수락 전 판매자가 거래 요청을 수락했으나, 구매자가 아직 확정하지 않은 단계
   */
  BEFORE_CONSUMER,

  /**
   * 거래 중 구매자와 판매자가 모두 거래를 수락하여 진행 중인 단계
   */
  MIDDLE_DEAL,

  /**
   * 거래 성사 거래가 완료되어 최종적으로 성사된 단계
   */
  CLOSE_DEAL
}

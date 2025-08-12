package bjs.zangbu.payment.repository;

import org.apache.ibatis.annotations.Param;
import java.util.Map;

public interface PaymentMapper {
    int upsertPaymentOnConfirm(Map<String, Object> params);
    int addPerCaseBalance(@Param("memberId") String memberId, @Param("delta") int delta);
    int upsertMembership(@Param("memberId") String memberId);
    Map<String, Object> selectEntitlements(@Param("memberId") String memberId);
    int consumePerCase(@Param("memberId") String memberId);
    int insertDownloadHistory(Map<String, Object> params);
}
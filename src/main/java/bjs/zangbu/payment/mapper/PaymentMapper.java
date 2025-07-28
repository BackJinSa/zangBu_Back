package bjs.zangbu.payment.mapper;

import bjs.zangbu.payment.dto.request.PaymentConfirmRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    int insertPayment(PaymentInsertParam param);
    int confirmPayment(PaymentConfirmRequest req);
}
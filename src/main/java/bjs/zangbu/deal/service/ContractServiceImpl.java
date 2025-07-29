package bjs.zangbu.deal.service;

import bjs.zangbu.codef.service.CodefService;
import bjs.zangbu.deal.dto.join.DealDocumentInfo;
import bjs.zangbu.deal.dto.join.DealWithSaleType;
import bjs.zangbu.deal.dto.request.BuildingRegisterRequest;
import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.mapper.DealMapper;
import bjs.zangbu.notification.vo.SaleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final DealMapper dealMapper;
    private final CodefService codefService;

    @Override
    @Transactional(readOnly = true)
    /** dealId → "/contracts/xxx.pdf" 상대 경로 반환 */
    public String getContractPdf(Long dealId) {
        DealWithSaleType deal = dealMapper.findWithType(dealId);

        if(deal == null) {
            throw new IllegalArgumentException("해당 거래가 존재하지 않습니다 dealId: "+ dealId);
        }

        String path;
        SaleType type = deal.getSaleType(); // 매매 전월세 타입 분류

        if(type == SaleType.TRADING){ // 매매
            path = "/contracts/sale_contract.pdf";
        } else if (type == SaleType.MONTHLY || type == SaleType.CHARTER) {
            // 전월세
            path = "/contracts/lease_contract.pdf";
        } else{
            throw new IllegalStateException("거래 타입 오류, saleType : " + type);
        }
        return path;
    }

    @Override
    public BuildingRegisterResponse generateRegisterPdf(Long dealId) throws Exception {
        // 1) DB 조회
        DealDocumentInfo deal = dealMapper.selectDocumentInfo(dealId);

        // 2) DTO -> Codef 요청
        BuildingRegisterRequest request = BuildingRegisterRequest.from(deal);

        String codefjson = codefService.callBuildingRegister(request);
    }
}

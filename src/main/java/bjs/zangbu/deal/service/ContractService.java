package bjs.zangbu.deal.service;

import bjs.zangbu.deal.dto.response.DealResponse;

public interface ContractService {
    public String getContractPdf(Long dealId);
    DealResponse.Download getEstateRegisternPdf(Long dealId) throws Exception;
    DealResponse.Download getBuildingRegisterPdf(Long dealId) throws Exception;
}

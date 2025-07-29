package bjs.zangbu.deal.service;

import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;

public interface ContractService {
    public String getContractPdf(Long dealId);
    BuildingRegisterResponse generateRegisterPdf(Long dealId) throws Exception;
}

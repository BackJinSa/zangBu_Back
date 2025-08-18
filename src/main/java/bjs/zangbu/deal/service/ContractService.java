package bjs.zangbu.deal.service;

import bjs.zangbu.deal.dto.response.DealResponse;

public interface ContractService {

  String getContractPdf(Long dealId);

  DealResponse.Download getEstateRegisterPdf(String memberId, Long buildingId) throws Exception;

  DealResponse.Download getBuildingRegisterPdf(String memberId, Long buildingId) throws Exception;
}

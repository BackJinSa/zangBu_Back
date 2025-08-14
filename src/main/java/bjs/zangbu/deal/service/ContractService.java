package bjs.zangbu.deal.service;

import bjs.zangbu.deal.dto.response.DealResponse;

public interface ContractService {

  public String getContractPdf(Long dealId);

  DealResponse.Download getEstateRegisternPdf(Long buildingId) throws Exception;

  DealResponse.Download getBuildingRegisterPdf(Long buildingId) throws Exception;
}

package bjs.zangbu.deal.service;

import bjs.zangbu.deal.dto.response.BuildingRegisterResponse;
import bjs.zangbu.deal.dto.response.EstateRegistrationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UnsupportedEncodingException;

public interface ContractService {
    public String getContractPdf(Long dealId);
    EstateRegistrationResponse getEstateRegistrationPdf(Long dealId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;
    BuildingRegisterResponse generateRegisterPdf(Long dealId) throws Exception;
}

package bjs.zangbu.codef.service;

import bjs.zangbu.building.dto.request.BuildingRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UnsupportedEncodingException;

public interface CodefService {

    String priceInformation(BuildingRequest.ViewDetailRequest request) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException;

    String residentRegistrationCertificate();
}

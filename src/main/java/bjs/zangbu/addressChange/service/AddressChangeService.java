package bjs.zangbu.addressChange.service;

import bjs.zangbu.addressChange.dto.response.ResRegisterCertResponse;

import java.util.List;

public interface AddressChangeService {
    public List<ResRegisterCertResponse> generateAddressChange(String memberId) throws Exception;
    /*임시 비활성화*/
    //public ResRegisterCertResponse generateAddressChange(Long memberId) throws UnsupportedEncodingException, JsonProcessingException, InterruptedException
}

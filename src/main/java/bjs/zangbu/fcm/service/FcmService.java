package bjs.zangbu.fcm.service;

import bjs.zangbu.fcm.dto.request.FcmRequest.*;
import bjs.zangbu.fcm.dto.request.FcmRequest.FcmRegisterRequest;
import bjs.zangbu.fcm.vo.Fcm;

public interface FcmService {

    // 디바이스 토큰 등록
    void registerToken(String memberId, FcmRegisterRequest request);

    // 현재 기기 디바이스 토큰만 삭제
    void deleteTokenByMemberIdAndToken(String memberId, FcmRemoveRequest request);

    // 디바이스 토큰 모두 삭제
    void deleteAllTokensByMemberId(String memberId);
}

package bjs.zangbu.fcm.service;

import bjs.zangbu.fcm.vo.Fcm;

public interface FcmService {

    // 디바이스 토큰 등록
    void registerToken(String memberId, String token);

    // 디바이스 토큰 모두 삭제
    void deleteAllTokensByMemberId(String memberId);
}

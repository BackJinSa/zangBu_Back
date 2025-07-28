package bjs.zangbu.fcm.service;

import bjs.zangbu.fcm.vo.Fcm;

public interface FcmService {

    void registerToken(String memberId, String token);
    void deleteAllTokensByMemberId(String memberId);
}

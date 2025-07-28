package bjs.zangbu.fcm.service;

import bjs.zangbu.fcm.mapper.FcmMapper;
import bjs.zangbu.fcm.vo.Fcm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    private final FcmMapper fcmMapper;

    @Override
    public void registerToken(String memberId, String token) {
        fcmMapper.insertFcmToken(memberId, token);
    }

    @Override
    public void deleteAllTokensByMemberId(String memberId) {
        fcmMapper.deleteAllTokensByMemberId(memberId);
    }
}

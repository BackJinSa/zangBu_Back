package bjs.zangbu.fcm.service;

import bjs.zangbu.fcm.dto.request.FcmRequest;
import bjs.zangbu.fcm.dto.request.FcmRequest.FcmRegisterRequest;
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

    // 디바이스 토큰 등록
    @Override
    public void registerToken(String memberId, FcmRegisterRequest request) {
        boolean exists = fcmMapper.existsByMemberIdAndToken(memberId, request.getToken());
        if (!exists) {
            fcmMapper.insertFcmToken(FcmRequest.tovo(memberId, request));
        }
    }

    // 디바이스 토큰 모두 삭제
    @Override
    public void deleteAllTokensByMemberId(String memberId) {
        fcmMapper.deleteAllTokensByMemberId(memberId);
    }
}

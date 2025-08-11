package bjs.zangbu.fcm.mapper;

import bjs.zangbu.fcm.vo.Fcm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FcmMapper {

    // 디바이스 토큰 중복 여부 확인
    boolean existsByMemberIdAndToken(@Param("memberId") String memberId, @Param("token") String token);

    // 디바이스 토큰 등록
    int insertFcmToken(Fcm fcm);

    // 유저 id로 디바이스 토큰 모두 삭제
    int deleteAllTokensByMemberId(String memberId);

    // 유저 id로 디바이스 토큰 모두 조회
    List<String> selectTokensByMemberId(String memberId);
}

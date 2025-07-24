package bjs.zangbu.security.account.mapper;

import bjs.zangbu.security.account.vo.Member;

public interface UserDetailsMapper {
    // 로그인 인증용 조회 전용 mapper
    Member get(String email);
}

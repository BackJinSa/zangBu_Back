package bjs.zangbu.security.account.mapper;

import bjs.zangbu.security.account.vo.Member;

public interface UserDetailsMapper {
    // 로그인 인증용 조회 전용 mapper.
    // Spring Security가 로그인 시 사용자의 권한/비밀번호를 검증하기 위해 호출
    // CustomUserDetailsService 같은 곳에서 이 매퍼가 호출
    Member get(String email);
}

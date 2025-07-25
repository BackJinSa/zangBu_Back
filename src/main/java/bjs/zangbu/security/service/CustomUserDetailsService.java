package bjs.zangbu.security.service;

import bjs.zangbu.security.account.mapper.UserDetailsMapper;
import bjs.zangbu.security.account.vo.CustomUser;
import bjs.zangbu.security.account.vo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    //UserDetailsService를 구현

    private final UserDetailsMapper userDetailsMapper;

    //로그인 시 security가 loadUserByUsername() 자동 호출

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //email로 사용자 정보 조회
        Member member = userDetailsMapper.get(email);
        //조회된 member 없으면 예외 발생
        if (member == null) {
            throw new UsernameNotFoundException(email + "에 해당하는 사용자를 찾을 수 없습니다");
        }
        //조회된 member를 customUser 객체로 감싸서 반환
        return new CustomUser(member);
    }
}

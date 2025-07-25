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
        Member member = userDetailsMapper.get(email);
        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        }
        return new CustomUser(member);
    }
}

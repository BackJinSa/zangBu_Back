package bjs.zangbu.security.service;

import bjs.zangbu.security.account.mapper.UserDetailsMapper;
import org.springframework.security.core.userdetails.UserDetailsService;

public class CustomUserDetailsService implements UserDetailsService {

    private final UserDetailsMapper userDetailsMapper;

}

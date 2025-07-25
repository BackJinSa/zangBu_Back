package bjs.zangbu.security.account.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Member {
    //유저 식별 id
    private String memberId;

    //아이디(이메일)
    private String email;

    //비밀번호
    private String password;

    //전화번호
    private String phone;

    //닉네임
    private String nickname;

    //주민번호
    private String identity;

    //역할(관리자, 사용자)
    private MemberEnum role;

    //생년월일 6자리
    private String birth;

    //사용자 이름
    private String name;

    //security에서 로그인한 사용자 권한 표현
    // -> role 값 기준으로 권한(ROLE_USER, ROLE_ADMIN) 넘겨줌
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

}//MemberVO

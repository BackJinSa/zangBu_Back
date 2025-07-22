package bjs.zangbu.security.account.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {
    //유저 식별 id
    private String userId;

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
    private UserEnum role;

    //생년월일 6자리
    private String birth;

    //사용자 이름
    private String userName;
}

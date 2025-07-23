package bjs.zangbu.security.account.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;


@Getter
@Setter
public class CustomUser extends User {
    //security 내에서 회원 정보 담을 객체는 User
    //회원 정보 담긴 Member -> User 객체에 매핑 해주기
    private Member member;

    public CustomUser(Member member) {
        super(member.getEmail(), member.getPassword(), member.getAuthorities());
        this.member = member;
    }

}

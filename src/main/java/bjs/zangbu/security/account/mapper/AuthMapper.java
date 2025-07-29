package bjs.zangbu.security.account.mapper;

import bjs.zangbu.security.account.vo.Member;

public interface AuthMapper {

    //1. 로그인 - 이메일로 사용자 조회하기
    Member findByEmail(String email);

    //2. 아이디(이메일) 찾기 - 이름, 전화번호로 아이디 찾기
    String findEmailByNameAndPhone(String name, String phone);

    //3. 회원가입
    //3-1. 이메일 중복 체크
    int countByEmail(String email);
    //3-2. 닉네임 중복 체크
    int countByNickname(String nickname);
    //3-3. 회원 db에 추가
    int insertMember(Member member);

    //4. 비밀번호 재설정 - 로그인 전이므로 email로
    int updatePassword(String email, String newPassword);
}

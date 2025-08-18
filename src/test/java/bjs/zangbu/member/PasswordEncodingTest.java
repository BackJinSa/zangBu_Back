package bjs.zangbu.member;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncodingTest {

  @Test
  void encodePassword() {
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    String raw = "test1234";   // 평문 비밀번호
    String encoded = encoder.encode(raw);

    System.out.println("평문: " + raw);
    System.out.println("해시: " + encoded);

    // 검증
    boolean matches = encoder.matches(raw, encoded);
    System.out.println("매칭 결과: " + matches);
  }
}

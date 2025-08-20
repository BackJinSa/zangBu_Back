package bjs.zangbu.chat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

public class passwordTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void createPassword() {
        String rawPassword = "jessica";  // 사용자가 입력한 원문 비밀번호
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("DB에 저장할 비밀번호 해시: " + encodedPassword);
        // 예: $2a$10$z9RJ6hCkIrU8ZbTjEcKMFeS/csbnA4bn.6XM...
    }

    @Test
    void passwordEncodeAndMatchTest() {
        String encodedPassword = "$2a$10$IiLExW2yb5sIRsVfgpZSwuKJkFCV2RaLqAsRWDFj2.3lMQQ8MwbtS";
        // 2. 로그인 시: 올바른 비밀번호 입력 -> true
        assertThat(passwordEncoder.matches("jessica", encodedPassword)).isTrue();
        System.out.println("matches(\"jessica\") = true");

        // 3. 로그인 시: 잘못된 비밀번호 입력 -> false
        assertThat(passwordEncoder.matches("jessicaa", encodedPassword)).isFalse();
        System.out.println("matches(\"jessicaa\") = false");
    }
}

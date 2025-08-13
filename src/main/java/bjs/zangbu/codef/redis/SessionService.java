package bjs.zangbu.codef.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

import java.util.UUID;

@Service
public class SessionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long SESSION_TTL = 60 * 30; // 30분

    public SessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 세션 키 발급
    public String createSession(Long userId) {
        String sessionKey = UUID.randomUUID().toString(); // 고유 키 발급
        String redisKey = "session:" + sessionKey;

        // Redis에 사용자 정보 저장
        redisTemplate.opsForValue().set(redisKey,
                userId,
                SESSION_TTL,
                TimeUnit.SECONDS);

        return sessionKey;
    }

    // 세션 조회
    public Long getUserIdFromSession(String sessionKey) {
        String redisKey = "session:" + sessionKey;
        Object value = redisTemplate.opsForValue().get(redisKey);
        return value != null ? (Long) value : null;
    }

    // 세션 삭제
    public void deleteSession(String sessionKey) {
        String redisKey = "session:" + sessionKey;
        redisTemplate.delete(redisKey);
    }
}
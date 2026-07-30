package org.example.tasktrackerbot.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


/**
 * Класс для сохранения JWT токенов в бд Redis
 */
@Service
@Slf4j
public class TokenHandlerService {

    private final StringRedisTemplate redisTemplate;
    private static final long DEFAULT_TTL_SECONDS = 1800; // 30 minutes

    public TokenHandlerService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String chatId, String token) {
        String key = buildKey(chatId);
        redisTemplate.opsForValue().set(key, token, Duration.ofSeconds(DEFAULT_TTL_SECONDS));
        log.info("Сохранён JWT токен в redis для chatId: {}", chatId);
    }

    public String getToken(String chatId) {
        return redisTemplate.opsForValue().get(buildKey(chatId));
    }

    public boolean hasToken(String chatId) {
        Boolean bool = redisTemplate.hasKey(buildKey(chatId));
        //noinspection PointlessBooleanExpression
        return Boolean.TRUE.equals(bool);
    }

    public void deleteToken(String chatId) {
        redisTemplate.delete(buildKey(chatId));
    }

    private String buildKey(String chatId) {
        return "chat_id: " + chatId.trim();
    }

}

package org.example.tasktrackerbot.session;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserStateService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final Duration STATE_TTL  = Duration.ofSeconds(900); // 15 minutes
    private static final Duration MENU_TTL  = Duration.ofDays(30);

    public UserStateService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String buildStateKey(String chatId) {
        return "user:state:" + chatId;
    }

    public String buildTempKey(String chatId) {
        return "user:tmp:" + chatId;
    }

    public String buildMenuKey(String chatId) {
        return "user:menu:" + chatId;
    }

    public UserState getState(String chatId) {
        String state = stringRedisTemplate.opsForValue().get(buildStateKey(chatId));
        if (state != null && !state.isEmpty()) {
            return UserState.valueOf(state);
        }
        return UserState.NONE;
    }


    public void setMenuId(String chatId, String menuId) {
        stringRedisTemplate.opsForValue().set(buildMenuKey(chatId), menuId, MENU_TTL);
    }


    public void setState(String chatId, UserState state) {
        stringRedisTemplate.opsForValue().set(buildStateKey(chatId), state.toString(), STATE_TTL);
    }

    public void setTemp(String chatId, String field, String value) {
        stringRedisTemplate.opsForHash().put(buildTempKey(chatId), field, value);
        stringRedisTemplate.expire(buildTempKey(chatId), STATE_TTL);
    }

    public String getMenuId(String chatId) {
        return stringRedisTemplate.opsForValue().get(buildMenuKey(chatId));
    }

    public String getTempField(String chatId, String field) {
        Object objValue = stringRedisTemplate.opsForHash().get(buildTempKey(chatId), field);
        if (objValue == null) {
            return null;
        }
        return objValue.toString();
    }

    public Map<Object, Object> getAllTempFields(String chatId) {
        return stringRedisTemplate.opsForHash().entries(buildTempKey(chatId));

    }

    public void clearState(String chatId) {
        stringRedisTemplate.delete(buildStateKey(chatId));
    }

    public void clearTemp(String chatId) {
        stringRedisTemplate.delete(buildTempKey(chatId));
    }


}

package team.seventhmile.tripforp.domain.refresh.service;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisRefreshService implements RefreshService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisRefreshService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveRefreshToken(String username, String refreshToken, long expiration) {
        redisTemplate.opsForValue().set(username, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getRefreshToken(String username) {
        Object token = redisTemplate.opsForValue().get(username);
        return (token != null) ? token.toString() : null;
    }

    @Override
    public void deleteRefreshToken(String username) {
        redisTemplate.delete(username);
    }

    @Override
    public boolean isKeyExists(String username) {
        Boolean hasKey = redisTemplate.hasKey(username);
        return hasKey != null && hasKey;
    }
}

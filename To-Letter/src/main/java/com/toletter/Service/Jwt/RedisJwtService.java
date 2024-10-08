package com.toletter.Service.Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisJwtService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> redisBlackListTemplate;

    // refreshToken, email 설정
    public void setValues(String email, String accessToken, String refreshToken){
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(tokenMap.getClass()));
        operations.set(email, tokenMap, Duration.ofMillis(86400000)); // 만료 1일
    }

    // 키 값으로 값이 존재하는지 확인
    public boolean isValid(String email){
        return redisTemplate.hasKey(email);
    }

    // 삭제
    public void deleteValues(String email){
        redisTemplate.delete(email);
    }

    public void setBlackList(String accessToken, String refreshToken, Long expiration) {
        redisBlackListTemplate.opsForValue().set(accessToken, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    public boolean isValidBlackList(String accessToken) {
        return redisBlackListTemplate.hasKey(accessToken);
    }
}

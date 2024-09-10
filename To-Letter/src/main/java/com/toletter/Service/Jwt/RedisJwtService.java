package com.toletter.Service.Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisJwtService {
    private final RedisTemplate<String, String> redisTemplate;

    // refreshToken, email 설정
    public void setValues(String email, String token){
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(email, token, Duration.ofMillis(86400000)); // 만료 1일
    }
    
    // 키 값으로 값 가져오기
    public String getValues(String email){
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String token = operations.get(email);
        if (token != null && !token.isEmpty()) {
            return token;
        }
        return null;
    }

    // 키 값으로 값이 존재하는지 확인
    public boolean isValid(String email){
        return redisTemplate.hasKey(email);
    }

    // 삭제
    public void deleteValues(String email){
        redisTemplate.delete(email);
    }
}

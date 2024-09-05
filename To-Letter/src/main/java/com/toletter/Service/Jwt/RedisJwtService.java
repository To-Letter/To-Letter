package com.toletter.Service.Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RedisJwtService {
    private final RedisTemplate<String, String> redisTemplate;

    // refreshToken, email 설정
    public void setValues(String email, String token){
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(email, token, Duration.ofMillis(1)); // 테스트 상 1분으로 했지만 그 후에는 7일로 할 예정
    }
    
    // 키 값으로 값 가져오기
    public String getValues(String email){
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String token = operations.get(email);
        if(token.isEmpty()){
            return null;
        }
        return token;
    }

    // 삭제
    public void deleteValues(String email){
        redisTemplate.delete(email);
    }
}

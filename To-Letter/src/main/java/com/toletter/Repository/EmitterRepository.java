package com.toletter.Repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String emitterId, Object event);

    Map<String, SseEmitter> findAllEmitterWithByMemberId(String nickname);

    void deleteById(String id);

    void deleteAllEmitterWithId(String nickname);

    void deleteAllEventCacheWithId(String nickname);

    void disconnect(String nickname);
}

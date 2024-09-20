package com.toletter.Service.Impl;

import com.toletter.Repository.EmitterRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(String nickname, SseEmitter emitter){
        emitters.put(nickname, emitter);
        return emitter;
    }

    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }


    public Map<String, SseEmitter> findAllEmitterWithByMemberId(String nickname) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().equals(nickname))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    public void deleteById(String id) {
        emitters.remove(id);
    }

    public void deleteAllEmitterWithId(String nickname) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.equals(nickname)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    public void deleteAllEventCacheWithId(String nickname) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.equals(nickname)) {
                        eventCache.remove(key);
                    }
                }
        );
    }

    public void disconnect(String nickname){
        emitters.forEach(
                (key, emitter) -> {
                    if(key.equals(nickname)) {
                        emitter.complete();
                    }
                }
        );
    }
}

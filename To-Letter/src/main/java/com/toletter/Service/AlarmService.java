package com.toletter.Service;

import com.toletter.Entity.Letter;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.Repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {
    private final EmitterRepository emitterRepository;

    private static final Long TIMEOUT = 10*60*1000L; // 10분

    // SSE 연결
    public SseEmitter connect(String loginNickname){
        this.delete(loginNickname); // 먼저 쌓여있는 알림 삭제
        SseEmitter emitter = emitterRepository.save(loginNickname, new SseEmitter(TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(loginNickname));
        emitter.onTimeout(() -> emitterRepository.deleteById(loginNickname));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, loginNickname, "connect. [loginNickname=" + loginNickname + "]");

        return emitter;
    }

    // 알림 보내기
    private void sendToClient(SseEmitter emitter, String loginNickname, Object data){
        try{
            emitter.send(SseEmitter.event().id(loginNickname).data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(loginNickname);
            throw new ErrorException("e : " + e , ErrorCode.NOT_FOUND_EXCEPTION);
        }
    }

    // 알림 보내려고 데이터 추가
    public void send(String toNickname, Letter letter){
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterWithByMemberId(toNickname);
        emitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, letter);
            sendToClient(emitter, key, letter);
        });
    }

    // 로그아웃 후 모든 알림을 삭제
    public void delete(String nickname){
        emitterRepository.deleteAllEmitterWithId(nickname);
        emitterRepository.deleteAllEventCacheWithId(nickname);
        emitterRepository.deleteById(nickname);
        emitterRepository.disconnect(nickname);
    }
}

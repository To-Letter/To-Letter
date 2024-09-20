package com.toletter.Service;

import com.toletter.Entity.Letter;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.Repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {
    private final EmitterRepository emitterRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final Long TIMEOUT = 10*60*1000L; // 10분

    // SSE 연결
    public SseEmitter connect(String loginNickname){
        SseEmitter emitter = emitterRepository.save(loginNickname, new SseEmitter(TIMEOUT));

        emitter.onCompletion(() -> {
            System.out.println("Emitter 요청이 안됨 : " + loginNickname);
            emitterRepository.deleteById(loginNickname);
        });
        emitter.onTimeout(() -> {
            System.out.println("Emitter 유효 시간이 만료된 이메일 : " + loginNickname);
            emitterRepository.deleteById(loginNickname);
        });

        try {
            // 최초 연결 시 메시지를 안 보내면 503 Service Unavailable 에러 발생
            emitter.send(SseEmitter.event().name("connect").data(loginNickname + " connected!"));
        } catch (IOException e) {
            throw new ErrorException("e : " + e, ErrorCode.NOT_FOUND_EXCEPTION);
        }
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
        emitterRepository.disconnect(nickname);
    }

    // 테스트 및 프론트 작업을 위해 1-5분으로 해놨지만 그 후에는 1-5일로 할 예정
    public void scheduleTask(String nickname, Letter letter, int time) {
        scheduler.schedule(() -> {
            try{
                System.out.println("새로운 알람이 왔어요!!!");
                this.send(nickname, letter);
            }catch (Exception e){
                throw new ErrorException("스케줄러 에러 :  " + e.getMessage(), ErrorCode.RUNTIME_EXCEPTION);
            }
        }, time, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

}

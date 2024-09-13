package com.toletter.Service;

import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.Repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {
    private final UserService userService;
    private final EmitterRepository emitterRepository;

    private static final Long TIMEOUT = 60*60*1000l; // 1시간
    public SseEmitter connect(HttpServletRequest request){
        String email = userService.findUserByToken(request).getEmail();
        SseEmitter emitter = emitterRepository.save(email, new SseEmitter(TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(email));
        emitter.onTimeout(() -> emitterRepository.deleteById(email));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, email, "EventStream Created. [userEmail=" + email + "]");

        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String email, Object data){
        try{
            emitter.send(SseEmitter.event().id(email).data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(email);
            throw new ErrorException("e : " + e , ErrorCode.NOT_FOUND_EXCEPTION);
        }
    }

    public void send(HttpServletRequest request, String content){
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(userService.findUserByToken(request).getEmail());
        emitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key, content);
            sendToClient(emitter, key, content);

        });
    }
}

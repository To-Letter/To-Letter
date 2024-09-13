package com.toletter.Controller;

import com.toletter.Service.AlarmService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/alarm")
public class AlarmController {
    private final AlarmService alarmService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(HttpServletRequest request){
        return ResponseEntity.ok(alarmService.connect(request));
    }

    @PostMapping(value = "/test")
    public void test(HttpServletRequest request, @Parameter String content){
        alarmService.send(request, content);
    }

}

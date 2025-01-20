package com.toletter.Controller;

import com.toletter.Service.AlarmService;
import com.toletter.Service.Jwt.CustomUserDetails;
import com.toletter.Entity.User;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/alarm")
public class AlarmController {
    private final AlarmService alarmService;

    // 메시지 알림
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "실시간 메일 알람 연결 성공"),
            @ApiResponse(code = 404, message = "실시간 메일 알람 연결 실패 / 에러 발생"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", dataType = "String", paramType = "header", example = "bearer token")
    })
    @ApiOperation(value = "실시간 알람 연결")
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user =  userDetails.getUser();

        return alarmService.connect(user.getNickname());
    }

}

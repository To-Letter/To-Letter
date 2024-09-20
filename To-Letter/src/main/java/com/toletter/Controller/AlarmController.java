package com.toletter.Controller;

import com.toletter.Service.AlarmService;
import com.toletter.Service.UserService;
import com.toletter.Entity.User;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/alarm")
public class AlarmController {
    private final AlarmService alarmService;
    private final UserService userService;

    // 메시지 알림
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "실시간 메일 알람 연결 성공"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "실시간 알람 연결")
    @GetMapping("/connect")
    public SseEmitter subscribe(HttpServletRequest request) {
        User user = userService.findUserByToken(request);

        return alarmService.connect(user.getNickname());
    }

}

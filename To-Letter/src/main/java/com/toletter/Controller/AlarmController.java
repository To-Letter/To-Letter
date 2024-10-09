package com.toletter.Controller;

import com.toletter.Service.AlarmService;
import com.toletter.Service.Jwt.CustomUserDetails;
import com.toletter.Entity.User;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
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
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "실시간 알람 연결")
    @GetMapping("/connect")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user =  userDetails.getUser();

        return alarmService.connect(user.getNickname());
    }

}

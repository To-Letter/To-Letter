package com.toletter.Controller;

import com.toletter.DTO.ResponseDTO;
import com.toletter.DTO.letter.Request.SendLetterRequest;
import com.toletter.Service.Jwt.CustomUserDetails;
import com.toletter.Service.LetterService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/letter")
public class LetterController {
    private final LetterService letterService;

    // 메일 보내기
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 보내기 성공"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "메일 보내기")
    @PostMapping("/send")
    public ResponseDTO sendLetter(@RequestBody SendLetterRequest sendLetterRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        letterService.sendLetter(sendLetterRequest, userDetails);
        return ResponseDTO.res(200, "메일 보내기 성공", "");
    }

    // 모든 메일함 열기
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 받기 성공"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "모든 메일 받기")
    @GetMapping("/receive")
    public ResponseDTO receivedLetter(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return letterService.receiveLetter(userDetails);
    }

    // 안 읽은 메일함 열기
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 받기 성공"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "안 읽은 메일함 열기")
    @GetMapping("/receive/unRead")
    public ResponseDTO receivedUnReadLetter(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return letterService.receivedUnReadLetter(userDetails);
    }

    // 읽은 메일함 열기
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 받기 성공"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "읽은 메일함 열기")
    @GetMapping("/receive/read")
    public ResponseDTO receivedReadLetter(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return letterService.receivedReadLetter(userDetails);
    }

    // 메일 열어서 확인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 열기 성공"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "메일 열기")
    @GetMapping("/open")
    public ResponseDTO openLetter (@RequestParam Long letterID) {
        return letterService.openLetter(letterID);
    }
}

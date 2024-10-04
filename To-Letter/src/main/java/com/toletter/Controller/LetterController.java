package com.toletter.Controller;

import com.toletter.DTO.letter.LetterDTO;
import com.toletter.DTO.letter.Request.SendLetterRequest;
import com.toletter.DTO.letter.Response.ReceivedLetterResponse;
import com.toletter.Service.LetterService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity<String> sendLetter(@RequestBody SendLetterRequest sendLetterRequest, HttpServletRequest httpServletRequest) {
        letterService.sendLetter(sendLetterRequest, httpServletRequest);
        return ResponseEntity.ok("메일 보내기 성공");
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
    public ReceivedLetterResponse receivedLetter(HttpServletRequest httpServletRequest) {
        return letterService.receiveLetter(httpServletRequest);
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
    public ReceivedLetterResponse receivedUnReadLetter(HttpServletRequest httpServletRequest) {
        return letterService.receivedUnReadLetter(httpServletRequest);
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
    public ReceivedLetterResponse receivedReadLetter(HttpServletRequest httpServletRequest) {
        return letterService.receivedReadLetter(httpServletRequest);
    }

    // 메일 열어서 확인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 열기 성공"),
            @ApiResponse(code = 401, message = "메일 열기 실패, 본인의 메일이 아님"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "메일 열기")
    @GetMapping("/open")
    public LetterDTO openLetter (@RequestParam Long letterID, HttpServletRequest httpServletRequest) {
        return letterService.openLetter(letterID, httpServletRequest);
    }

    // 메일 읽음 처리
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 읽음 처리 성공"),
            @ApiResponse(code = 401, message = "메일 읽음 처리 실패, 본인의 메일이 아님"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "메일 읽음 처리")
    @GetMapping("/viewCheckLetter")
    public ResponseEntity<String> viewCheckLetter (@RequestParam Long letterID, HttpServletRequest httpServletRequest) {
        return letterService.viewCheckLetter(letterID, httpServletRequest);
    }

    // 메일 삭제
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 삭제 성공"),
            @ApiResponse(code = 401, message = "메일 삭제 실패, 본인의 메일이 아님"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "메일 삭제")
    @DeleteMapping("/deleteLetter")
    public ResponseEntity<String> deleteLetter (@RequestParam Long letterID, HttpServletRequest httpServletRequest) {
        return letterService.deleteLetter(letterID, httpServletRequest);
    }
}

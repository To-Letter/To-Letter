package com.toletter.Controller;

import com.toletter.DTO.letter.LetterDTO;
import com.toletter.DTO.letter.Request.SendLetterRequest;
import com.toletter.DTO.letter.Response.ReceivedLetterResponse;
import com.toletter.DTO.letter.Response.SentLetterResponse;
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

    // 받은 모든 메일함 열기
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "받은 모든 메일함 열기 성공"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "받은 모든 메일함 열기")
    @GetMapping("/receive")
    public ReceivedLetterResponse viewReceivedBox(HttpServletRequest httpServletRequest) {
        return letterService.viewReceivedBox(httpServletRequest);
    }

    // 안 읽은 메일함 열기
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "안 읽은 메일함 열기 성공"),
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
            @ApiResponse(code = 200, message = "읽은 메일함 열기 성공"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "읽은 메일함 열기")
    @GetMapping("/receive/read")
    public ReceivedLetterResponse receivedReadLetter(HttpServletRequest httpServletRequest) {
        return letterService.receivedReadLetter(httpServletRequest);
    }

    // 받은 메일 열어서 확인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "받은 메일함 열기 성공"),
            @ApiResponse(code = 401, message = "받은 메일함 열기 실패, 본인의 메일이 아님"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "받은 메일함 열기")
    @GetMapping("/receive/open")
    public LetterDTO openReceivedLetter (@RequestParam Long letterID, HttpServletRequest httpServletRequest) {
        return letterService.openReceivedLetter(letterID, httpServletRequest);
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
    @GetMapping("/receive/viewCheckLetter")
    public ResponseEntity<String> viewCheckReceivedLetter (@RequestParam Long letterID, HttpServletRequest httpServletRequest) {
        return letterService.viewCheckReceivedLetter(letterID, httpServletRequest);
    }

    // 보낸 모든 메일함
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "보낸 모든 메일함 열기 성공")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "보낸 모든 메일함 열기")
    @GetMapping("/sent")
    public SentLetterResponse viewSentBox (HttpServletRequest httpServletRequest) {
        return letterService.viewSentBox (httpServletRequest);
    }

    // 보낸 메일 열어서 확인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "보낸 메일함 열기 성공"),
            @ApiResponse(code = 401, message = "보낸 메일함 열기 실패, 본인의 메일이 아님"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "보낸 메일함 열기")
    @GetMapping("/sent/open")
    public LetterDTO openSentLetter (@RequestParam Long letterID, HttpServletRequest httpServletRequest) {
        return letterService.openSentLetter(letterID, httpServletRequest);
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

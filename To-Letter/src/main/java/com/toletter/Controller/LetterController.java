package com.toletter.Controller;

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
    @ApiOperation(value = "메일 보내기")
    @PostMapping("/send")
    public ResponseEntity<String> sendLetter(@RequestBody SendLetterRequest sendLetterRequest, HttpServletRequest httpServletRequest) {
        letterService.sendLetter(sendLetterRequest, httpServletRequest);
        return ResponseEntity.ok("메일 보내기 성공");
    }

    // 메일 받기
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "메일 받기 성공"),
    })
    @ApiOperation(value = "메일 받기")
    @GetMapping("/receive")
    public ReceivedLetterResponse receivedLetter(HttpServletRequest httpServletRequest) {
        return letterService.receiveLetter(httpServletRequest);
    }
}

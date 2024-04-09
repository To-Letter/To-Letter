package com.toletter.Controller;

import com.toletter.DTO.auth.Request.EmailVerifyRequest;
import com.toletter.DTO.auth.Response.EmailVerifyResponse;
import com.toletter.DTO.user.Request.*;
import com.toletter.DTO.user.Response.*;
import com.toletter.Service.EmailService;
import com.toletter.Service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    // 회원가입
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "회원가입 성공"),
            @ApiResponse(code = 401, message = "같은 아이디 존재")
    })
    @ApiOperation(value = "유저 회원가입", notes = "토큰 필요 없음")
    @PostMapping("/signup")
    public ResponseEntity<String> userSignUp(@RequestBody UserSignupRequest userSignupRequest, HttpServletResponse response) {
        userService.signup(userSignupRequest, response);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "아이디 존재안함"),
            @ApiResponse(code = 401, message = "비밀번호 틀림"),
            @ApiResponse(code = 403, message = "2차 인증 안됨")
    })
    @ApiOperation(value = "유저 로그인", notes = "토큰 필요 없음")
    @PostMapping("/login")
    public UserLoginResponse userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        return userService.login(userLoginRequest, response);
    }

    // 마이페이지
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "유저 정보 전달 성공"),
    })
    @ApiOperation(value = "유저 정보 보여주기")
    @GetMapping("/mypage")
    public UserViewResponse viewUser(HttpServletRequest httpServletRequest) {
        return userService.viewUser(httpServletRequest);
    }

    // 마이페이지 수정
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "유저 정보 수정 성공"),
    })
    @ApiOperation(value = "유저 정보 수정")
    @PutMapping("/update")
    public UserUpdateResponse updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest httpServletRequest) {
        return userService.updateUser(userUpdateRequest, httpServletRequest);
    }

    // 로그아웃
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "로그아웃 성공"),
    })
    @ApiOperation(value = "로그아웃")
    @GetMapping("/logout")
    public ResponseEntity<String> updateUser(HttpServletRequest httpServletRequest) {
        System.out.println(httpServletRequest);
        userService.logout(httpServletRequest);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 2차 인증
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "2차 인증 메일 전송 성공"),
    })
    @ApiOperation(value = "2차 인증")
    @PostMapping ("/email-auth")
    public ResponseEntity<String> emailAuth(@RequestParam String toEmail) throws Exception {
        emailService.sendEmail(toEmail);
        return ResponseEntity.ok("이메일 전송 성공");
    }

    // 이메일 인증 검증
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "이메일 인증 성공"),
            @ApiResponse(code = 401, message = "이메일 인증 실패 / 시간 초과 / 다시 보냄"),
            @ApiResponse(code = 403, message = "이메일 인증 실패 / 랜덤 코드 불일치")
    })
    @ApiOperation(value = "2차 인증 검증")
    @PostMapping("/email-verify")
    public EmailVerifyResponse emailVerify(@RequestBody EmailVerifyRequest emailVerifyRequest) throws Exception {
        return emailService.verifyEmail(emailVerifyRequest);
    }
}

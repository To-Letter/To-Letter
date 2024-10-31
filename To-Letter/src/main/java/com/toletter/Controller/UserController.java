package com.toletter.Controller;

import com.toletter.DTO.ResponseDTO;
import com.toletter.DTO.auth.Request.EmailVerifyRequest;
import com.toletter.DTO.user.Request.*;
import com.toletter.Service.EmailService;
import com.toletter.Service.Jwt.CustomUserDetails;
import com.toletter.Service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    // 이메일 중복 확인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "이메일 중복 없음"),
            @ApiResponse(code = 401, message = "같은 이메일 존재")
    })
    @ApiOperation(value = "이메일 중복 확인", notes = "토큰 필요 없음")
    @GetMapping("/su/confirmEmail")
    public ResponseDTO confirmEmail(@RequestParam String userEmail) {
        return userService.confirmEmail(userEmail);
    }

    // 닉네임 중복 확인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "닉네임 중복 없음"),
            @ApiResponse(code = 401, message = "같은 닉네임 존재")
    })
    @ApiOperation(value = "닉네임 중복 확인", notes = "토큰 필요 없음")
    @GetMapping("/su/confirmNickname")
    public ResponseDTO confirmNickname(@RequestParam String userNickname) {
        return userService.confirmNickname(userNickname);
    }

    // 회원가입
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "회원가입 성공"),
            @ApiResponse(code = 401, message = "회원가입 실패 / 같은 이메일, 닉네임 존재")
    })
    @ApiOperation(value = "유저 회원가입", notes = "토큰 필요 없음")
    @PostMapping("/su/signup")
    public ResponseDTO userSignUp(@RequestBody UserSignupRequest userSignupRequest) {
        return userService.signup(userSignupRequest);
    }

    // 로그인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "로그인 실패 / 이메일 없음"),
            @ApiResponse(code = 401, message = "로그인 실패 / 비밀번호 틀림"),
            @ApiResponse(code = 403, message = "로그인 실패 / 2차 인증 안됨")
    })
    @ApiOperation(value = "유저 로그인", notes = "토큰 필요 없음")
    @PostMapping("/su/login")
    public ResponseDTO userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        return userService.login(userLoginRequest, response);
    }

    // 비밀번호 변경을 위한 이메일 전송
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "비밀번호 변경 이메일 전송 성공"),
            @ApiResponse(code = 201, message = "비밀번호 변경 이메일 전송 실패 / 시간 초과, 인증 이메일 다시 보냄"),
            @ApiResponse(code = 401, message = "비밀번호 변경 이메일 전송 실패 / 유저 없음(혹은 2차인증이 되지 않은 유저임)"),
            @ApiResponse(code = 403, message = "비밀번호 변경 이메일 전송 실패 / 이미 인증 이메일을 보냄")
    })
    @ApiOperation(value = "비밀번호 변경을 위한 이메일 전송", notes = "토큰 필요 없음")
    @GetMapping("/find/sendEmail")
    public ResponseDTO emailPassword(@RequestParam String email) throws MessagingException {
        return emailService.emailPassword(email);
    }

    // 비밀번호 변경(로그인X)
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "비밀번호 변경 성공"),
            @ApiResponse(code = 401, message = "비밀번호 변경 실패 / 유저가 없음(이메일이 없음)"),
    })
    @ApiOperation(value = "비밀번호 변경", notes = "토큰 필요 없음")
    @PatchMapping("/find/updatePW")
    public ResponseDTO findUpdatePW(@RequestBody UserFindUpdatePWRequest userFindUpdatePWRequest) {
        return userService.findUpdatePW(userFindUpdatePWRequest);
    }

    // 비밀번호 변경(로그인O)
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "비밀번호 변경 성공"),
            @ApiResponse(code = 401, message = "비밀번호 변경 실패 / 현재 비밀번호 틀림"),
            @ApiResponse(code = 1001, message = "유효하지 않은 토큰"),
            @ApiResponse(code = 1002, message = "빈 문자열 토큰"),
            @ApiResponse(code = 1003, message = "만료된 토큰"),
            @ApiResponse(code = 1004, message = "변조된 토큰"),
            @ApiResponse(code = 1005, message = "잘못된 접근")
    })
    @ApiOperation(value = "비밀번호 변경")
    @PatchMapping("/updatePW")
    public ResponseDTO updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserUpdatePWRequest userUpdatePWRequest) {
        return userService.updatePassword(userDetails, userUpdatePWRequest);
    }

    // 마이페이지
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "유저 정보 전달 성공"),
            @ApiResponse(code = 1001, message = "유효하지 않은 토큰"),
            @ApiResponse(code = 1002, message = "빈 문자열 토큰"),
            @ApiResponse(code = 1003, message = "만료된 토큰"),
            @ApiResponse(code = 1004, message = "변조된 토큰"),
            @ApiResponse(code = 1005, message = "잘못된 접근")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "유저 정보 보여주기")
    @GetMapping("/mypage")
    public ResponseDTO viewUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.viewUser(userDetails);
    }

    // 마이페이지 수정
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "유저 정보 수정 성공"),
            @ApiResponse(code = 1001, message = "유효하지 않은 토큰"),
            @ApiResponse(code = 1002, message = "빈 문자열 토큰"),
            @ApiResponse(code = 1003, message = "만료된 토큰"),
            @ApiResponse(code = 1004, message = "변조된 토큰"),
            @ApiResponse(code = 1005, message = "잘못된 접근")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "유저 정보 수정")
    @PatchMapping("/update")
    public ResponseDTO updateUser(@RequestBody UserUpdateRequest userUpdateRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.updateUser(userUpdateRequest, userDetails);
    }

    // 로그아웃
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "로그아웃 성공"),
            @ApiResponse(code = 1001, message = "유효하지 않은 토큰"),
            @ApiResponse(code = 1002, message = "빈 문자열 토큰"),
            @ApiResponse(code = 1003, message = "만료된 토큰"),
            @ApiResponse(code = 1004, message = "변조된 토큰"),
            @ApiResponse(code = 1005, message = "잘못된 접근")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "로그아웃")
    @GetMapping("/logout")
    public ResponseDTO updateUser(HttpServletRequest httpServletRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.logout(httpServletRequest, userDetails);
    }

    // 2차 인증
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "2차 인증 이메일 전송 성공"),
            @ApiResponse(code = 201, message = "2차 인증 이메일 전송 실패 / 시간 초과하여 2차 인증 메일 다시 보냄"),
            @ApiResponse(code = 401, message = "2차 인증 이메일 전송 실패 / 이미 메일을 보냄"),
            @ApiResponse(code = 403, message = "2차 인증 이메일 전송 실패 / 2차 인증 완료한 유저")
    })
    @ApiOperation(value = "2차 인증", notes = "토큰 필요 없음")
    @GetMapping ("/email/auth")
    public ResponseDTO emailAuth(@RequestParam String toEmail) throws Exception {
        return emailService.sendEmail(toEmail);
    }

    // 이메일 인증 검증
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "2차 인증 검증 성공"),
            @ApiResponse(code = 201, message = "비밀번호 인증 검증 성공"),
            @ApiResponse(code = 401, message = "이메일 인증 실패 / 시간 초과"),
            @ApiResponse(code = 403, message = "이메일 인증 실패 / 랜덤 코드 불일치"),
            @ApiResponse(code = 404, message = "이메일 인증 실패 / 메일 없음")
    })
    @ApiOperation(value = "2차 인증 검증", notes = "토큰 필요 없음")
    @PostMapping("/email/verify")
    public ResponseDTO emailVerify(@RequestBody EmailVerifyRequest emailVerifyRequest) {
        return emailService.verifyEmail(emailVerifyRequest);
    }

    // 유저 탈퇴
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "탈퇴 성공"),
            @ApiResponse(code = 400, message = "탈퇴 실패 / 비밀번호 틀림"),
            @ApiResponse(code = 401, message = "탈퇴 실패 / 유저 이메일이 없음"),
            @ApiResponse(code = 1001, message = "유효하지 않은 토큰"),
            @ApiResponse(code = 1002, message = "빈 문자열 토큰"),
            @ApiResponse(code = 1003, message = "만료된 토큰"),
            @ApiResponse(code = 1004, message = "변조된 토큰"),
            @ApiResponse(code = 1005, message = "잘못된 접근")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "유저 탈퇴")
    @DeleteMapping("/delete")
    public ResponseDTO userDelete(HttpServletRequest httpServletRequest, @RequestBody UserDeleteRequest userDeleteRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.userDelete(httpServletRequest, userDeleteRequest, userDetails);
    }
}

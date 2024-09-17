package com.toletter.Controller;

import com.toletter.DTO.auth.Request.EmailVerifyRequest;
import com.toletter.DTO.auth.Response.EmailVerifyResponse;
import com.toletter.DTO.user.Request.*;
import com.toletter.DTO.user.Response.*;
import com.toletter.Service.EmailService;
import com.toletter.Service.KakaoService;
import com.toletter.Service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final KakaoService kakaoService;

    // 이메일 중복 확인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "이메일 중복 없음"),
            @ApiResponse(code = 401, message = "같은 이메일 존재")
    })
    @ApiOperation(value = "이메일 중복 확인", notes = "토큰 필요 없음")
    @PostMapping("/su/confirmEmail")
    public ResponseEntity<String> confirmEmail(@RequestParam String userEmail) {
        userService.confirmEmail(userEmail);
        return ResponseEntity.ok("이메일 중복 없음.");
    }

    // 닉네임 중복 확인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "닉네임 중복 없음"),
            @ApiResponse(code = 401, message = "같은 닉네임 존재")
    })
    @ApiOperation(value = "닉네임 중복 확인", notes = "토큰 필요 없음")
    @PostMapping("/su/confirmNickname")
    public ResponseEntity<String> confirmNickname(@RequestParam String userNickname) {
        userService.confirmNickname(userNickname);
        return ResponseEntity.ok("닉네임 중복 없음.");
    }

    // 토큰 재발급
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "토큰 재발급 완료"),
            @ApiResponse(code = 1001, message = "유효하지 않은 토큰"),
            @ApiResponse(code = 1002, message = "빈 문자열 토큰"),
            @ApiResponse(code = 1003, message = "만료된 토큰"),
            @ApiResponse(code = 1004, message = "변조된 토큰"),
            @ApiResponse(code = 1005, message = "잘못된 접근")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "Authorization/refreshToken", required = true, dataType = "HttpServletRequest", paramType = "body", example = "bearer token")
    })
    @ApiOperation(value = "토큰 재발급", notes = "accessToken 만료 시 refreshToken 검증 후 토큰 재발급")
    @GetMapping("/reissue")
    public ResponseEntity<String> reissueToken() {
        return ResponseEntity.ok("토큰 재발급이 완료되었습니다");
    }

    // 회원가입
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "회원가입 성공"),
            @ApiResponse(code = 401, message = "같은 이메일 존재")
    })
    @ApiOperation(value = "유저 회원가입", notes = "토큰 필요 없음")
    @PostMapping("/su/signup")
    public ResponseEntity<String> userSignUp(@RequestBody UserSignupRequest userSignupRequest) {
        userService.signup(userSignupRequest);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 400, message = "이메일 존재안함"),
            @ApiResponse(code = 401, message = "비밀번호 틀림"),
            @ApiResponse(code = 403, message = "2차 인증 안됨")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", required = true, dataType = "HttpServletResponse", paramType = "body"),
    })
    @ApiOperation(value = "유저 로그인", notes = "토큰 필요 없음")
    @PostMapping("/su/login")
    public UserLoginResponse userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response) {
        return userService.login(userLoginRequest, response);
    }

    // 카카오 인증 코드 발급
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "인증코드 발급 성공"),
    })
    @ApiOperation(value = "카카오 인증 코드 발급", notes = "카카오 인증 코드 발급을 위한 URL 발급")
    @GetMapping("/kakao/auth")
    public String authKakao(){
        return kakaoService.getAuthCode();
    }

    // 카카오 로그인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "카카오 로그인 성공"),
            @ApiResponse(code = 401, message = "인증 실패함. / 토큰이 이상하거나 만료됨."),
            @ApiResponse(code = 404, message = "카카오 토큰이 발급이 안됨."),
    })
    @ApiOperation(value = "카카오 로그인")
    @PostMapping("/kakao/token")
    public Map tokenKaKao(@RequestParam String code) throws ParseException {
        Map token = kakaoService.getTokenUrl(code);
        Map userInfo = kakaoService.getUserInfo(token);
        return userInfo;
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
    public UserViewResponse viewUser(HttpServletRequest httpServletRequest) {
        return userService.viewUser(httpServletRequest);
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
    @PutMapping("/update")
    public UserUpdateResponse updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest httpServletRequest) {
        return userService.updateUser(userUpdateRequest, httpServletRequest);
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
    public ResponseEntity<String> updateUser(HttpServletRequest httpServletRequest) {
        userService.logout(httpServletRequest);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 2차 인증
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "2차 인증 메일 전송 성공"),
    })
    @ApiOperation(value = "2차 인증", notes = "토큰 필요 없음")
    @PostMapping ("/email/auth")
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
    @ApiOperation(value = "2차 인증 검증", notes = "토큰 필요 없음")
    @PostMapping("/email/verify")
    public EmailVerifyResponse emailVerify(@RequestBody EmailVerifyRequest emailVerifyRequest) {
        return emailService.verifyEmail(emailVerifyRequest);
    }

    // 유저 탈퇴
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "탈퇴 성공"),
            @ApiResponse(code = 401, message = "탈퇴 실패 / 비밀번호 틀림 / 유저 이메일이 없음"),
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
    public UserDeleteResponse userDelete(@RequestBody UserDeleteRequest userDeleteRequest, HttpServletRequest httpServletRequest) {
        return userService.userDelete(userDeleteRequest, httpServletRequest);
    }
}

package com.toletter.Controller;

import com.toletter.DTO.ResponseDTO;
import com.toletter.DTO.user.Request.UserKaKaoUpdateRequest;
import com.toletter.Service.Jwt.CustomUserDetails;
import com.toletter.Service.KakaoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/kakao")
public class KaKaoController {
    private final KakaoService kakaoService;

    // 카카오 인증 코드 발급
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "인증코드 발급 성공"),
    })
    @ApiOperation(value = "카카오 인증 코드 발급 / 회원가입을 위한", notes = "카카오 인증 코드 발급을 위한 URL 발급")
    @GetMapping("/su/auth")
    public ResponseDTO authKakao(){
        return kakaoService.getAuthCode();
    }

    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "인증코드 발급 성공"),
    })
    @ApiOperation(value = "카카오 인증 코드 발급 / 탈퇴를 위한", notes = "카카오 인증 코드 발급을 위한 URL 발급")
    @GetMapping("/su/auth/delete")
    public ResponseDTO authDeleteKakao(){
        return kakaoService.getDeleteAuthCode();
    }

    // 카카오 회원가입
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "카카오 회원가입 성공"),
            @ApiResponse(code = 401, message = "카카오 회원가입 실패/유저가 다름"),
            @ApiResponse(code = 403, message = "카카오 회원가입 실패/로컬 유저")
    })
    @ApiOperation(value = "카카오 유저 회원가입", notes = "토큰 필요 없음")
    @PostMapping("/su/signup")
    public ResponseDTO userKakaoSignUp(@RequestBody UserKaKaoUpdateRequest userKaKaoUpdateRequest) {
        return kakaoService.kakaoSignup(userKaKaoUpdateRequest);
    }

    // 카카오 로그인
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "카카오 회원가입 성공"),
            @ApiResponse(code = 201, message = "카카오 로그인 성공/이미 회원가입 된 유저라 바로 로그인 처리"),
            @ApiResponse(code = 400, message = "카카오 로그인 실패/ 2차 회원가입(닉네임, 주소)이 제대로 진행이 되지 않음."),
            @ApiResponse(code = 401, message = "인증 실패함. / 토큰이 이상하거나 만료됨."),
            @ApiResponse(code = 403, message = "카카오 회원가입 실패/동일한 이메일 존재"),
            @ApiResponse(code = 404, message = "카카오 토큰이 발급이 안됨."),
    })
    @ApiOperation(value = "카카오 로그인")
    @PostMapping("/su/token")
    public ResponseDTO tokenKaKao(@RequestParam String code, HttpServletResponse httpServletResponse) throws ParseException {
        Map token = kakaoService.getTokenUrl(code);
        return kakaoService.getUserInfo(token, httpServletResponse);
    }

    // 카카오 유저 탈퇴
    @ApiResponses( value ={
            @ApiResponse(code = 200, message = "카카오 유저 탈퇴 성공"),
            @ApiResponse(code = 401, message = "카카오 유저 탈퇴 실패"),
    })
    @ApiOperation(value = "카카오 유저 탈퇴")
    @DeleteMapping("/delete")
    public ResponseDTO tokenKaKao(HttpServletRequest httpServletRequest, @RequestParam String code, @AuthenticationPrincipal CustomUserDetails userDetails) throws ParseException {
        Map token = kakaoService.getTokenUrl(code);
        return kakaoService.userKaKaoDelete(httpServletRequest, token, userDetails);
    }
}

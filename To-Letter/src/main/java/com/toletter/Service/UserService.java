package com.toletter.Service;

import com.toletter.DTO.ResponseDTO;
import com.toletter.DTO.user.Request.*;
import com.toletter.DTO.user.Response.*;
import com.toletter.Entity.User;
import com.toletter.Enums.UserRole;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.JWT.*;
import com.toletter.Repository.*;
import com.toletter.Service.Jwt.CustomUserDetails;
import com.toletter.Service.Jwt.RedisJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.*;

@Service
@RequiredArgsConstructor // 초기화되지 않은 final 필드에 대해 생성자를 생성해줌.
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  final JwtTokenProvider jwtTokenProvider;
    private final RedisJwtService redisJwtService;
    private final AlarmService alarmService;

    // 아이디 중복 확인
    public ResponseDTO confirmEmail(String userEmail){
        if(userRepository.existsByEmail(userEmail)){
            return ResponseDTO.res(401, "이메일 중복", "");
        }
        return ResponseDTO.res(200, "이메일 중복 없음", "");
    }

    // 닉네임 중복 확인
    public ResponseDTO confirmNickname(String userNickname){
        if(userRepository.existsByNickname(userNickname)){
            return ResponseDTO.res(401, "닉네임 중복", "");
        }
        return ResponseDTO.res(200, "닉네임 중복 없음", "");
    }

    // 회원가입
    @Transactional
    public ResponseDTO signup(UserSignupRequest userSignupRequest){
        User user = userSignupRequest.toEntity();

        if(userRepository.existsByEmail(userSignupRequest.getEmail())){
            return ResponseDTO.res(401, "회원가입 실패 / 같은 이메일 존재", "");
        }

        if(userRepository.existsByNickname(userSignupRequest.getNickname())){
            return ResponseDTO.res(401, "회원가입 실패 / 같은 닉네임 존재", "");
        }

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(userSignupRequest.getPassword()));
        user.setSecondConfirmed(false);
        user.setChangePassWord(false);
        user.setUserRole(UserRole.User);
        userRepository.save(user);
        return ResponseDTO.res(200, "회원가입 성공", "");
    }

    // 로그인
    public ResponseDTO login(UserLoginRequest userLoginRequest, HttpServletResponse httpServletResponse){
        // 아이디가 존재하지 않으면
        if(!userRepository.existsByEmail(userLoginRequest.getEmail())){
            return ResponseDTO.res(400, "로그인 실패 / 이메일 없음", "");
        }
        User user = userRepository.findByEmail(userLoginRequest.getEmail()).orElseThrow();

        // 비밀번호 틀리면
        if(!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())){
            return ResponseDTO.res(401, "로그인 실패 / 비밀번호 틀림", "");
        }
        // 2차 인증 안하면
        if(!user.isSecondConfirmed()){
            return ResponseDTO.res(403, "로그인 실패 / 2차 인증 안됨", "");
        }
        this.setJwtTokenInHeader(userLoginRequest.getEmail(), user.getUserRole(), httpServletResponse);
        return ResponseDTO.res(200, "로그인 성공", "");
    }

    // 비밀번호 변경(로그인X)
    public ResponseDTO findUpdatePW(UserFindUpdatePWRequest userFindUpdatePWRequest){
        User user = userRepository.findByEmail(userFindUpdatePWRequest.getEmail()).orElseThrow(() ->
            new ErrorException("비밀번호 변경 실패 / 유저가 없음(이메일이 없음)", 200, ErrorCode.UNAUTHORIZED_EXCEPTION)
        );
        if(!user.isSecondConfirmed()){
            return ResponseDTO.res(403, "비밀번호 변경 실패 / 2차 인증이 되지 않음", "");
        }
        if(!user.isChangePassWord()){
            return ResponseDTO.res(404, "비밀번호 변경 실패 / 이메일로 검증 안됨", "");
        }
        if(passwordEncoder.matches(userFindUpdatePWRequest.getChangePassword(), user.getPassword())){
            return ResponseDTO.res(400, "비밀번호 변경 실패 / 원래 비밀번호와 같음", "");
        }
        // 비밀번호 암호화
        user.updatePassword(passwordEncoder.encode(userFindUpdatePWRequest.getChangePassword()));
        user.setChangePassWord(false);
        userRepository.save(user);
        return ResponseDTO.res(200, "비밀번호 변경 성공", "");
    }

    //비밀번호 변경(로그인O)
    public ResponseDTO updatePassword(CustomUserDetails userDetails, UserUpdatePWRequest userUpdatePWRequest){
        User user = userDetails.getUser();

        if(!passwordEncoder.matches(userUpdatePWRequest.getNowPassword(), user.getPassword())){
            return ResponseDTO.res(401, "비밀번호 변경 실패 / 현재 비밀번호 틀림", "");
        }

        user.updatePassword(passwordEncoder.encode(userUpdatePWRequest.getChangePassword()));
        userRepository.save(user);
        return ResponseDTO.res(200, "비밀번호 변경 성공", "");
    }

    // 유저 정보 보여주기
    public ResponseDTO viewUser(CustomUserDetails userDetails){
        User user =  userDetails.getUser();

        return  ResponseDTO.res(200, "유저 정보 보여주기 성공", UserViewResponse.res(user.getAddress(), user.getNickname(), user.getEmail(), user.getLoginType()));
    }

    // 유저 정보 수정
    @Transactional
    public ResponseDTO updateUser(UserUpdateRequest userUpdateRequest, CustomUserDetails userDetails){
        User user =  userDetails.getUser();
        user.updateUser(userUpdateRequest);
        userRepository.save(user);
        UserUpdateResponse userUpdateResponse = UserUpdateResponse.res(user.getEmail(), user.getNickname(), user.getAddress(), user.getLoginType());
        return ResponseDTO.res(200, "유저 정보 수정 성공", userUpdateResponse);
    }

    // 로그아웃
    public ResponseDTO logout(HttpServletRequest httpServletRequest, HttpServletResponse response, CustomUserDetails userDetails){
        User user =  userDetails.getUser();
        alarmService.delete(user.getNickname());
        redisJwtService.deleteValues(user.getEmail());
        jwtTokenProvider.expireToken(httpServletRequest, response);
        return ResponseDTO.res(200, "로그아웃 성공", "");
    }

    // 유저 탈퇴
    public ResponseDTO userDelete(HttpServletRequest httpServletRequest, HttpServletResponse response, UserDeleteRequest userDeleteRequest, CustomUserDetails userDetails){
        // 유저의 아이디가 존재하지 않으면
        if(!userRepository.existsByEmail(userDeleteRequest.getEmail())){
            return ResponseDTO.res(401, "유저 이메일이 없음.", "");
        }
        User user =  userDetails.getUser();
        if(!passwordEncoder.matches(userDeleteRequest.getPassword(), user.getPassword())){
            return ResponseDTO.res(400, "비밀번호가 틀림", "");
        }
        alarmService.delete(user.getNickname());
        redisJwtService.deleteValues(userDeleteRequest.getEmail());
        jwtTokenProvider.expireToken(httpServletRequest, response);
        userRepository.delete(user);
        return ResponseDTO.res(200, "탈퇴 성공", "");
    }

    // 토큰 헤더에 저장
    public void setJwtTokenInHeader(String email, UserRole userRole, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setCookieRefreshToken(response, refreshToken);
        redisJwtService.setValues(email, accessToken,refreshToken);
    }
}

package com.toletter.Service;

import com.toletter.DTO.ResponseDTO;
import com.toletter.DTO.user.Request.*;
import com.toletter.DTO.user.Response.*;
import com.toletter.Entity.User;
import com.toletter.Enums.LoginType;
import com.toletter.Enums.UserRole;
import com.toletter.Error.*;
import com.toletter.JWT.*;
import com.toletter.Repository.*;
import com.toletter.Service.Jwt.RedisJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 아이디 중복 확인
    public void confirmEmail(String userEmail){
        if(userRepository.existsByEmail(userEmail)){
            throw new ErrorException("같은 이메일이 존재합니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
    }

    // 닉네임 중복 확인
    public void confirmNickname(String userNickname){
        if(userRepository.existsByNickname(userNickname)){
            throw new ErrorException("같은 닉네임이 존재합니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
    }

    // 회원가입
    @Transactional
    public void signup(UserSignupRequest userSignupRequest){
        User user = userSignupRequest.toEntity();

        if(userRepository.existsByEmail(userSignupRequest.getEmail())){
            throw new ErrorException("같은 이메일이 존재합니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        if(userRepository.existsByNickname(userSignupRequest.getNickname())){
            throw new ErrorException("같은 닉네임이 존재합니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        // 카카오 로그인인지 로컬 로그인인지 구분
        if(userSignupRequest.getLoginType().equals(LoginType.kakaoLogin)){
            user.setSecondConfirmed(true);
        } else if (userSignupRequest.getLoginType().equals(LoginType.localLogin)) {
            // 비밀번호 암호화
            user.setPassword(passwordEncoder.encode(userSignupRequest.getPassword()));
            user.setSecondConfirmed(false);
        }
        user.setUserRole(UserRole.User);
        userRepository.save(user);
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

    // 유저 정보 보여주기
    public ResponseDTO viewUser(HttpServletRequest httpServletRequest){
        User user = this.findUserByToken(httpServletRequest);

        return  ResponseDTO.res(200, "유저 정보 보여주기 성공", UserViewResponse.res(user.getAddress(), user.getNickname(), user.getEmail()));
    }

    // 유저 정보 수정
    @Transactional
    public ResponseDTO updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest httpServletRequest){
        User user = this.findUserByToken(httpServletRequest);
        user.updateUser(userUpdateRequest);
        userRepository.save(user);
        UserUpdateResponse userUpdateResponse = UserUpdateResponse.res("200", "수정 완료",  user.getEmail(), user.getNickname(), user.getAddress());
        return ResponseDTO.res(200, "유저 정보 수정 성공", userUpdateResponse);
    }

    // 로그아웃
    public void logout(HttpServletRequest httpServletRequest){
        alarmService.delete(this.findUserByToken(httpServletRequest).getNickname());
        redisJwtService.deleteValues(findUserByToken(httpServletRequest).getEmail());
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(httpServletRequest));
    }

    // 유저 탈퇴
    public ResponseDTO userDelete(UserDeleteRequest userDeleteRequest, HttpServletRequest httpServletRequest){
        // 유저의 아이디가 존재하지 않으면
        if(!userRepository.existsByEmail(userDeleteRequest.getEmail())){
            return ResponseDTO.res(401, "유저 이메일이 없음.", "");
        }
        User user = userRepository.findByEmail(userDeleteRequest.getEmail()).orElseThrow();
        if(!passwordEncoder.matches(userDeleteRequest.getPassword(), user.getPassword())){
            return ResponseDTO.res(400, "비밀번호가 틀림", "");
        }
        alarmService.delete(user.getNickname());
        redisJwtService.deleteValues(userDeleteRequest.getEmail());
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(httpServletRequest));
        userRepository.delete(user);
        return ResponseDTO.res(200, "탈퇴 성공", "");
    }

    public void setFirstAuthentication(String id, String password) {
        // 1. id, password 기반 Authentication 객체 생성, 해당 객체는 인증 여부를 확인하는 authenticated 값이 false.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(id, password);

        // 2. 검증 진행 - CustomUserDetailsService.loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        System.out.println("authj : " + authentication.getName());
        System.out.println(authentication.getAuthorities());
    }

    // 토큰 헤더에 저장
    public void setJwtTokenInHeader(String email, UserRole userRole, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(email, userRole);
        String refreshToken = jwtTokenProvider.createRefreshToken(email, userRole);

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);
        redisJwtService.setValues(email, refreshToken);
    }

    // 토큰에서 유저 정보 가져오기
    public User findUserByToken(HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        return userRepository.findByEmail(email).orElseThrow();
    }

}

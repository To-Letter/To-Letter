package com.toletter.Service;

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

    // 토큰 재발급
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);
        String newRefreshToken = jwtTokenProvider.reissueRefreshToken(refreshToken);

        jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, newRefreshToken);
    }

    // 회원가입
    @Transactional
    public void signup(UserSignupRequest userSignupRequest){
        User user = userSignupRequest.toEntity();

        if(userRepository.existsByEmail(userSignupRequest.getEmail())){
            throw new ErrorException("같은 이메일이 존재합니다.", ErrorCode.UNAUTHORIZED_EXCEPTION);
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
    public UserLoginResponse login(UserLoginRequest userLoginRequest, HttpServletResponse httpServletResponse){
        // 아이디가 존재하지 않으면
        if(!userRepository.existsByEmail(userLoginRequest.getEmail())){
            return UserLoginResponse.res("400", "로그인 실패 / 이메일 없음");
        }
        User user = userRepository.findByEmail(userLoginRequest.getEmail()).orElseThrow();

        // 비밀번호 틀리면
        if(!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())){
            return UserLoginResponse.res("401", "로그인 실패 / 비밀번호 틀림");
        }
        // 2차 인증 안하면
        if(!user.isSecondConfirmed()){
            return UserLoginResponse.res("403", "로그인 실패 / 2차 인증 안됨");
        }
        this.setJwtTokenInHeader(userLoginRequest.getEmail(), user.getUserRole(), httpServletResponse);
        return UserLoginResponse.res("200", "로그인 성공");
    }

    // 유저 정보 보여주기
    public UserViewResponse viewUser(HttpServletRequest httpServletRequest){
        User user = this.findUserByToken(httpServletRequest);
        return  UserViewResponse.res(user.getAddress(), user.getNickname(), user.getEmail());
    }

    // 유저 정보 수정
    @Transactional
    public UserUpdateResponse updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest httpServletRequest){
        User user = this.findUserByToken(httpServletRequest);
        user.updateUser(userUpdateRequest);
        userRepository.save(user);
        return UserUpdateResponse.res("200", "수정 완료",  user.getEmail(), user.getNickname(), user.getAddress());
    }

    // 로그아웃
    public void logout(HttpServletRequest httpServletRequest){
        redisJwtService.deleteValues(findUserByToken(httpServletRequest).getEmail());
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(httpServletRequest));
    }

    // 유저 탈퇴
    public UserDeleteResponse userDelete(UserDeleteRequest userDeleteRequest, HttpServletRequest httpServletRequest){
        // 유저의 아이디가 존재하지 않으면
        if(!userRepository.existsByEmail(userDeleteRequest.getEmail())){
            return UserDeleteResponse.res("401", "유저 이메일이 없음.");
        }
        User user = userRepository.findByEmail(userDeleteRequest.getEmail()).orElseThrow();
        if(!passwordEncoder.matches(userDeleteRequest.getPassword(), user.getPassword())){
            return UserDeleteResponse.res("401", "비밀번호가 틀림");
        }
        redisJwtService.deleteValues(userDeleteRequest.getEmail());
        jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(httpServletRequest));
        userRepository.delete(user);
        return UserDeleteResponse.res("200", "탈퇴 성공");
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

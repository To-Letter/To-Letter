package com.toletter.Service;

import com.toletter.DTO.ResponseDTO;
import com.toletter.DTO.user.KaKaoDTO;
import com.toletter.DTO.user.Request.UserKaKaoSignupRequest;
import com.toletter.DTO.user.Request.UserKaKaoUpdateRequest;
import com.toletter.DTO.user.Response.UserKaKaoLoginResponse;
import com.toletter.Entity.User;
import com.toletter.Enums.LoginType;
import com.toletter.Enums.UserRole;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.JWT.JwtTokenProvider;
import com.toletter.Repository.UserRepository;
import com.toletter.Service.Jwt.RedisJwtService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {
    @Value("${kakao.apiKey}")
    String kakaoApiKey;

    @Value("${kakao.adminKey}")
    String kakaoAdminKey;

    @Value("${kakao.redirectUrl}")
    String redirectUrl;

    String authUrl = "https://kauth.kakao.com/oauth/authorize?";
    String tokenUrl = "https://kauth.kakao.com/oauth/token";
    String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
    String unLinkUrl = "https://kapi.kakao.com/v1/user/unlink";
    private final UserRepository userRepository;
    private final UserService userService;
    private  final JwtTokenProvider jwtTokenProvider;
    private final RedisJwtService redisJwtService;
    private final AlarmService alarmService;

    public ResponseDTO getAuthCode(){
        StringBuffer url = new StringBuffer();
        url.append(authUrl) .append("client_id="+kakaoApiKey).append("&redirect_uri="+redirectUrl).append("&response_type=code");
        return ResponseDTO.res(200, "url 전달 성공",url.toString());
    }

    // 토큰 발급하기
    public Map getTokenUrl(String code) throws ParseException {
        String access_Token = "";
        String refresh_Token = "";
        String refresh_token_expires_in = "";
        String expires_in = "";

        // http header 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //고정값
        params.add("client_id", kakaoApiKey);
        params.add("redirect_uri", redirectUrl);
        params.add("code", code);

        // header + body
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, httpHeaders);

        // 4. http 요청하기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        if(response.getStatusCode().equals(HttpStatus.OK)){
            //Response 데이터 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody());
            access_Token = String.valueOf(jsonObj.get("access_token"));
            expires_in = String.valueOf(jsonObj.get("expires_in"));
            refresh_Token = String.valueOf(jsonObj.get("refresh_token"));
            refresh_token_expires_in = String.valueOf(jsonObj.get("refresh_token_expires_in"));
        } else {
            throw new ErrorException( response.getStatusCode()+"카카오 토큰이 발급이 안됩니다.", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        Map<String, Object> tokenMap = new HashMap<String, Object>();
        tokenMap.put("access_Token", access_Token);
        tokenMap.put("expires_in", expires_in);
        tokenMap.put("refresh_Token", refresh_Token);
        tokenMap.put("refresh_token_expires_in", refresh_token_expires_in);

        return tokenMap;
    }

    // 회원가입 혹은 로그인 처리
    public UserKaKaoLoginResponse getUserInfo(Map token, HttpServletResponse httpServletResponse) throws ParseException {
        //access_token을 이용하여 사용자 정보 조회
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.get("access_Token").toString());

        HttpEntity request = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        KaKaoDTO kakaoUser;
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            Long userId = Long.parseLong(jsonObject.get("id").toString());
            JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");

            kakaoUser = new KaKaoDTO(kakaoAccount.get("email").toString(), userId);

            if(userRepository.existsByEmail(kakaoUser.getEmail())){
                User user = userRepository.findByEmail(kakaoUser.getEmail()).orElseThrow();

                // 만약, 이미 회원가입이 된 카카오톡 유저라면
                if(user.getLoginType().equals(LoginType.kakaoLogin)){
                    userService.setJwtTokenInHeader(user.getEmail(), user.getUserRole(), httpServletResponse);
                    return UserKaKaoLoginResponse.res("201", "로그인 성공", kakaoUser);
                } else if (user.getLoginType().equals(LoginType.localLogin)) {
                    throw new ErrorException("회원가입 실패, 동일한 이메일이 존재함. ", ErrorCode.FORBIDDEN_EXCEPTION);
                }
            }
        } else {
            throw new ErrorException( response.getStatusCode()+"인증에 실패하였습니다. 다시 확인해주세요.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        UserKaKaoSignupRequest userKaKaoSignupRequest = new UserKaKaoSignupRequest(kakaoUser.getEmail(), kakaoUser.getUserId(), LoginType.kakaoLogin, true, UserRole.User);

        User newUser = userKaKaoSignupRequest.toEntity();
        userRepository.save(newUser);

        return UserKaKaoLoginResponse.res("200", "회원가입 성공", kakaoUser);
    }

    public ResponseEntity<String> kakaoSignup(UserKaKaoUpdateRequest userKaKaoUpdateRequest){
        User user = userRepository.findByEmail(userKaKaoUpdateRequest.getEmail()).orElseThrow();

        if(!userKaKaoUpdateRequest.getEmail().equals(user.getEmail())){
            throw new ErrorException("카카오 회원가입 실패/유저가 다름", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        user.updateKakaoUser(userKaKaoUpdateRequest);
        userRepository.save(user);

        return ResponseEntity.ok("카카오 회원가입 성공");
    }

    public void userKaKaoDelete(Map token, HttpServletRequest httpServletRequest) throws ParseException {
        //access_token을 이용하여 사용자 정보 조회
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.get("access_Token").toString());

        HttpEntity request = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                unLinkUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        if(response.getStatusCode().equals(HttpStatus.OK)){
            User user = userService.findUserByToken(httpServletRequest);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            Long userId = Long.parseLong(jsonObject.get("id").toString());

            // 카카오 로그인인지와 탈퇴 유저가 맞는지 확인
            if(user.getLoginType().equals(LoginType.kakaoLogin) && user.getKakaoId().equals(userId)){
                alarmService.delete(user.getNickname());
                redisJwtService.deleteValues(user.getEmail());
                jwtTokenProvider.expireToken(jwtTokenProvider.resolveAccessToken(httpServletRequest));
                userRepository.delete(user);
            }

        } else {
            throw new ErrorException(response.getStatusCode()+"카카오 유저 탈퇴 실패", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

    }
}

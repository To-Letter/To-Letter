package com.toletter.Service;

import com.toletter.DTO.ResponseDTO;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
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

    @Value("${kakao.redirectUrl}")
    String redirectUrl;

    String authUrl = "https://kauth.kakao.com/oauth/authorize?";
    String tokenUrl = "https://kauth.kakao.com/oauth/token";
    String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

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

    public Map getUserInfo(Map token) throws ParseException {
        String email = "";
        String nickname = "";

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

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");
            JSONObject profile = (JSONObject)kakaoAccount.get("profile");
            email = kakaoAccount.get("email").toString();
            nickname = profile.get("nickname").toString();
        } else {
            throw new ErrorException( response.getStatusCode()+"인증에 실패하였습니다. 다시 확인해주세요.", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("email", email);
        userMap.put("nickname", nickname);
        userMap.put("id", email.split("@")[0]);

        return userMap;
    }

}

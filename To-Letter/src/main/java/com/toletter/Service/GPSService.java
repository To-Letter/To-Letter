package com.toletter.Service;

import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GPSService {
    // 거리 관련 기능

    @Value("${kakao.apiKey}")
    String kakaoApiKey;

    String getUrl = "https://dapi.kakao.com/v2/local/search/address.json";

    // 위도 경도 가져오기
    public String getGpsUrl(String add) throws ParseException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        getUrl = getUrl + "?query="+add;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                getUrl,
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        Map<String, String> gpsMap = new HashMap<String, String>();

        if(response.getStatusCode().equals(HttpStatus.OK)){
            JSONParser jsonParser = new JSONParser(); // JSON 형식의 문자열 파싱하여 JAVA객체로 변환
            JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody()); // JSON 객체로 변환
            JSONArray jsonArray = (JSONArray) jsonObj.get("documents");
            JSONObject position = (JSONObject)jsonArray.get(0);
            float lon = Float.parseFloat((String) position.get("x"));
            float lat = Float.parseFloat((String) position.get("y"));
            System.out.println("lon : " + lon);
            System.out.println("lat : " + lat);
        } else {
            throw new ErrorException( response.getStatusCode()+"카카오 정보가 주어지지 않습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
        }

        return "jsonString";
    }
}

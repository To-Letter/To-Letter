package com.toletter.Service;

import com.toletter.DTO.letter.GpsDTO;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GPSService { // 거리 관련 기능
    @Value("${kakao.apiKey}")
    private String kakaoApiKey;

    String getUrl = "https://dapi.kakao.com/v2/local/search/address.json";

    // 위도 경도 가져오기
    public Map getGpsUrl(String add) {
        // 정보 가져오기
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        String fullUrl = getUrl + "?query="+add;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        Map<String, Double> gpsMap = new HashMap<>();

        if(response.getStatusCode().equals(HttpStatus.OK)){
            try{
                JSONParser jsonParser = new JSONParser(); // JSON 형식의 문자열 파싱하여 JAVA객체로 변환
                JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody()); // JSON 객체로 변환
                JSONArray jsonArray = (JSONArray) jsonObj.get("documents");
                JSONObject position = (JSONObject)jsonArray.get(0);
                double lon = Float.parseFloat((String) position.get("x"));
                double lat = Float.parseFloat((String) position.get("y"));
                gpsMap.put("lon", lon);
                gpsMap.put("lat", lat);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            };
        } else {
            throw new ErrorException( response.getStatusCode()+"카카오 정보가 주어지지 못했습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
        }
        return gpsMap;
    }

    // 거리 구하기
    public Double getDistance(GpsDTO gps){
        double EARTH_RADIUS = 6371.0; // 지구 반지름 (km)
        // 위도 및 경도를 라디안으로 변환
        double radLat1 = Math.toRadians(gps.getLat1());
        double radLon1 = Math.toRadians(gps.getLon1());
        double radLat2 = Math.toRadians(gps.getLat2());
        double radLon2 = Math.toRadians(gps.getLon2());

        // 위도 및 경도의 차이 계산
        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        // Haversine formula 적용
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) *
                        Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리 계산 (km)
        double distance = (double) Math.round((EARTH_RADIUS * c) *100)/100;

        return distance;
    }
}

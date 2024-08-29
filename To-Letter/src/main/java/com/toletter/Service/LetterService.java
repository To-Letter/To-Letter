package com.toletter.Service;

import com.toletter.DTO.letter.GpsDTO;
import com.toletter.DTO.letter.Request.SendLetterRequest;
import com.toletter.DTO.letter.Response.ReceivedLetterResponse;
import com.toletter.DTO.letter.SaveReceivedBox;
import com.toletter.DTO.letter.SaveSentBox;
import com.toletter.Entity.*;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterRepository letterRepository;
    private final SentBoxRepository sentBoxRepository;
    private final ReceivedBoxRepository receivedBoxRepository;
    private final UserService userService;
    private final GPSService gpsService;
    private final UserRepository userRepository;

    // 메일 보내기
    public void sendLetter(SendLetterRequest sendLetterRequest, HttpServletRequest httpServletRequest){
        Letter letter = sendLetterRequest.toEntity();
        User fromUser = userService.findUserByToken(httpServletRequest); // 보내는 유저
        User toUser = userRepository.findByNickname((letter.getToUserNickname())).orElseThrow(() -> new ErrorException("유저 없음.", ErrorCode.FORBIDDEN_EXCEPTION)); // 받는 유저

        Map fromUserGPS = gpsService.getGpsUrl(fromUser.getAddress()); // 보내는 유저 위도 경도 구함
        Map toUserGPS = gpsService.getGpsUrl(toUser.getAddress()); // 받는 유저 위도 경도 구함

        GpsDTO gps = new GpsDTO();
        gps.setLat1((Double)fromUserGPS.get("lat"));
        gps.setLon1((Double)fromUserGPS.get("lon"));
        gps.setLat2((Double)toUserGPS.get("lat"));
        gps.setLon2((Double)toUserGPS.get("lon"));

        // 도착하는 시간 구함.
        LocalDateTime arrivedTime = this.getReceivedTime(gps);

        // 메일 db 저장
        letter.setFromUserNickname(fromUser.getNickname());
        letter.setArrivedAt(arrivedTime);
        letter.setViewCheck(false);
        letter.setTemporaryStorage(false);
        letterRepository.save(letter);

        // 보낸 메일함에 저장
        SaveSentBox saveBoxDTO = new SaveSentBox();
        saveBoxDTO.setFromUserNickname(fromUser.getNickname());
        saveBoxDTO.setSentTime(letter.getCreatedAt());
        saveBoxDTO.setLetter(letter);
        sentBoxRepository.save(saveBoxDTO.toEntity());

        // 받는 메일함에 저장
        SaveReceivedBox saveReceivedBox = new SaveReceivedBox();
        saveReceivedBox.setToUserNickname(toUser.getNickname());
        saveReceivedBox.setReceivedTime(arrivedTime);
        saveReceivedBox.setLetter(letter);
        receivedBoxRepository.save(saveReceivedBox.toEntity());
    }

    // 메일 받기
    public ReceivedLetterResponse receiveLetter(HttpServletRequest httpServletRequest){
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        User user = userService.findUserByToken(httpServletRequest);

        List<Letter> listBox = receivedBoxRepository.findAllByReceivedTimeBeforeAndUserNickname(now, user.getNickname());
        return ReceivedLetterResponse.res(user.getNickname(), listBox);
    }

    // 거리에 따른 메일 도착 시간
    public LocalDateTime getReceivedTime(GpsDTO gps){
        double distance = gpsService.getDistance(gps);

        LocalDateTime now = LocalDateTime.now();

        if(0 <= distance && distance < 10){
            return now.plusDays(1);
        } else if (distance < 30) {
            return now.plusDays(2);
        } else if (distance < 50) {
            return now.plusDays(3);
        } else if (distance < 80) {
            return now.plusDays(4);
        } else if (distance > 100) {
            return now.plusDays(5);
        } else {
            throw new ErrorException("400"+"거리가 나오지 않습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
        }
    }
}
